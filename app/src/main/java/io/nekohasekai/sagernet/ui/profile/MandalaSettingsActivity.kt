package io.nekohasekai.sagernet.ui.profile

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.CheckBoxPreference
import io.nekohasekai.sagernet.R
import io.nekohasekai.sagernet.fmt.mandala.MandalaBean
import io.nekohasekai.sagernet.ui.preferences.PreferenceBindingManager

// [修正 1] 必須指定泛型 <MandalaBean>
class MandalaSettingsActivity : ProfileSettingsActivity<MandalaBean>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // [修正 2] 根據日誌錯誤 "Too many arguments"，這裡改為無參構造
        // 如果您的項目版本不同報錯，請嘗試改回 PreferenceBindingManager(this)
        val binding = PreferenceBindingManager() 

        // 綁定服務器地址
        binding.bind(
            findPreference<EditTextPreference>("server"),
            profile.serverAddress,
            { profile.serverAddress = it }
        )

        // 綁定端口 (Int 需要轉換)
        binding.bind(
            findPreference<EditTextPreference>("port"),
            profile.serverPort,
            { profile.serverPort = it },
            Int::toString,
            String::toInt
        )

        // 綁定用戶名
        binding.bind(
            findPreference<EditTextPreference>("username"),
            profile.username,
            { profile.username = it }
        )

        // 綁定密碼
        binding.bind(
            findPreference<EditTextPreference>("password"),
            profile.password,
            { profile.password = it }
        )

        // 綁定安全設置 (TLS)
        binding.bind(
            findPreference<ListPreference>("security"),
            profile.security,
            { profile.security = it },
            Int::toString,
            String::toInt
        )

        // 綁定 SNI (僅在 TLS 開啟時有效，這裡簡單綁定)
        binding.bind(
            findPreference<EditTextPreference>("sni"),
            profile.sni,
            { profile.sni = it }
        )

        // 綁定允許不安全連接
        binding.bind(
            findPreference<CheckBoxPreference>("allow_insecure"),
            profile.allowInsecure,
            { profile.allowInsecure = it }
        )
    }
}
