package io.nekohasekai.sagernet.ui.profile

import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import io.nekohasekai.sagernet.fmt.mandala.MandalaBean
// [修正 1] 使用 NekoBox 專有的包路徑
import moe.matsuri.nb4a.ui.preference.PreferenceBinder

class MandalaSettingsActivity : ProfileSettingsActivity<MandalaBean>() {

    override fun createEntity(): MandalaBean {
        return MandalaBean()
    }

    // [修正 2] 此時 PreferenceBinder 應該能正確識別，init 方法簽名匹配
    override fun init(binding: PreferenceBinder) {
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
            { it.toInt() }
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
            { it.toInt() }
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
