package io.nekohasekai.sagernet.ui.profile

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import io.nekohasekai.sagernet.R
import io.nekohasekai.sagernet.fmt.mandala.MandalaBean
import io.nekohasekai.sagernet.ktx.startFragment
import moe.matsuri.nb4a.proxy.PreferenceBinding
import moe.matsuri.nb4a.proxy.PreferenceBindingManager

class MandalaSettingsActivity : ProfileSettingsActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            startFragment(MandalaSettingsFragment())
        }
    }

    class MandalaSettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.mandala_preferences) // 引用上面创建的 xml
        }

        override fun onResume() {
            super.onResume()
            val activity = activity as MandalaSettingsActivity
            val bean = activity.profile.mandalaBean!! // 从 ProxyEntity 获取 bean

            val manager = PreferenceBindingManager(
                preferenceManager,
                activity.profile // 绑定到 profile
            )

            // 绑定通用字段
            manager.bind(
                PreferenceBinding.String("address", bean::serverAddress),
                PreferenceBinding.Int("port", bean::serverPort),
                PreferenceBinding.String("username", bean::username),
                PreferenceBinding.String("password", bean::password)
            )

            // 绑定 TLS 字段
            manager.bind(
                PreferenceBinding.Int(
                    "security", bean::security,
                    mapOf(
                        MandalaBean.SECURITY_NONE to "none",
                        MandalaBean.SECURITY_TLS to "tls"
                    )
                ),
                PreferenceBinding.String("sni", bean::sni),
                PreferenceBinding.Boolean("allow_insecure", bean::allowInsecure)
            )

            // 绑定 Transport 字段
            manager.bind(
                PreferenceBinding.Int(
                    "transport", bean::transport,
                    mapOf(
                        MandalaBean.TRANSPORT_TCP to "tcp",
                        MandalaBean.TRANSPORT_WS to "ws"
                    )
                ),
                PreferenceBinding.String("ws_path", bean::path),
                PreferenceBinding.String("ws_host", bean::host)
            )
        }
    }
}
