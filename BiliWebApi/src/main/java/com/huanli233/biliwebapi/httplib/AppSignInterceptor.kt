package com.huanli233.biliwebapi.httplib

import com.huanli233.biliwebapi.api.util.AppSignUtil
import com.huanli233.biliwebapi.httplib.annotation.AppSign
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import retrofit2.Invocation

/**
 * 拦截器：为标记了 @AppSign 的 API 方法自动添加 app 签名。
 *
 * 处理流程：
 * 1. 读取请求的 form body 参数
 * 2. 追加 appkey + ts
 * 3. 计算 sign = MD5(排序拼接 + appSecret)
 * 4. 重建 form body
 * 5. 替换 User-Agent 为 BiliDroid HD 风格
 */
class AppSignInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val invocation = request.tag(Invocation::class.java) ?: return chain.proceed(request)

        val appSign = invocation.method().getAnnotation(AppSign::class.java) ?: return chain.proceed(request)

        val body = request.body
        if (body !is FormBody) return chain.proceed(request)

        // 读取现有 form 参数
        val params = mutableMapOf<String, String>()
        for (i in 0 until body.size) {
            params[body.name(i)] = body.value(i)
        }

        // 添加 app 签名
        AppSignUtil.sign(params, appSign.appkey, appSign.appsecret)

        // 重建 form body
        val newBody = FormBody.Builder()
        params.forEach { (key, value) ->
            newBody.add(key, value)
        }

        // 替换 UA 为 BiliDroid HD 风格
        val hdUserAgent = "Mozilla/5.0 BiliDroid/2.0.1 (bbcallen@gmail.com) " +
                "os/android model/android_hd mobi_app/android_hd " +
                "build/2001100 channel/master innerVer/2001100 osVer/15 network/2"

        val newRequest = request.newBuilder()
            .header(HeaderNames.USER_AGENT, hdUserAgent)
            .header(HeaderNames.REFERER, "https://www.bilibili.com")
            .header(HeaderNames.ORIGIN, "https://www.bilibili.com")
            .removeHeader(HeaderNames.SEC_CH_UA)
            .removeHeader(HeaderNames.SEC_CH_UA_PLATFORM)
            .removeHeader(HeaderNames.SEC_CH_UA_MOBILE)
            .method(request.method, newBody.build())
            .build()

        return chain.proceed(newRequest)
    }
}
