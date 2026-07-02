package rj.kilikili.data.repository

import com.huanli233.biliwebapi.api.interfaces.ICommonApi
import com.huanli233.biliwebapi.bean.common.SimpleAction
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommonRepository @Inject constructor() {

    suspend fun simpleAction(action: Int, objectIdStr: String, biz: Int, spmid: String = "unknown", from: String = "unknown"): Result<Unit> {
        return bilibiliApi.api(ICommonApi::class) {
            simpleAction(SimpleAction(action, SimpleAction.Entity(objectIdStr, SimpleAction.EntityType(biz)), SimpleAction.Meta(spmid, from)))
        }.apiResultNonNull()
    }
}