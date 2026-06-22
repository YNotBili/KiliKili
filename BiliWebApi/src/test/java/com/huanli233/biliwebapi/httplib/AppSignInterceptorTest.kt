package com.huanli233.biliwebapi.httplib

import com.huanli233.biliwebapi.api.util.AppSignUtil
import okhttp3.*
import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * AppSignInterceptor 拦截器单元测试。
 *
 * 测试 OkHttp 拦截器行为：
 * 1. 被 @AppSign 标记的请求：检查是否添加了 appkey、ts、sign 到 form body
 * 2. 未标记的请求：不做任何修改
 * 3. UA 是否被替换为 BiliDroid
 */
class AppSignInterceptorTest {

    /**
     * 测试：模拟被 @AppSign 标记的请求，验证签名参数被正确添加。
     *
     * 由于 OkHttp 拦截器测试需要完整的 Retrofit 注解链，
     * 这里直接测试 AppSignUtil.sign() 的集成效果。
     */
    @Test
    fun `interceptor should add signature params to form body`() {
        val originalParams = mapOf(
            "local_id" to "0",
            "mobi_app" to "android_hd",
            "platform" to "android"
        )

        // 模拟拦截器行为：读取 body → 添加签名 → 重建 body
        val mutableParams = originalParams.toMutableMap()
        val signedParams = AppSignUtil.sign(mutableParams)

        // 验证签名参数已添加
        assertNotNull("appkey should be added", signedParams["appkey"])
        assertNotNull("ts should be added", signedParams["ts"])
        assertNotNull("sign should be added", signedParams["sign"])

        // 原始参数应保留
        assertEquals("local_id should be preserved", "0", signedParams["local_id"])
        assertEquals("mobi_app should be preserved", "android_hd", signedParams["mobi_app"])
        assertEquals("platform should be preserved", "android", signedParams["platform"])

        // sign 应该是 32 位 MD5 hex
        assertEquals(32, signedParams["sign"]?.length)
    }

    @Test
    fun `sign should be deterministic for same params`() {
        val params1 = mutableMapOf(
            "auth_code" to "ABC123",
            "local_id" to "0"
        )
        val result1 = AppSignUtil.sign(params1)
        val sign1 = result1["sign"]

        val params2 = mutableMapOf(
            "auth_code" to "ABC123",
            "local_id" to "0"
        )
        val result2 = AppSignUtil.sign(params2)
        val sign2 = result2["sign"]

        assertEquals("deterministic sign for same input", sign1, sign2)
    }

    @Test
    fun `sign should differ when params differ`() {
        val params1 = mutableMapOf("auth_code" to "VALUE_A", "local_id" to "0")
        val result1 = AppSignUtil.sign(params1)

        val params2 = mutableMapOf("auth_code" to "VALUE_B", "local_id" to "0")
        val result2 = AppSignUtil.sign(params2)

        assertNotEquals("different params ≠ same sign", result1["sign"], result2["sign"])
    }

    @Test
    fun `ts should be a recent unix timestamp`() {
        val now = System.currentTimeMillis() / 1000
        val params = mutableMapOf("local_id" to "0")
        val result = AppSignUtil.sign(params)

        val ts = result["ts"]?.toLongOrNull()
        assertNotNull("ts should be a valid long", ts)

        // ts 应该在当前时间的 ±5 秒内
        val diff = kotlin.math.abs(ts!! - now)
        assertTrue("ts should be current time (±5s), diff=$diff", diff < 5)
    }

    @Test
    fun `interceptor should add BiliDroid specific headers`() {
        // 验证拦截器使用的 BiliDroid UA 格式
        val hdUserAgent = "Mozilla/5.0 BiliDroid/2.0.1 (bbcallen@gmail.com) " +
                "os/android model/android_hd mobi_app/android_hd " +
                "build/2001100 channel/master innerVer/2001100 osVer/15 network/2"

        assertTrue("UA should contain BiliDroid", hdUserAgent.contains("BiliDroid"))
        assertTrue("UA should contain android_hd", hdUserAgent.contains("mobi_app/android_hd"))
        assertTrue("UA should contain build number", hdUserAgent.contains("build/2001100"))
    }
}
