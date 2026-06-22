package rj.kilikili.utils.encode

import org.junit.Assert.*
import org.junit.Test

class MD5UtilTest {

    @Test
    fun `md5 of empty string`() {
        val result = MD5Util.md5("")
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", result)
    }

    @Test
    fun `md5 of known input`() {
        val result = MD5Util.md5("test")
        assertEquals("098f6bcd4621d373cade4e832627b4f6", result)
    }

    @Test
    fun `md5 of Chinese input`() {
        val result = MD5Util.md5("你好")
        assertEquals("7eca689f0d3389d9dea66ae112e5cfd7", result)
    }

    @Test
    fun `md5 is deterministic`() {
        val a = MD5Util.md5("hello_world_42")
        val b = MD5Util.md5("hello_world_42")
        assertEquals(a, b)
    }

    @Test
    fun `md5 differs for different inputs`() {
        val a = MD5Util.md5("abc")
        val b = MD5Util.md5("xyz")
        assertNotEquals(a, b)
    }

    @Test
    fun `md5 output is 32 lowercase hex chars`() {
        val result = MD5Util.md5("any string here 123!@#")
        assertEquals(32, result.length)
        assertTrue(result.matches(Regex("[0-9a-f]{32}")))
    }
}
