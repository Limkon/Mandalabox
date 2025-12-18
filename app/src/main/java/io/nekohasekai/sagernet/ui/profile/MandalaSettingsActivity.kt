package io.nekohasekai.sagernet.ui.profile

import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import io.nekohasekai.sagernet.fmt.mandala.MandalaBean
// [修正 1] 使用單數 'preference' 包名和正確的類名 'PreferenceBinder'
import io.nekohasekai.sagernet.ui.preference.PreferenceBinder

class MandalaSettingsActivity : ProfileSettingsActivity<MandalaBean>() {

    override fun createEntity(): MandalaBean {
        return MandalaBean()
    }

    // [修正 2] 參數類型修正為 PreferenceBinder
    override fun init(binding: PreferenceBinder) {
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
            { it.toInt() } // [修正 3] 使用 Lambda 解決 String::toInt 歧義
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
            { it.toInt() } // [修正 3] 使用 Lambda 解決 String::toInt 歧義
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
