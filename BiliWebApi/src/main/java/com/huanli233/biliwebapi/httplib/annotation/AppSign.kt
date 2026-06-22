package com.huanli233.biliwebapi.httplib.annotation

/**
 * 标记需要 app 签名的接口。
 * 被标记的方法会自动添加 appkey、ts、sign 参数到请求体。
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class AppSign(
    val appkey: String = "dfca71928277209b",
    val appsecret: String = "b5475a8825547a4fc26c7d518eaaa02e",
)
