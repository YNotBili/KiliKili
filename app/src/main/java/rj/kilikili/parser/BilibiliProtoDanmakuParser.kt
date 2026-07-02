package rj.kilikili.parser

import android.graphics.Color
import android.util.Log
import master.flame.danmaku.danmaku.model.AlphaValue
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.Duration
import master.flame.danmaku.danmaku.model.IDisplayer
import master.flame.danmaku.danmaku.model.android.DanmakuFactory
import master.flame.danmaku.danmaku.model.android.Danmakus
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser
import master.flame.danmaku.danmaku.parser.IDataSource
import master.flame.danmaku.danmaku.util.DanmakuUtils
import org.json.JSONArray
import bilibili.community.service.dm.v1.Dm
import bilibili.community.service.dm.v1.Dm.DanmakuElem

/**
 * Protobuf 弹幕解析器。
 * 字段映射与历史 BiliDanmukuParser（XML 版）保持一致。
 */
class BilibiliProtoDanmakuParser : BaseDanmakuParser() {

    private var mDispScaleX: Float = 1f
    private var mDispScaleY: Float = 1f

    private var mIndex: Int = 0

    override fun setDisplayer(disp: IDisplayer): BaseDanmakuParser {
        super.setDisplayer(disp)
        mDispScaleX = mDispWidth / DanmakuFactory.BILI_PLAYER_WIDTH
        mDispScaleY = mDispHeight / DanmakuFactory.BILI_PLAYER_HEIGHT
        return this
    }

    @Suppress("UNCHECKED_CAST")
    override fun parse(): Danmakus? {
        val source = mDataSource as? IDataSource<List<DanmakuElem>> ?: return null
        val elems = source.data() ?: return null
        val result = Danmakus()
        val ctx = mContext
        if (ctx == null) {
            Log.e(TAG, "mContext is null")
            return result
        }

        for (elem in elems) {
            val type = elem.mode
            // BAS (mode=9) 暂不支持
            if (type == 9) continue

            val item = ctx.mDanmakuFactory.createDanmaku(type, ctx) ?: continue
            item.time = elem.progress.toLong()
            item.textSize = elem.fontsize * (mDispDensity - 0.6f)
            val colorInt = elem.color or 0xFF000000.toInt()
            item.textColor = colorInt
            item.textShadowColor = if (colorInt == Color.BLACK) Color.WHITE else Color.BLACK
            item.index = mIndex++

            DanmakuUtils.fillText(item, elem.content)

            if (item.duration == null) {
                // 工厂未设置 duration（理论上不会发生），跳过
                continue
            }

            // 高级弹幕（mode=7）：content 是 "[x,y,a-a,dur,...]" JSON 数组
            if (type == BaseDanmaku.TYPE_SPECIAL) {
                val text = item.text?.toString()?.trim().orEmpty()
                if (text.startsWith("[") && text.endsWith("]")) {
                    val textArr = try {
                        val arr = JSONArray(text)
                        Array(arr.length()) { i -> arr.getString(i) }
                    } catch (_: Exception) {
                        null
                    }
                    if (textArr == null || textArr.size < 5) continue
                    applySpecialDanmaku(item, textArr)
                }
            }

            item.setTimer(mTimer)
            result.addItem(item)
        }
        return result
    }

    private fun applySpecialDanmaku(item: BaseDanmaku, textArr: Array<String>) {
        item.text = textArr[4]
        var beginX = textArr[0].toFloatOrNull() ?: return
        var beginY = textArr[1].toFloatOrNull() ?: return
        var endX = beginX
        var endY = beginY
        val alphaArr = textArr[2].split("-")
        val beginAlpha = (AlphaValue.MAX * (alphaArr[0].toFloatOrNull() ?: 1f)).toInt()
        var endAlpha = beginAlpha
        if (alphaArr.size > 1) {
            endAlpha = (AlphaValue.MAX * (alphaArr[1].toFloatOrNull() ?: 1f)).toInt()
        }
        val alphaDuration = ((textArr[3].toFloatOrNull() ?: 0f) * 1000).toLong()
        var translationDuration = alphaDuration
        var translationStartDelay = 0L
        var rotateZ = 0f
        var rotateY = 0f
        if (textArr.size >= 7 && textArr[5].isNotEmpty()) {
            rotateZ = textArr[5].toFloatOrNull() ?: 0f
            rotateY = textArr[6].toFloatOrNull() ?: 0f
        }
        if (textArr.size >= 11 && textArr[7].isNotEmpty()) {
            endX = textArr[7].toFloatOrNull() ?: endX
            endY = textArr[8].toFloatOrNull() ?: endY
            if (textArr[9].isNotEmpty()) {
                translationDuration = textArr[9].toLongOrNull() ?: translationDuration
            }
            if (textArr[10].isNotEmpty()) {
                translationStartDelay = (textArr[10].toFloatOrNull() ?: 0f).toLong()
            }
        }
        if (isPercentage(beginX)) beginX *= DanmakuFactory.BILI_PLAYER_WIDTH
        if (isPercentage(beginY)) beginY *= DanmakuFactory.BILI_PLAYER_HEIGHT
        if (isPercentage(endX)) endX *= DanmakuFactory.BILI_PLAYER_WIDTH
        if (isPercentage(endY)) endY *= DanmakuFactory.BILI_PLAYER_HEIGHT

        item.duration = Duration(alphaDuration)
        item.rotationZ = rotateZ
        item.rotationY = rotateY
        mContext?.mDanmakuFactory?.fillTranslationData(
            item, beginX, beginY, endX, endY,
            translationDuration, translationStartDelay, mDispScaleX, mDispScaleY
        )
        mContext?.mDanmakuFactory?.fillAlphaData(item, beginAlpha, endAlpha, alphaDuration)

        if (textArr.size >= 12 && textArr[11].isNotEmpty() && textArr[11] == "true") {
            item.textShadowColor = Color.TRANSPARENT
        }
        if (textArr.size >= 15 && textArr[14].isNotEmpty()) {
            val motionPathString = textArr[14].substring(1)
            val pointStrArray = motionPathString.split("L")
            if (pointStrArray.isNotEmpty()) {
                val points = Array(pointStrArray.size) { FloatArray(2) }
                for (i in pointStrArray.indices) {
                    val parts = pointStrArray[i].split(",")
                    if (parts.size >= 2) {
                        points[i][0] = parts[0].toFloatOrNull() ?: 0f
                        points[i][1] = parts[1].toFloatOrNull() ?: 0f
                    }
                }
                DanmakuFactory.fillLinePathData(item, points, mDispScaleX, mDispScaleY)
            }
        }
    }

    private fun isPercentage(n: Float): Boolean = n in 0f..1f

    companion object {
        private const val TAG = "ProtoDanmaku"
    }
}

/**
 * 简单的 List<DanmakuElem> 数据源包装。
 */
class DanmakuListDataSource(
    private val elems: List<DanmakuElem>
) : IDataSource<List<DanmakuElem>> {
    override fun data(): List<DanmakuElem> = elems
    override fun release() {}
}
