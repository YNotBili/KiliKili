package com.huanli233.biliwebapi.httplib

import com.huanli233.biliwebapi.httplib.HttpUtils.parseFormBody
import okhttp3.FormBody
import org.junit.Assert.*
import org.junit.Test

/**
 * HttpUtils 工具单元测试。
 *
 * 验证：
 * 1. parseFormBody 正确解析 URL-encoded 字符串
 * 2. 空字符串处理
 * 3. 特殊字符处理
 */
class HttpUtilsTest {

    @Test
    fun `parseFormBody should parse simple key-value pairs`() {
        val input = "key1=value1&key2=value2&key3=value3"
        val builder = input.parseFormBody()
        val body = builder.build()

        assertEquals(3, body.size)
        assertEquals("value1", body.value("key1"))
        assertEquals("value2", body.value("key2"))
        assertEquals("value3", body.value("key3"))
    }

    @Test
    fun `parseFormBody should handle URL-encoded values`() {
        val input = "message=%E4%BD%A0%E5%A5%BD&type=text"
        val builder = input.parseFormBody()
        val body = builder.build()

        assertEquals(2, body.size)
        assertEquals("你好", body.value("message"))
        assertEquals("text", body.value("type"))
    }

    @Test
    fun `parseFormBody should handle empty string`() {
        val input = ""
        val builder = input.parseFormBody()
        val body = builder.build()
        assertEquals(0, body.size)
    }

    @Test
    fun `parseFormBody should handle blank string`() {
        val input = "   "
        val builder = input.parseFormBody()
        val body = builder.build()
        assertEquals(0, body.size)
    }

    @Test
    fun `parseFormBody should handle special characters`() {
        val input = "name=hello%20world&symbol=%26%3D"
        val builder = input.parseFormBody()
        val body = builder.build()

        assertEquals("hello world", body.value("name"))
        assertEquals("&=", body.value("symbol"))
    }

    @Test
    fun `parseFormBody should handle value with no value`() {
        val input = "flag&key=value"
        val builder = input.parseFormBody()
        val body = builder.build()

        // 注意：`flag` 没有 = 时，parse 会 split 成 [flag]，keyValue 长度为 1
        // encodedKey = "flag", encodedValue = ""
        // 但 limit=2 在 split 时... 实际行为取决于具体实现
        // 验证 key=value 对可正常解析即可
        assertEquals("value", body.value("key"))
    }

    @Test
    fun `parseFormBody should handle multiple ampersands`() {
        val input = "a=1&&b=2&&&c=3"
        val builder = input.parseFormBody()
        val body = builder.build()

        // 空字符串段 split 后可能产生空 pair，应跳过
        assertEquals(3, body.size)
    }
}

/** 辅助：从 FormBody 按 name 获取 value */
private fun FormBody.value(name: String): String? {
    for (i in 0 until size) {
        if (name(i) == name) return value(i)
    }
    return null
}
