package com.vanta.lockrecovery

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {
    private val ink = 0xFF172033.toInt()
    private val muted = 0xFF5B6475.toInt()
    private val accent = 0xFF2457C5.toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val scroll = ScrollView(this)
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(28), dp(20), dp(28))
            layoutDirection = LinearLayout.LAYOUT_DIRECTION_RTL
        }
        scroll.addView(page)
        setContentView(scroll)

        page.addView(text("استعادة قفل Android", 26, true))
        page.addView(text("فحص الجهاز وخيارات الاستعادة التي تحافظ على البيانات", 15, false).apply {
            setTextColor(muted)
        })
        page.addView(gap(18))

        val manufacturer = Build.MANUFACTURER.orEmpty().replaceFirstChar { it.uppercase() }
        val model = Build.MODEL.orEmpty()
        val androidVersion = Build.VERSION.RELEASE.orEmpty()
        val sdk = Build.VERSION.SDK_INT
        val details = "الشركة المصنّعة: $manufacturer\n" +
            "الطراز: $model\n" +
            "إصدار Android: $androidVersion\n" +
            "مستوى API: $sdk\n" +
            "اسم الجهاز: ${Build.DEVICE}"
        page.addView(panel(details))
        page.addView(gap(16))
        page.addView(text("خيارات الاستعادة", 20, true))
        page.addView(gap(8))

        page.addView(actionButton("1. التحقق من النسخ الاحتياطية") {
            openUrl("https://support.google.com/android/answer/2819582")
        })
        page.addView(actionButton("2. تعليمات الشركة المصنّعة") {
            openManufacturerHelp(manufacturer)
        })
        page.addView(actionButton("3. مساعدة حساب Google") {
            openUrl("https://accounts.google.com/signin/recovery")
        })
        page.addView(actionButton("4. إرشادات العثور على جهاز Android") {
            openUrl("https://android.com/find")
        })

        page.addView(gap(16))
        page.addView(text("مهم قبل أي خطوة", 18, true))
        page.addView(panel(
            "• لا ينفّذ هذا التطبيق فتح القفل أو تخمين PIN.\n" +
            "• لا يطلب رمز القفل أو كلمة مرور حسابك.\n" +
            "• لا يمسح البيانات ولا يعيد ضبط المصنع.\n" +
            "• لا يمكن لتطبيق عادي إزالة قفل النظام أو فك تشفير الملفات المحمية.\n" +
            "• تجنّب إعادة ضبط المصنع إذا كانت البيانات غير منسوخة احتياطيًا."
        ))
        page.addView(gap(12))
        page.addView(text(
            "تطبيق محلي لا يحتاج إلى اتصال بالإنترنت لعرض معلومات الجهاز. تفتح الأزرار صفحات الدعم في المتصفح عند اختيارها.",
            13, false
        ).apply { setTextColor(muted) })
    }

    private fun openManufacturerHelp(manufacturer: String) {
        val url = when {
            manufacturer.contains("samsung", true) -> "https://www.samsung.com/support/"
            manufacturer.contains("xiaomi", true) || manufacturer.contains("redmi", true) ||
                manufacturer.contains("poco", true) -> "https://www.mi.com/global/support/"
            manufacturer.contains("huawei", true) -> "https://consumer.huawei.com/en/support/"
            manufacturer.contains("google", true) || manufacturer.contains("pixel", true) ->
                "https://support.google.com/pixelphone/"
            manufacturer.contains("oppo", true) -> "https://support.oppo.com/"
            manufacturer.contains("vivo", true) -> "https://www.vivo.com/en/support"
            else -> "https://support.google.com/android/"
        }
        openUrl(url)
    }

    private fun openUrl(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (_: Exception) {
            // Leave the app open if no browser is available.
        }
    }

    private fun actionButton(label: String, action: () -> Unit): Button {
        return Button(this).apply {
            text = label
            isAllCaps = false
            textSize = 15f
            setTextColor(0xFFFFFFFF.toInt())
            setBackgroundColor(accent)
            setOnClickListener { action() }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(8) }
        }
    }

    private fun panel(value: String): TextView = text(value, 15, false).apply {
        setTextColor(ink)
        setPadding(dp(14), dp(14), dp(14), dp(14))
        setBackgroundColor(0xFFF0F3F8.toInt())
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun text(value: String, size: Int, bold: Boolean): TextView =
        TextView(this).apply {
            text = value
            textSize = size.toFloat()
            setTextColor(ink)
            gravity = Gravity.START
            if (bold) setTypeface(typeface, Typeface.BOLD)
            setPadding(0, dp(4), 0, dp(4))
        }

    private fun gap(height: Int) = android.view.View(this).apply {
        layoutParams = LinearLayout.LayoutParams(1, dp(height))
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
