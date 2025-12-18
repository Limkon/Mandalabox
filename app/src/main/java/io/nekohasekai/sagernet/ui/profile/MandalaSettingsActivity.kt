package io.nekohasekai.sagernet.ui.profile

import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import io.nekohasekai.sagernet.fmt.mandala.MandalaBean
// [修正 1] 包名修正為 preferences (複數)
import io.nekohasekai.sagernet.ui.preferences.PreferenceBindingManager

class MandalaSettingsActivity : ProfileSettingsActivity<MandalaBean>() {

    override fun createEntity(): MandalaBean {
        return MandalaBean()
    }

    // [修正 2] 方法名修正為 init，這才是基類要求的抽象方法
    override fun init(binding: PreferenceBindingManager) {
        // server
        binding.bind(
            findPreference("server"), 
            bean.serverAddress, 
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
