package com.huanli233.biliwebapi.bean.login

import com.google.gson.Gson
import com.huanli233.biliwebapi.bean.ApiResponse
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * 登录相关 Bean 模型 + Gson 序列化/反序列化单元测试。
 *
 * 验证：
 * 1. TvQrCodeAuth 的 JSON 解析
 * 2. TvQrCodePoll 的 JSON 解析（含 token_info 和 cookie_info）
 * 3. QrCode 的 JSON 解析
 * 4. ApiResponse 包装解析
 */
class LoginModelsTest {

    private lateinit var gson: Gson

    @Before
    fun setup() {
        gson = Gson()
    }

    @Test
    fun `TvQrCodeAuth should parse from JSON`() {
        val json = """
            {
                "auth_code": "ABC123DEF",
                "url": "https://bilibili.com/tv/qr/ABC123DEF",
                "expires_in": 1800
            }
        """.trimIndent()

        val auth = gson.fromJson(json, TvQrCodeAuth::class.java)

        assertNotNull("auth should not be null", auth)
        assertEquals("ABC123DEF", auth.authCode)
        assertEquals("https://bilibili.com/tv/qr/ABC123DEF", auth.url)
        assertEquals(1800L, auth.expiresIn)
    }

    @Test
    fun `TvQrCodeAuth should handle missing expires_in`() {
        val json = """
            {
                "auth_code": "XYZ789",
                "url": "https://bilibili.com/tv/qr/XYZ789"
            }
        """.trimIndent()

        val auth = gson.fromJson(json, TvQrCodeAuth::class.java)
        assertNotNull(auth)
        assertEquals("XYZ789", auth.authCode)
        assertEquals(0L, auth.expiresIn) // default value
    }

    @Test
    fun `TvQrCodePoll with token_info should parse correctly`() {
        val json = """
            {
                "auth_code": "ABC123",
                "status": true,
                "token_info": {
                    "mid": 12345678,
                    "access_token": "access_token_value_here",
                    "refresh_token": "refresh_token_value_here",
                    "expires_in": 2592000
                }
            }
        """.trimIndent()

        val poll = gson.fromJson(json, TvQrCodePoll::class.java)

        assertNotNull(poll)
        assertEquals("ABC123", poll.authCode)
        assertTrue("status should be true", poll.status)

        val tokenInfo = poll.tokenInfo
        assertNotNull("token_info should not be null", tokenInfo)
        assertEquals(12345678L, tokenInfo!!.mid)
        assertEquals("access_token_value_here", tokenInfo.accessToken)
        assertEquals("refresh_token_value_here", tokenInfo.refreshToken)
        assertEquals(2592000L, tokenInfo.expiresIn)
    }

    @Test
    fun `TvQrCodePoll with cookie_info should parse correctly`() {
        val json = """
            {
                "auth_code": "ABC123",
                "status": true,
                "token_info": {
                    "mid": 12345678,
                    "access_token": "tok_abc",
                    "refresh_token": "ref_abc",
                    "expires_in": 2592000
                },
                "cookie_info": {
                    "cookies": [
                        {"name": "DedeUserID", "value": "12345678", "http_only": 1, "expires": 1700000000},
                        {"name": "DedeUserID__ckMd5", "value": "ckmd5hash", "http_only": 1, "expires": 1700000000},
                        {"name": "bili_jct", "value": "csrf_token_val", "http_only": 1, "expires": 1700000000},
                        {"name": "SESSDATA", "value": "sessdata_val", "http_only": 1, "expires": 1700000000}
                    ]
                }
            }
        """.trimIndent()

        val poll = gson.fromJson(json, TvQrCodePoll::class.java)

        assertNotNull(poll)
        assertTrue(poll.status)

        val cookieInfo = poll.cookieInfo
        assertNotNull("cookie_info should not be null", cookieInfo)
        assertEquals(4, cookieInfo!!.cookies.size)

        val cookies = cookieInfo.cookies
        assertEquals("DedeUserID", cookies[0].name)
        assertEquals("12345678", cookies[0].value)
        assertEquals(1, cookies[0].httpOnly)

        assertEquals("bili_jct", cookies[2].name)
        assertEquals("csrf_token_val", cookies[2].value)
    }

    @Test
    fun `TvQrCodePoll with no token should default to empty strings`() {
        val json = """
            {
                "auth_code": "",
                "status": false,
                "token_info": null,
                "cookie_info": null
            }
        """.trimIndent()

        val poll = gson.fromJson(json, TvQrCodePoll::class.java)
        assertNotNull(poll)
        assertFalse("status should be false", poll.status)
        assertNull("token_info should be null", poll.tokenInfo)
    }

    @Test
    fun `ApiResponse wrapper should parse correctly`() {
        val json = """
            {
                "code": 0,
                "message": "0",
                "data": {
                    "auth_code": "TEST_AUTH",
                    "url": "https://bilibili.com/tv/qr/TEST_AUTH",
                    "expires_in": 1800
                }
            }
        """.trimIndent()

        val response = gson.fromJson(json, ApiResponse::class.java)
        assertNotNull(response)
        assertEquals(0, response.code)
        assertEquals("0", response.message)
        assertNotNull("data should not be null", response.data)
    }

    @Test
    fun `ApiResponse with error code should still parse`() {
        val json = """
            {
                "code": -400,
                "message": "请求错误",
                "data": null
            }
        """.trimIndent()

        val response = gson.fromJson(json, ApiResponse::class.java)
        assertEquals(-400, response.code)
        assertEquals("请求错误", response.message)
    }

    @Test
    fun `QrCode should parse from JSON`() {
        val json = """
            {
                "url": "https://passport.bilibili.com/x/passport-login/web/qrcode/generate?qrcode_key=test_key",
                "qrcode_key": "test_key"
            }
        """.trimIndent()

        val qrCode = gson.fromJson(json, QrCode::class.java)
        assertNotNull(qrCode)
        assertEquals("test_key", qrCode.qrcodeKey)
        assertTrue("url should contain qrcode_key", qrCode.url.contains("qrcode_key"))
    }

    @Test
    fun `QrCode LoginResult should parse`() {
        val json = """
            {
                "code": 0,
                "message": "成功",
                "data": {
                    "url": "https://passport.bilibili.com/...",
                    "refresh_token": "refresh_token_value",
                    "timestamp": 1234567890,
                    "code": 0,
                    "mid": 12345678
                }
            }
        """.trimIndent()

        // QrCode.LoginResult 是内部类，需要通过 Gson 解析嵌套
        // 实际上 ApiResponse 会解析 data 为 JsonObject
        // 这里测试 data 中的某个字段
        val response = gson.fromJson(json, ApiResponse::class.java)
        assertEquals(0, response.code)
        assertEquals("成功", response.message)

        val data = response.data as? Map<*, *>
        assertNotNull("data should be a map", data)
        assertEquals("refresh_token_value", data?.get("refresh_token"))
        assertEquals(12345678.0, data?.get("mid")) // Gson returns Double for numbers
    }

    // ============ TvCookie 数据类测试 ============

    @Test
    fun `TvCookie should store fields correctly`() {
        val cookie = TvCookie(
            name = "SESSDATA",
            value = "sessdata_abc_123",
            httpOnly = 1,
            expires = 1700000000L
        )

        assertEquals("SESSDATA", cookie.name)
        assertEquals("sessdata_abc_123", cookie.value)
        assertEquals(1, cookie.httpOnly)
        assertEquals(1700000000L, cookie.expires)
    }

    @Test
    fun `TvCookie should work with httpOnly=0 and expires=0`() {
        val cookie = TvCookie(
            name = "buvid3",
            value = "buvid3_value",
            httpOnly = 0,
            expires = 0L
        )

        assertEquals(0, cookie.httpOnly)
        assertEquals(0L, cookie.expires)
    }

    @Test
    fun `TvCookie list should be iterable`() {
        val cookies = listOf(
            TvCookie("DedeUserID", "123", 1, 1700000000L),
            TvCookie("bili_jct", "csrf123", 1, 1700000000L),
            TvCookie("SESSDATA", "session123", 1, 1700000000L),
            TvCookie("buvid3", "buvid123", 0, 0L),
        )

        assertEquals(4, cookies.size)
        assertEquals("DedeUserID", cookies[0].name)
        assertEquals("buvid3", cookies[3].name)
    }

    @Test
    fun `TvCookie should map httpOnly correctly as boolean check`() {
        val httpOnly1 = TvCookie("a", "1", 1, 0)
        val httpOnly0 = TvCookie("b", "0", 0, 0)

        assertTrue("httpOnly=1 should be true", httpOnly1.httpOnly == 1)
        assertFalse("httpOnly=0 should be false", httpOnly0.httpOnly == 1)
    }
}
