package io.nekohasekai.sagernet.ui.profile

import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import io.nekohasekai.sagernet.fmt.mandala.MandalaBean
// 尝试使用单数形式的包名，如果仍报错，通常基类已经包含此引用
import io.nekohasekai.sagernet.ui.preference.PreferenceBindingManager

class MandalaSettingsActivity : ProfileSettingsActivity<MandalaBean>() {

    // [修正 1] 必须实现此方法，返回一个新的 Bean 实例
    override fun createEntity(): MandalaBean {
        return MandalaBean()
    }

    // [修正 2] 使用 initializeCallback 而不是 onCreate
    // 这样系统会将准备好的 binding 管理器传给我们，无需自己实例化
    override fun initializeCallback(binding: PreferenceBindingManager) {
        // server
        binding.bind(
            findPreference("server"), 
            bean.serverAddress, // [修正 3] 使用 'bean' 而不是 'profile'
            { bean.serverAddress = it }
        )

        // port
        binding.bind(
            findPreference("port"),
            bean.serverPort,
            { bean.serverPort = it },
            Int::toString,
            String::toInt
        )

        // username
        binding.bind(
            findPreference("username"),
            bean.username,
            { bean.username = it }
        )

        // password
        binding.bind(
            findPreference("password"),
            bean.password,
            { bean.password = it }
        )

        // security (TLS)
        binding.bind(
            findPreference("security"),
            bean.security,
            { bean.security = it },
            Int::toString,
            String::toInt
        )

        // sni
        binding.bind(
            findPreference("sni"),
            bean.sni,
            { bean.sni = it }
        )

        // allow_insecure
        binding.bind(
            findPreference("allow_insecure"),
            bean.allowInsecure,
            { bean.allowInsecure = it }
        )
    }
}
