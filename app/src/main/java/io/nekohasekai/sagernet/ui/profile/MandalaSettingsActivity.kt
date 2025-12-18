package io.nekohasekai.sagernet.ui.profile

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import io.nekohasekai.sagernet.R
import io.nekohasekai.sagernet.database.preference.EditTextPreferenceModifiers
import io.nekohasekai.sagernet.fmt.mandala.MandalaBean
import moe.matsuri.nb4a.proxy.PreferenceBinding
import moe.matsuri.nb4a.proxy.PreferenceBindingManager
import moe.matsuri.nb4a.proxy.Type

class MandalaSettingsActivity : ProfileSettingsActivity<MandalaBean>() {

    override fun createEntity(): MandalaBean {
        return MandalaBean()
    }

    private val pbm = PreferenceBindingManager()

    // 綁定界面 Key 與 Bean 屬性
    private val serverAddress = pbm.add(PreferenceBinding(Type.Text, "server"))
    private val serverPort = pbm.add(PreferenceBinding(Type.TextToInt, "port"))
    private val username = pbm.add(PreferenceBinding(Type.Text, "username"))
    private val password = pbm.add(PreferenceBinding(Type.Text, "password"))
    private val security = pbm.add(PreferenceBinding(Type.TextToInt, "security"))
    private val sni = pbm.add(PreferenceBinding(Type.Text, "sni"))
    private val allowInsecure = pbm.add(PreferenceBinding(Type.Bool, "allow_insecure"))

    // 初始化：將 Bean 數據寫入緩存
    override fun MandalaBean.init() {
        pbm.writeToCacheAll(this)
    }

    // 序列化：從緩存讀取數據回填至 Bean
    override fun MandalaBean.serialize() {
        pbm.fromCacheAll(this)
    }

    override fun PreferenceFragmentCompat.createPreferences(
        savedInstanceState: Bundle?,
        rootKey: String?,
    ) {
        // 加載布局文件
        addPreferencesFromResource(R.xml.mandala_preferences)
        pbm.setPreferenceFragment(this)

        // 端口輸入限制
        serverPort.preference.apply {
            this as EditTextPreference
            setOnBindEditTextListener(EditTextPreferenceModifiers.Port)
        }

        // 密碼摘要顯示（掩碼）
        password.preference.apply {
            this as EditTextPreference
            summaryProvider = PasswordSummaryProvider
        }
    }
}
