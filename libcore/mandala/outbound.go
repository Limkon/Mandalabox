package mandala

import (
	"context"
	"fmt"
	"net"
	"os"

	"github.com/sagernet/sing-box/adapter"
	"github.com/sagernet/sing-box/common/dialer"
	"github.com/sagernet/sing-box/common/tls"
	C "github.com/sagernet/sing-box/constant"
	"github.com/sagernet/sing-box/option"
	"github.com/sagernet/sing/common/json"
	M "github.com/sagernet/sing/common/metadata"
	N "github.com/sagernet/sing/common/network"
)

// 注册 "mandala" 类型到 sing-box 系统
func init() {
	adapter.RegisterOutbound("mandala", NewOutbound)
}

// Options 定义 Android 端传来的 JSON 结构
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

	// 初始化底层 TCP 拨号器
	var err error
	outbound.dialer, err = dialer.New(ctx, options.DialerOptions, outboundOption.Tag != "")
	if err != nil {
		return nil, err
	}

	// 初始化 TLS 配置 (如果有)
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
	// 1. 连接代理服务器 TCP
	conn, err := h.dialer.DialContext(ctx, "tcp", h.serverAddr)
	if err != nil {
		return nil, err
	}

	// 2. 处理 TLS (如果启用)
	if h.tlsConfig != nil {
		conn, err = tls.ClientHandshake(ctx, conn, h.tlsConfig)
		if err != nil {
			conn.Close()
			return nil, fmt.Errorf("tls handshake failed: %w", err)
		}
	}

	// 3. 封装 Mandala 协议
	// 注意：Mandala 协议在第一次写入数据时才会发送握手包，因此我们使用 wrapper
	return &Conn{
		Conn:   conn,
		Client: h.client,
		Host:   destination.String(), // 目标地址 (Host:Port)
		Port:   destination.Port,
	}, nil
}

func (h *Outbound) ListenPacket(ctx context.Context, destination M.Socksaddr) (net.PacketConn, error) {
	// 暂不实现 UDP 支持，直接返回错误
	// Mandala 的原始实现主要针对 TCP 代理
	return nil, os.ErrInvalid
}

func (h *Outbound) InterfaceUpdateListener() func() {
	// 监听网络接口变化 (通常不需要，除非有特殊绑定逻辑)
	return nil
}

func (h *Outbound) Dependencies() []string {
	// 定义依赖的出站 (例如 chain / selector)，这里没有
	return nil
}
