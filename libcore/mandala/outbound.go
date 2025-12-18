package mandala

import (
	"context"
	"fmt"
	"net"
	"os"

	"github.com/sagernet/sing-box/adapter"
	"github.com/sagernet/sing-box/common/dialer"
	"github.com/sagernet/sing-box/common/tls"
	"github.com/sagernet/sing-box/option"
	"github.com/sagernet/sing/common/buf"
	"github.com/sagernet/sing/common/json"
	M "github.com/sagernet/sing/common/metadata"
	N "github.com/sagernet/sing/common/network"
	"github.com/sagernet/sing/common/rw"
)

func init() {
	adapter.RegisterOutbound("mandala", NewOutbound)
}

// Options 定义 Android 端传来的配置结构
type Options struct {
	option.OutboundCommonOptions
	Server   string                     `json:"server"`
	Port     uint16                     `json:"port"`
	Username string                     `json:"username"`
	Password string                     `json:"password"`
	TLS      *option.OutboundTLSOptions `json:"tls,omitempty"`
}

type Outbound struct {
	myTag      string
	ctx        context.Context
	dialer     N.Dialer
	serverAddr M.Socksaddr
	client     *Client
	tlsConfig  *tls.Config
}

func NewOutbound(ctx context.Context, outboundOption option.Outbound) (adapter.Outbound, error) {
	var options Options
	if err := json.Unmarshal(outboundOption.Options, &options); err != nil {
		return nil, err
	}

	outbound := &Outbound{
		myTag: outboundOption.Tag,
		ctx:   ctx,
		serverAddr: M.Socksaddr{
			Fqdn: options.Server,
			Port: options.Port,
		},
		client: NewClient(options.Username, options.Password),
	}

	var err error
	outbound.dialer, err = dialer.New(ctx, options.DialerOptions, outboundOption.Tag != "")
	if err != nil {
		return nil, err
	}

	if options.TLS != nil {
		outbound.tlsConfig, err = tls.NewClient(ctx, options.Server, options.TLS)
		if err != nil {
			return nil, err
		}
	}

	return outbound, nil
}

func (h *Outbound) Type() string {
	return "mandala"
}

func (h *Outbound) Tag() string {
	return h.myTag
}

func (h *Outbound) DialContext(ctx context.Context, network string, destination M.Socksaddr) (net.Conn, error) {
	// 1. 连接代理服务器
	conn, err := h.dialer.DialContext(ctx, "tcp", h.serverAddr)
	if err != nil {
		return nil, err
	}

	// 2. TLS 握手 (如果启用)
	if h.tlsConfig != nil {
		conn, err = tls.ClientHandshake(ctx, conn, h.tlsConfig)
		if err != nil {
			conn.Close()
			return nil, fmt.Errorf("tls handshake failed: %w", err)
		}
	}

	// 3. 返回封装后的连接，处理 Mandala 握手
	return &Conn{
		Conn:   conn,
		Client: h.client,
		Host:   destination.String(),
		Port:   destination.Port,
	}, nil
}

func (h *Outbound) ListenPacket(ctx context.Context, destination M.Socksaddr) (net.PacketConn, error) {
	return nil, os.ErrInvalid
}

func (h *Outbound) InterfaceUpdateListener() func() {
	return nil
}

func (h *Outbound) Dependencies() []string {
	return nil
}

// Conn 封装 net.Conn，用于在第一次写入时发送握手包
type Conn struct {
	net.Conn
	Client     *Client
	Host       string
	Port       uint16
	handshaked bool
}

func (c *Conn) handshake() error {
	if c.handshaked {
		return nil
	}
	// 使用纯 Go 的 protocol 逻辑构建握手包
	payload, err := c.Client.BuildHandshakePayload(c.Host, int(c.Port))
	if err != nil {
		return err
	}
	_, err = c.Conn.Write(payload)
	if err == nil {
		c.handshaked = true
	}
	return err
}

func (c *Conn) Write(b []byte) (n int, err error) {
	if err := c.handshake(); err != nil {
		return 0, err
	}
	return c.Conn.Write(b)
}

// WriteBuffer 实现 sing-box 的 LinkWriter 接口，提高性能
func (c *Conn) WriteBuffer(buffer *buf.Buffer) error {
	if err := c.handshake(); err != nil {
		return err
	}
	return rw.WriteBuffer(c.Conn, buffer)
}

// ReaderFrom 实现 io.ReaderFrom
func (c *Conn) ReadFrom(r io.Reader) (n int64, err error) {
	if err := c.handshake(); err != nil {
		return 0, err
	}
	return c.Conn.(io.ReaderFrom).ReadFrom(r)
}
