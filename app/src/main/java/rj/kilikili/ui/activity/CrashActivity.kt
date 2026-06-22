package rj.kilikili.ui.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.color.MaterialColors
import com.google.android.material.textview.MaterialTextView
import rj.kilikili.BuildConfig
import java.io.StringWriter
import java.io.PrintWriter

class CrashActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_THROWABLE = "throwable"

        fun createIntent(context: Context, throwable: Throwable): Intent {
            return Intent(context, CrashActivity::class.java).apply {
                putExtra(EXTRA_THROWABLE, throwable)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
        }
    }

    private var restartApp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val throwable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_THROWABLE, Throwable::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(EXTRA_THROWABLE) as? Throwable
        }

        setContentView(buildRootView(throwable))
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!restartApp) {
            Process.killProcess(Process.myPid())
        }
    }

    private fun buildRootView(throwable: Throwable?): ScrollView {
        val scrollView = ScrollView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            clipToPadding = false
        }

        val root = LinearLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }
        scrollView.addView(root)

        // ── Header ──
        root.addView(buildHeader())
        root.addView(spacer(12))

        // ── Error card ──
        root.addView(buildErrorCard(throwable))
        root.addView(spacer(12))

        // ── Device info card ──
        root.addView(buildDeviceInfoCard())
        root.addView(spacer(12))

        // ── Actions ──
        root.addView(buildActions(throwable))

        return scrollView
    }

    private fun buildHeader(): MaterialTextView {
        return MaterialTextView(this, null, 0, com.google.android.material.R.style.TextAppearance_Material3_HeadlineMedium).apply {
            text = "应用崩溃了"
            setTextColor(getColorAttr(com.google.android.material.R.attr.colorOnSurface))
            gravity = Gravity.CENTER
        }
    }

    private fun buildErrorCard(throwable: Throwable?): MaterialCardView {
        return MaterialCardView(this, null, com.google.android.material.R.attr.materialCardViewOutlinedStyle).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            radius = dp(12).toFloat()

            val cardContent = LinearLayout(this@CrashActivity).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.VERTICAL
                setPadding(dp(16), dp(16), dp(16), dp(16))
            }

            // Error type label
            if (throwable != null) {
                cardContent.addView(MaterialTextView(this@CrashActivity, null, 0,
                    com.google.android.material.R.style.TextAppearance_Material3_TitleMedium).apply {
                    text = throwable.javaClass.simpleName
                    setTextColor(getColorAttr(com.google.android.material.R.attr.colorError))
                })
                cardContent.addView(spacer(8))

                // Error message
                cardContent.addView(MaterialTextView(this@CrashActivity, null, 0,
                    com.google.android.material.R.style.TextAppearance_Material3_BodyMedium).apply {
                    text = throwable.message ?: "(无错误信息)"
                    setTextColor(getColorAttr(com.google.android.material.R.attr.colorOnSurface))
                })
                cardContent.addView(spacer(8))

                // Stack trace (scrollable within card)
                cardContent.addView(buildStackTraceView(throwable))
            } else {
                cardContent.addView(MaterialTextView(this@CrashActivity, null, 0,
                    com.google.android.material.R.style.TextAppearance_Material3_BodyLarge).apply {
                    text = "未知错误"
                    setTextColor(getColorAttr(com.google.android.material.R.attr.colorOnSurface))
                })
            }

            addView(cardContent)
        }
    }

    private fun buildStackTraceView(throwable: Throwable): MaterialTextView {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        throwable.printStackTrace(pw)
        pw.flush()
        val stackTrace = sw.toString()

        return MaterialTextView(this, null, 0,
            com.google.android.material.R.style.TextAppearance_Material3_BodySmall).apply {
            text = stackTrace
            setTextColor(getColorAttr(com.google.android.material.R.attr.colorOnSurfaceVariant))
            setLineSpacing(dp(2).toFloat(), 1f)
            maxLines = 50
        }
    }

    private fun buildDeviceInfoCard(): MaterialCardView {
        return MaterialCardView(this, null, com.google.android.material.R.attr.materialCardViewOutlinedStyle).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            radius = dp(12).toFloat()

            val cardContent = LinearLayout(this@CrashActivity).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.VERTICAL
                setPadding(dp(16), dp(16), dp(16), dp(16))
            }

            cardContent.addView(MaterialTextView(this@CrashActivity, null, 0,
                com.google.android.material.R.style.TextAppearance_Material3_TitleSmall).apply {
                text = "设备信息"
                setTextColor(getColorAttr(com.google.android.material.R.attr.colorOnSurface))
            })
            cardContent.addView(spacer(8))

            val infoItems = listOf(
                "设备" to "${Build.MANUFACTURER} ${Build.MODEL}",
                "系统" to "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
                "版本" to "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                "ABI" to Build.SUPPORTED_ABIS.joinToString(", "),
            )

            for ((label, value) in infoItems) {
                cardContent.addView(buildInfoRow(label, value))
            }

            addView(cardContent)
        }
    }

    private fun buildInfoRow(label: String, value: String): LinearLayout {
        return LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.bottomMargin = dp(4) }
            orientation = LinearLayout.HORIZONTAL

            addView(MaterialTextView(this@CrashActivity, null, 0,
                com.google.android.material.R.style.TextAppearance_Material3_BodySmall).apply {
                text = "$label: "
                setTextColor(getColorAttr(com.google.android.material.R.attr.colorOnSurfaceVariant))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            })

            addView(MaterialTextView(this@CrashActivity, null, 0,
                com.google.android.material.R.style.TextAppearance_Material3_BodySmall).apply {
                text = value
                setTextColor(getColorAttr(com.google.android.material.R.attr.colorOnSurface))
            })
        }
    }

    private fun buildActions(throwable: Throwable?): LinearLayout {
        return LinearLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL

            // Row 1: Copy + Share
            val row1 = LinearLayout(this@CrashActivity).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.HORIZONTAL
            }

            row1.addView(MaterialButton(this@CrashActivity, null,
                com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
                layoutParams = LinearLayout.LayoutParams(0, dp(48), 1f).also {
                    it.rightMargin = dp(8)
                }
                text = "复制错误"
                setOnClickListener { copyCrashInfo(throwable) }
            })

            row1.addView(MaterialButton(this@CrashActivity, null,
                com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
                layoutParams = LinearLayout.LayoutParams(0, dp(48), 1f).also {
                    it.leftMargin = dp(8)
                }
                text = "分享"
                setOnClickListener { shareCrashInfo(throwable) }
            })

            addView(row1)
            addView(spacer(8))

            // Row 2: Restart
            addView(MaterialButton(this@CrashActivity, null,
                com.google.android.material.R.attr.materialButtonStyle).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(48)
                )
                text = "重启应用"
                setOnClickListener { restartApp() }
            })
        }
    }

    private fun copyCrashInfo(throwable: Throwable?) {
        val text = buildCrashReport(throwable)
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Crash Report", text))
        Toast.makeText(this, "已复制到剪贴板", Toast.LENGTH_SHORT).show()
    }

    private fun shareCrashInfo(throwable: Throwable?) {
        val text = buildCrashReport(throwable)
        startActivity(Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }, "分享崩溃信息"))
    }

    private fun restartApp() {
        restartApp = true
        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        if (intent != null) {
            startActivity(intent)
        }
        finishAffinity()
    }

    private fun buildCrashReport(throwable: Throwable?): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        pw.println("=== 崩溃报告 ===")
        pw.println()

        if (throwable != null) {
            pw.println("--- 错误信息 ---")
            pw.println("类型: ${throwable.javaClass.name}")
            pw.println("消息: ${throwable.message ?: "(无)"}")
            pw.println()
            pw.println("--- 堆栈跟踪 ---")
            throwable.printStackTrace(pw)
            pw.println()
        }

        pw.println("--- 设备信息 ---")
        pw.println("设备: ${Build.MANUFACTURER} ${Build.MODEL}")
        pw.println("系统: Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
        pw.println("版本: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
        pw.println("ABI: ${Build.SUPPORTED_ABIS.joinToString(", ")}")
        pw.flush()
        return sw.toString()
    }

    private fun getColorAttr(attr: Int): Int {
        return MaterialColors.getColor(this, attr, "CrashActivity")
    }

    private fun dp(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    private fun spacer(heightDp: Int): View {
        return View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(heightDp)
            )
        }
    }
}
