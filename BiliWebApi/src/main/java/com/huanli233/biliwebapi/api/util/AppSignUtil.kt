package com.huanli233.biliwebapi.api.util

import java.net.URLEncoder
import java.security.MessageDigest
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

object AppSignUtil {

    /** android_hd appKey */
    const val APP_KEY_HD = "dfca71928277209b"
    /** TV/HD appSecret */
    const val APP_SEC_HD = "b5475a8825547a4fc26c7d518eaaa02e"

    /**
     * 对参数列表进行 app 签名。
     * 1. 添加 appkey + ts
     * 2. 按键名排序
     * 3. URL-encode 拼接
     * 4. 末尾追加 appSecret 后计算 MD5
     * 5. 添加 sign
     *
     * @return 原地修改后的 params map
     */
    @OptIn(ExperimentalTime::class)
    fun sign(
        params: MutableMap<String, String>,
        appKey: String = APP_KEY_HD,
        appSecret: String = APP_SEC_HD,
    ): Map<String, String> {
        params["appkey"] = appKey
        params["ts"] = (Clock.System.now().epochSeconds).toString()

        val sortedKeys = params.keys.sorted()
        val raw = sortedKeys.joinToString("&") { key ->
            URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(params[key]!!, "UTF-8")
        }

        params["sign"] = md5(raw + appSecret)
        return params
    }

    /**
     * 对参数列表进行 app 签名（支持任意 value 类型）。
     */
    fun signAny(
        params: MutableMap<String, Any>,
        appKey: String = APP_KEY_HD,
        appSecret: String = APP_SEC_HD,
    ): Map<String, Any> {
        params["appkey"] = appKey
        params["ts"] = (System.currentTimeMillis() / 1000).toString()

        val sortedKeys = params.keys.sorted()
        val raw = sortedKeys.joinToString("&") { key ->
            URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(params[key]!!.toString(), "UTF-8")
        }

        params["sign"] = md5(raw + appSecret)
        return params
    }

    private fun md5(input: String): String {
        val digest = MessageDigest.getInstance("MD5")
        val bytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
