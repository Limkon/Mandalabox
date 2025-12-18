package mandala

import (
	"bytes"
	"crypto/rand"
	"crypto/sha256"
	"encoding/binary"
	"encoding/hex"
	"errors"
	"io"
	"net"

	"github.com/sagernet/sing-box/common/buf"
	"github.com/sagernet/sing/common/rw"
)

// Client 包含 Mandala 协议的配置信息
type Client struct {
	Username string
	Password string
}

// NewClient 创建一个新的 Mandala 客户端实例
func NewClient(username, password string) *Client {
	return &Client{
		Username: username,
		Password: password,
	}
}

// WriteHandshake 向 writer 写入 Mandala 协议握手包
func (c *Client) WriteHandshake(writer io.Writer, destination string, port uint16) error {
	payload, err := c.buildHandshakePayload(destination, int(port))
	if err != nil {
		return err
	}
	_, err = writer.Write(payload)
	return err
}

// buildHandshakePayload 构造 Mandala 协议的握手包
// 协议结构: Salt(4) + XOR( Hash(56) + PadLen(1) + Padding(N) + Cmd(1) + AddrType(1) + Addr... + Port(2) + CRLF(2) )
func (c *Client) buildHandshakePayload(targetHost string, targetPort int) ([]byte, error) {
	// 1. 生成随机 Salt (4 bytes)
	salt := make([]byte, 4)
	if _, err := io.ReadFull(rand.Reader, salt); err != nil {
		return nil, err
	}

	// 2. 准备明文 Payload (临时缓冲区)
	buffer := buf.New()
	defer buffer.Release()

	// 2.1 哈希 ID (SHA224 Hex String, 56 bytes)
	hash := sha256.Sum224([]byte(c.Password))
	hashHex := hex.EncodeToString(hash[:]) // 28 bytes binary -> 56 bytes hex string
	if len(hashHex) != 56 {
		return nil, errors.New("hash generation failed")
	}
	buffer.WriteString(hashHex)

	// 2.2 随机填充 (Padding)
	padLenByte := make([]byte, 1)
	if _, err := io.ReadFull(rand.Reader, padLenByte); err != nil {
		return nil, err
	}
	padLen := int(padLenByte[0] % 16)

	buffer.WriteByte(byte(padLen)) // PadLen (1 byte)

	if padLen > 0 {
		padding := make([]byte, padLen)
		if _, err := io.ReadFull(rand.Reader, padding); err != nil {
			return nil, err
		}
		buffer.Write(padding) // Padding (N bytes)
	}

	// 2.3 指令 CMD (0x01 Connect)
	buffer.WriteByte(0x01)

	// 2.4 目标地址 (SOCKS5 格式)
	ip := net.ParseIP(targetHost)
	if ip != nil {
		if ip4 := ip.To4(); ip4 != nil {
			// IPv4: 0x01 + 4 bytes IP
			buffer.WriteByte(0x01)
			buffer.Write(ip4)
		} else {
			// IPv6: 0x04 + 16 bytes IP
			buffer.WriteByte(0x04)
			buffer.Write(ip.To16())
		}
	} else {
		// Domain: 0x03 + Len(1) + DomainString
		if len(targetHost) > 255 {
			return nil, errors.New("domain too long")
		}
		buffer.WriteByte(0x03)
		buffer.WriteByte(byte(len(targetHost)))
		buffer.WriteString(targetHost)
	}

	// 2.5 端口 (2 bytes Big Endian)
	portBuf := make([]byte, 2)
	binary.BigEndian.PutUint16(portBuf, uint16(targetPort))
	buffer.Write(portBuf)

	// 2.6 CRLF (0x0D 0x0A)
	buffer.Write([]byte{0x0D, 0x0A})

	// 3. 构造最终包 (Salt + XOR Encrypted Payload)
	plaintext := buffer.Bytes()
	finalSize := 4 + len(plaintext)
	finalBuf := make([]byte, finalSize)

	// 写入 Salt 到头部
	copy(finalBuf[0:4], salt)

	// 执行 XOR 加密: Cipher[i] = Plain[i] ^ Salt[i % 4]
	for i := 0; i < len(plaintext); i++ {
		finalBuf[4+i] = plaintext[i] ^ salt[i%4]
	}

	return finalBuf, nil
}

// Conn 封装 net.Conn，仅用于在连接建立时写入握手包
// 握手后，它表现得像普通的 net.Conn
type Conn struct {
	net.Conn
	Client *Client
	Host   string
	Port   uint16
	handshaked bool
}

func (c *Conn) Write(b []byte) (n int, err error) {
	if !c.handshaked {
		err = c.Client.WriteHandshake(c.Conn, c.Host, c.Port)
		if err != nil {
			return 0, err
		}
		c.handshaked = true
	}
	return c.Conn.Write(b)
}

func (c *Conn) WriteBuffer(buffer *buf.Buffer) error {
	if !c.handshaked {
		err := c.Client.WriteHandshake(c.Conn, c.Host, c.Port)
		if err != nil {
			return err
		}
		c.handshaked = true
	}
	return rw.WriteBuffer(c.Conn, buffer)
}
