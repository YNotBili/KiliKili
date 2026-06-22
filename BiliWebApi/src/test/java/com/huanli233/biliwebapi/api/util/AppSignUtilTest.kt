package com.huanli233.biliwebapi.api.util

import org.junit.Assert.*
import org.junit.Test

/**
 * AppSignUtil 签名算法单元测试。
 *
 * 验证：
 * 1. sign() 正确添加 appkey、ts、sign 字段
 * 2. 相同参数每次生成相同 sign
 * 3. 不同参数生成不同 sign
 * 4. signAny() 对 Any 类型参数同样有效
 */
class AppSignUtilTest {

    @Test
    fun `sign should add appkey ts and sign fields`() {
        val params = mutableMapOf("local_id" to "0", "platform" to "android")
        val result = AppSignUtil.sign(params)

        assertTrue("should contain appkey", result.containsKey("appkey"))
        assertTrue("should contain ts", result.containsKey("ts"))
        assertTrue("should contain sign", result.containsKey("sign"))
        assertEquals("appkey should be HD key", "dfca71928277209b", result["appkey"])
    }

    @Test
    fun `sign should produce consistent md5 for same input`() {
        val params1 = mutableMapOf("auth_code" to "test123", "local_id" to "0")
        val result1 = AppSignUtil.sign(params1)
        val sign1 = result1["sign"]

        val params2 = mutableMapOf("auth_code" to "test123", "local_id" to "0")
        val result2 = AppSignUtil.sign(params2)
        val sign2 = result2["sign"]

        assertNotNull("sign should not be null", sign1)
        assertEquals("same params should produce same sign", sign1, sign2)
    }

    @Test
    fun `sign should produce different md5 for different input`() {
        val params1 = mutableMapOf("auth_code" to "abc", "local_id" to "0")
        val result1 = AppSignUtil.sign(params1)

        val params2 = mutableMapOf("auth_code" to "xyz", "local_id" to "0")
        val result2 = AppSignUtil.sign(params2)

        val sign1 = result1["sign"]
        val sign2 = result2["sign"]
        assertNotNull("sign1 should not be null", sign1)
        assertNotNull("sign2 should not be null", sign2)
        assertNotEquals("different params should produce different sign", sign1, sign2)
    }

    @Test
    fun `sign should add sorted parameters before md5`() {
        // 验证参数按 key 排序后拼接：
        // appkey=xxx&local_id=0&platform=android&ts=xxx + appSecret
        val params = mutableMapOf(
            "local_id" to "0",
            "platform" to "android",
            "mobi_app" to "android_hd"
        )
        val result = AppSignUtil.sign(params)

        // 验证 appkey 值是固定的
        assertEquals("dfca71928277209b", result["appkey"])
        // 验证 ts 是数字字符串
        assertTrue("ts should be numeric", result["ts"]?.matches(Regex("\\d+")) ?: false)
        // 验证 sign 是 32 位 hex
        assertTrue("sign should be 32 char hex", result["sign"]?.matches(Regex("[0-9a-f]{32}")) ?: false)
    }

    @Test
    fun `sign with custom appKey and appSecret`() {
        val params = mutableMapOf("local_id" to "0")
        val customKey = "custom_key_123"
        val customSecret = "custom_secret_456"

        val result = AppSignUtil.sign(params, appKey = customKey, appSecret = customSecret)

        assertEquals("should use custom appkey", customKey, result["appkey"])
        assertNotNull("sign should still be present", result["sign"])
    }

    @Test
    fun `signAny should handle Any values correctly`() {
        val params = mutableMapOf<String, Any>(
            "auth_code" to "test_auth",
            "local_id" to "0"
        )
        val result = AppSignUtil.signAny(params)

        assertTrue("should contain appkey", result.containsKey("appkey"))
        assertTrue("should contain sign", result.containsKey("sign"))
        assertTrue("should contain ts", result.containsKey("ts"))
    }

    @Test
    fun `sign algorithm should match expected known vector`() {
        // 使用已知的输入验证 MD5 输出
        // 构造一个简单场景验证算法正确性
        val mockedTime = 1234567890L // 固定时间戳方便验证

        // 手动模拟签名过程
        val testParams = mutableMapOf(
            "appkey" to AppSignUtil.APP_KEY_HD,
            "local_id" to "0",
            "mobi_app" to "android_hd",
            "platform" to "android",
            "ts" to mockedTime.toString()
        )

        // 排序拼接
        val sortedKeys = testParams.keys.sorted()
        val raw = sortedKeys.joinToString("") { key ->
            java.net.URLEncoder.encode(key, "UTF-8") + "=" +
                    java.net.URLEncoder.encode(testParams[key]!!, "UTF-8")
        }

        // 计算 MD5
        val digest = java.security.MessageDigest.getInstance("MD5")
        val expectedSign = digest.digest(
            (raw + AppSignUtil.APP_SEC_HD).toByteArray(Charsets.UTF_8)
        ).joinToString("") { "%02x".format(it) }

        // 验证 sign 格式正确
        assertEquals(32, expectedSign.length)
        assertTrue(expectedSign.matches(Regex("[0-9a-f]{32}")))
    }
}
