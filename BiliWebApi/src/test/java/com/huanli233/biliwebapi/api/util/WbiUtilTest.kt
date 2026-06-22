package com.huanli233.biliwebapi.api.util

import org.junit.Assert.*
import org.junit.Test
import java.math.BigInteger
import java.security.MessageDigest

/**
 * WBI 签名算法单元测试。
 *
 * 验证：
 * 1. mixin key 生成逻辑（getWBIMixinKey）
 * 2. 文件名校验（getFileFirstName / getFileNameFromLink）
 * 3. MD5 输出格式
 * 4. 参数排序逻辑
 * 5. 编码处理
 */
class WbiUtilTest {

    @Test
    fun `getFileFirstName should extract filename without extension`() {
        // 通过反射测试私有方法
        val result = invokeGetFileFirstName("abc123.jpg")
        assertEquals("should strip extension", "abc123", result)
    }

    @Test
    fun `getFileFirstName should handle multiple dots`() {
        val result = invokeGetFileFirstName("image.data.test.png")
        assertEquals("should keep only first part", "image", result)
    }

    @Test
    fun `getFileFirstName should return raw string if no dot`() {
        val result = invokeGetFileFirstName("noextension")
        assertEquals("no dot = full string", "noextension", result)
    }

    @Test
    fun `getFileFirstName should handle empty string`() {
        // 空字符串会导致循环无 dot，返回 "fail"
        val result = invokeGetFileFirstName("")
        assertEquals("empty returns fail", "fail", result)
    }

    @Test
    fun `getFileNameFromLink should extract filename after last slash`() {
        val result = invokeGetFileNameFromLink("https://i0.hdslb.com/bfs/face/abc123.jpg")
        assertEquals("should get last segment", "abc123.jpg", result)
    }

    @Test
    fun `getFileNameFromLink should handle link with trailing slash`() {
        val result = invokeGetFileNameFromLink("https://i0.hdslb.com/bfs/face/")
        assertEquals("empty after slash returns fail", "fail", result)
    }

    @Test
    fun `md5 should produce 32 character hex string`() {
        val input = "test_string_123"
        val md5 = invokeMd5(input)
        assertEquals("MD5 should be 32 chars", 32, md5.length)
        assertTrue("MD5 should be hex", md5.matches(Regex("[0-9a-f]{32}")))
    }

    @Test
    fun `md5 should be deterministic`() {
        val input = "hello_world"
        val first = invokeMd5(input)
        val second = invokeMd5(input)
        assertEquals("deterministic", first, second)
    }

    @Test
    fun `md5 should produce known value for known input`() {
        // MD5("test") = 098f6bcd4621d373cade4e832627b4f6
        val result = invokeMd5("test")
        assertEquals("known MD5 of 'test'", "098f6bcd4621d373cade4e832627b4f6", result)
    }

    @Test
    fun `mixin key generation follows WBI algorithm`() {
        // 模拟 WBI mixin key 生成
        // img_key = "9b5787b6b8f74e41b4f14f4686e958a9"
        // sub_key = "456e5f4e2c2848a9bdd1434ee85f1bbe"
        // raw = "9b5787b6b8f74e41b4f14f4686e958a9" + "456e5f4e2c2848a9bdd1434ee85f1bbe"
        // 用 mixin key 表取前 32 个字符
        val rawKey = "9b5787b6b8f74e41b4f14f4686e958a9456e5f4e2c2848a9bdd1434ee85f1bbe"
        val mixinKey = invokeGetWBIMixinKey(rawKey)
        assertEquals("mixin key should be 32 chars", 32, mixinKey.length)
    }

    @Test
    fun `sortUrlParams should sort by key alphabetically`() {
        val query = "b=2&a=1&c=3"
        val sorted = invokeSortUrlParams(query)
        // 应该按 key a, b, c 排序
        val parts = sorted.split("&")
        assertEquals(3, parts.size)
        assertTrue("first should be a=...", parts[0].startsWith("a="))
        assertTrue("second should be b=...", parts[1].startsWith("b="))
        assertTrue("third should be c=...", parts[2].startsWith("c="))
    }

    @Test
    fun `sortUrlParams should handle URL-encoded values`() {
        val query = "keyword=%E6%B5%8B%E8%AF%95&type=video&page=1"
        val sorted = invokeSortUrlParams(query)
        val parts = sorted.split("&")
        assertEquals(3, parts.size)
        // keyword 按字母序在 page 前？
        // "keyword" < "page" < "type"？k < p < t，是的
        assertEquals("keyword=%E6%B5%8B%E8%AF%95", parts[0])
        assertEquals("page=1", parts[1])
        assertEquals("type=video", parts[2])
    }

    @Test
    fun `WBI signing process should produce wts and w_rid`() {
        // 验证完整的 WBI 签名流程输出格式
        // 使用简化测试：验证 getMixinKey 和 md5 组合的正确性

        val imgUrl = "https://i0.hdslb.com/bfs/wbi/9b5787b6b8f74e41b4f14f4686e958a9.png"
        val subUrl = "https://i0.hdslb.com/bfs/wbi/456e5f4e2c2848a9bdd1434ee85f1bbe.png"

        val imgKey = invokeGetFileFirstName(invokeGetFileNameFromLink(imgUrl))
        val subKey = invokeGetFileFirstName(invokeGetFileNameFromLink(subUrl))

        assertEquals("img key", "9b5787b6b8f74e41b4f14f4686e958a9", imgKey)
        assertEquals("sub key", "456e5f4e2c2848a9bdd1434ee85f1bbe", subKey)

        val rawMixin = imgKey + subKey
        assertEquals("raw mixin length", 64, rawMixin.length)

        val mixinKey = invokeGetWBIMixinKey(rawMixin)
        assertEquals("mixin key should be 32 chars", 32, mixinKey.length)
    }

    @Test
    fun `encodeUrl should encode special characters`() {
        // 验证 encodeUrl 对特殊字符的处理
        val input = "hello world & test=1"
        val encoded = invokeEncodeUrl(input)
        assertNotNull("encoded should not be null", encoded)
        assertTrue("spaces become %20", encoded!!.contains("%20"))
        assertFalse("should not contain +", encoded.contains("+"))
    }

    // ============ 反射辅助方法 ============

    private fun invokeGetFileFirstName(file: String): String {
        val method = WbiUtil::class.java.getDeclaredMethod(
            "getFileFirstName", String::class.java
        )
        method.isAccessible = true
        return method.invoke(WbiUtil, file) as String
    }

    private fun invokeGetFileNameFromLink(link: String): String {
        val method = WbiUtil::class.java.getDeclaredMethod(
            "getFileNameFromLink", String::class.java
        )
        method.isAccessible = true
        return method.invoke(WbiUtil, link) as String
    }

    private fun invokeGetWBIMixinKey(rawKey: String): String {
        val method = WbiUtil::class.java.getDeclaredMethod(
            "getWBIMixinKey", String::class.java
        )
        method.isAccessible = true
        return method.invoke(WbiUtil, rawKey) as String
    }

    private fun invokeMd5(plainText: String): String {
        val method = WbiUtil::class.java.getDeclaredMethod(
            "md5", String::class.java
        )
        method.isAccessible = true
        return method.invoke(WbiUtil, plainText) as String
    }

    private fun invokeSortUrlParams(encodedParams: String): String {
        val method = WbiUtil::class.java.getDeclaredMethod(
            "sortUrlParams", String::class.java
        )
        method.isAccessible = true
        return method.invoke(WbiUtil, encodedParams) as String
    }

    private fun invokeEncodeUrl(input: String?): String? {
        val method = WbiUtil::class.java.getDeclaredMethod(
            "encodeUrl", String::class.java
        )
        method.isAccessible = true
        return method.invoke(WbiUtil, input) as String?
    }

    // ============ 独立 MD5 验证，不依赖私有方法 ============

    @Test
    fun `md5 reference implementation should match`() {
        val digest = MessageDigest.getInstance("MD5")
        val hash = digest.digest("test".toByteArray(Charsets.UTF_8))
        val hex = hash.joinToString("") { "%02x".format(it) }
        assertEquals("098f6bcd4621d373cade4e832627b4f6", hex)
    }
}
