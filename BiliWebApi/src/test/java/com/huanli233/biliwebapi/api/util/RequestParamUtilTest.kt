package com.huanli233.biliwebapi.api.util

import org.junit.Assert.*
import org.junit.Test

/**
 * RequestParamUtil 参数生成工具单元测试。
 *
 * 验证：
 * 1. genBlsid 格式：8位随机大写hex_时间戳hex
 * 2. genUuidInfoc 格式：8-4-4-4-12-uuid格式+5位数字+infoc
 * 3. genBnut 格式：Unix时间戳字符串
 */
class RequestParamUtilTest {

    @Test
    fun `genBlsid should match expected format`() {
        val blsid = RequestParamUtil.genBlsid()
        // 格式: [0-9A-F]{8}_[0-9a-f]+
        assertTrue(
            "blsid should match pattern",
            blsid.matches(Regex("[0-9A-F]{8}_[0-9a-f]+"))
        )
    }

    @Test
    fun `genBlsid should be unique across calls`() {
        val first = RequestParamUtil.genBlsid()
        val second = RequestParamUtil.genBlsid()
        assertNotEquals("blsid should differ", first, second)
    }

    @Test
    fun `genUuidInfoc should match uuid-like pattern ending with infoc`() {
        val uuid = RequestParamUtil.genUuidInfoc()
        // 格式: XXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX-XXXXXinfoc
        // 但实际每个 section 是 hex 字符，长度 8-4-4-4-12 + 5位数字 + infoc
        assertTrue(
            "uuid infoc should match pattern",
            uuid.matches(Regex("[0-9A-F]{8}-[0-9A-F]{4}-[0-9A-F]{4}-[0-9A-F]{4}-[0-9A-F]{12}\\d{5}infoc"))
        )
    }

    @Test
    fun `genUuidInfoc should end with infoc`() {
        val uuid = RequestParamUtil.genUuidInfoc()
        assertTrue("should end with infoc", uuid.endsWith("infoc"))
    }

    @Test
    fun `genUuidInfoc should have hyphens in correct positions`() {
        val uuid = RequestParamUtil.genUuidInfoc()
        // 8-4-4-4-12 格式
        val parts = uuid.split("-")
        assertEquals("should have 5 hyphen-separated parts", 5, parts.size)
    }

    @Test
    fun `genUuidInfoc should produce different values each call`() {
        val first = RequestParamUtil.genUuidInfoc()
        val second = RequestParamUtil.genUuidInfoc()
        assertNotEquals("uuid infoc should be unique", first, second)
    }

    @Test
    fun `genBnut should be a numeric timestamp string`() {
        val bnut = RequestParamUtil.genBnut()
        assertTrue("bnut should be numeric", bnut.matches(Regex("\\d+")))
    }

    @Test
    fun `genBnut should be current unix timestamp (approx)`() {
        val now = System.currentTimeMillis() / 1000
        val bnut = RequestParamUtil.genBnut().toLongOrNull()
        assertNotNull("bnut should parse as long", bnut)
        // 应该在当前时间 ±5 秒内
        val diff = kotlin.math.abs(bnut!! - now)
        assertTrue("bnut should be ~current time, diff=$diff", diff < 5)
    }

    @Test
    fun `genBnut should increase over time`() {
        val first = RequestParamUtil.genBnut().toLong()
        Thread.sleep(2) // 等至少 1 秒
        val second = RequestParamUtil.genBnut().toLong()
        assertTrue("second should be >= first", second >= first)
    }
}
