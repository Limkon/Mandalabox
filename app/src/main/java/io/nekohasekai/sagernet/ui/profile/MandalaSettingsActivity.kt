package io.nekohasekai.sagernet.ui.profile

import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import io.nekohasekai.sagernet.fmt.mandala.MandalaBean
// [修正 1] 包名修正為 preferences (複數)
import io.nekohasekai.sagernet.ui.preferences.PreferenceBinder

class MandalaSettingsActivity : ProfileSettingsActivity<MandalaBean>() {

    override fun createEntity(): MandalaBean {
        return MandalaBean()
    }

    override fun init(binding: PreferenceBinder) {
        // server
        binding.bind(
            findPreference("server"), 
            profile.serverAddress, // [修正 2] 變量名改回 profile
            { profile.serverAddress = it }
        )

        // port
        binding.bind(
            findPreference("port"),
            profile.serverPort,
            { profile.serverPort = it },
            Int::toString,
            { it.toInt() }
        )

        // username
        binding.bind(
            findPreference("username"),
            profile.username,
            { profile.username = it }
        )

        // password
        binding.bind(
            findPreference("password"),
            profile.password,
            { profile.password = it }
        )

        // security (TLS)
        binding.bind(
            findPreference("security"),
            profile.security,
            { profile.security = it },
            Int::toString,
            { it.toInt() }
        )

        // sni
        binding.bind(
            findPreference("sni"),
            profile.sni,
            { profile.sni = it }
        )

        // allow_insecure
        binding.bind(
            findPreference("allow_insecure"),
            profile.allowInsecure,
            { profile.allowInsecure = it }
        )
    }
}
