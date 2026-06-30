package rj.kilikili.ui.screens.comment

import org.junit.Assert.assertEquals
import org.junit.Test
import rj.kilikili.api.BilibiliApiException

/**
 * Pure-logic tests for [resolveWriteReplyError] and the structured
 * [writeReplyErrorMessages] table.
 *
 * Why not exercise the full ViewModel: WriteReplyViewModel requires a
 * Hilt-injected ReplyRepository, and the spec restricts edits to a fixed
 * file set. The mapping logic is the load-bearing part of the change —
 * if the table lookup returns the right Chinese message for the right
 * code, the ViewModel's `sendReply` produces the same result it used to.
 */
class WriteReplyViewModelErrorTableTest {

    @Test
    fun `AccountNotFound (-101) maps to table message`() {
        assertEquals(
            "没有登录或登录信息有误",
            resolveWriteReplyError(BilibiliApiException(-101, "raw chinese ignored"))
        )
    }

    @Test
    fun `BannedAccount (-102) maps to table message`() {
        assertEquals(
            "账号被封禁",
            resolveWriteReplyError(BilibiliApiException(-102, "raw"))
        )
    }

    @Test
    fun `RateLimited (-509) maps to table message`() {
        assertEquals(
            "请求过于频繁",
            resolveWriteReplyError(BilibiliApiException(-509, "raw"))
        )
    }

    @Test
    fun `CaptchaRequired (12015) maps to table message`() {
        assertEquals(
            "需要评论验证码",
            resolveWriteReplyError(BilibiliApiException(12015, "raw"))
        )
    }

    @Test
    fun `SensitiveContent (12016) maps to table message`() {
        assertEquals(
            "包含敏感内容",
            resolveWriteReplyError(BilibiliApiException(12016, "raw"))
        )
    }

    @Test
    fun `TooLong (12025) maps to table message`() {
        assertEquals(
            "字数过多",
            resolveWriteReplyError(BilibiliApiException(12025, "raw"))
        )
    }

    @Test
    fun `Blocked (12035) maps to table message`() {
        assertEquals(
            "被拉黑了",
            resolveWriteReplyError(BilibiliApiException(12035, "raw"))
        )
    }

    @Test
    fun `DuplicateReply (12051) maps to table message`() {
        assertEquals(
            "重复评论，请勿刷屏",
            resolveWriteReplyError(BilibiliApiException(12051, "raw"))
        )
    }

    @Test
    fun `unmapped api code falls back to raw message`() {
        val raw = "999999 unknown"
        assertEquals(
            raw,
            resolveWriteReplyError(BilibiliApiException(999999, raw))
        )
    }

    @Test
    fun `non-api exception falls back to raw message`() {
        assertEquals(
            "socket closed",
            resolveWriteReplyError(IllegalStateException("socket closed"))
        )
    }

    @Test
    fun `non-api exception with null message falls back to generic`() {
        val thrown = object : Throwable() {}
        assertEquals("发送失败，请重试", resolveWriteReplyError(thrown))
    }

    @Test
    fun `api exception with null message uses table message`() {
        assertEquals(
            "重复评论，请勿刷屏",
            resolveWriteReplyError(BilibiliApiException(12051, ""))
        )
    }
}