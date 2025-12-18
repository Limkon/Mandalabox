package io.nekohasekai.sagernet.ui.profile

import android.os.Bundle
import androidx.preference.Preference
import io.nekohasekai.sagernet.fmt.mandala.MandalaBean
// [修正1] 尝试使用 NekoBox 的 Binder 包路径 (如果报错，请尝试删除此行让 IDE 自动导入)
import moe.matsuri.nb4a.ui.PreferenceBinder 

class MandalaSettingsActivity : ProfileSettingsActivity<MandalaBean>() {

    override fun createEntity(): MandalaBean {
        return MandalaBean()
    }

    // [修正2] 使用 'data' 作为变量名 (NekoBox 基类通常命名为 data)
    override fun init(binding: PreferenceBinder) {
        // server
        val serverPref = findPreference<Preference>("server")
        if (serverPref != null) {
            binding.bind(
                serverPref, 
                data.serverAddress, 
                { data.serverAddress = it }
            )
        }

        // port
        val portPref = findPreference<Preference>("port")
        if (portPref != null) {
            binding.bind(
                portPref,
                data.serverPort,
                { data.serverPort = it },
                Int::toString,
                { it.toInt() }
            )
        }

        // username
        val userPref = findPreference<Preference>("username")
        if (userPref != null) {
            binding.bind(
                userPref,
                data.username,
                { data.username = it }
            )
        }

        // password
        val passPref = findPreference<Preference>("password")
        if (passPref != null) {
            binding.bind(
                passPref,
                data.password,
                { data.password = it }
            )
        }

        // security (TLS)
        val secPref = findPreference<Preference>("security")
        if (secPref != null) {
            binding.bind(
                secPref,
                data.security,
                { data.security = it },
                Int::toString,
                { it.toInt() }
            )
        }

        // sni
        val sniPref = findPreference<Preference>("sni")
        if (sniPref != null) {
            binding.bind(
                sniPref,
                data.sni,
                { data.sni = it }
            )
        }

        // allow_insecure
        val insecPref = findPreference<Preference>("allow_insecure")
        if (insecPref != null) {
            binding.bind(
                insecPref,
                data.allowInsecure,
                { data.allowInsecure = it }
            )
        }
    }
}
