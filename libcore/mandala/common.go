package mandala

import (
	"crypto/sha256"
	"encoding/hex"
)

// TrojanPasswordHash 计算密码的 SHA224 哈希并返回 Hex 字符串
// Mandala 协议使用这个生成的 56 字节 Hex 字符串作为认证凭证
func TrojanPasswordHash(password string) string {
	hash := sha256.Sum224([]byte(password))
	return hex.EncodeToString(hash[:])
}
