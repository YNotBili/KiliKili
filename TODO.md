# KiliKili TODO — 缺失功能 / 零调用 API

> 对照 `~/PiliPlus` (Flutter 移动端) + `~/Orbit` (WearOS 同赛道 Compose)
> 范围: `app/src/main/java/rj/kilikili/` + `BiliWebApi/`
> 平台: WearOS (Wear OS 手表客户端)
>
> 按"功能维度"分组，不打分优先级——你看到了再决定做不做、什么时候做。
>
> **技术债类（runBlocking / VM 膨胀 / FileProvider / 日志泄露 / 死代码等）见下方"已落 issue"分区，单独跟踪。**

---

## 已落 issue（kili-research 团队审计 2026-06-23）

> 6 个审计结论已沉淀为 GitHub issues，与本页"功能差距"分开管理。issue 详情用 `gh issue view <n>` 看。

- **#1** [P1] 主线程 runBlocking 与 WBI/Cookie 并发写 — `BilibiliApiInterceptor` / `AppCookieManager` / `WbiDataManager` / `LocalData.settings` / `AccountManager.currentAccount` 改异步 + Mutex + Dispatchers.IO
- **#2** [P1] 登录流程:accountId 用错及错误码硬编码 (死代码) — `QrCodeLoginViewModel` 死代码删除,`HdQrCodeLogin` 错误码改结构化
- **#3** [P1] 播放器:ViewModel 膨胀、资源泄漏、SurfaceView 泄漏 — `PlayerViewModel` 拆 2-3 个 VM,`PlayerScreen` 死循环改 `repeatOnLifecycle`,SurfaceView 回调移 `DisposableEffect`
- **#4** [P1] 下载子系统和 FileProvider 安全性 — `DownloadManager` / `DownloadWorker` 强解包改 `getOrNull` + 合并 Room 查询,`file_paths.xml` 收窄到 `cache-path` / `external-files-path`
- **#5** [P2] 调试日志泄露与绕过 OkHttp 的图片下载 — `ApiDebugLogger` 编译期常量化,`ImageViewerScreen.saveImage` 改 OkHttp 下载
- **#6** [P2] 死代码与杂项清理 — 双轨登录合并、`isHd` 死字段删、菜单双源统一、URLEncoder 加 charset、MANAGE_EXTERNAL_STORAGE 评估、Hikage/media3 死引入移除

---

## 已知 Stub / 占位

- [ ] **合集详情页 `CollectionDetailScreen`** — 当前显示 "合集详情页开发中..." + Season ID。`IVideoApi.getVideoInfo` 已返回 `ugcSeason`，VideoDetail 也引入了入口，但页面没实现。Orbit / PiliPlus 都有完整合集列表+详情+分集播放。
- [ ] **`ViewPreview` 路由** (SettingsGraph) — 空 `Box(Modifier.fillMaxSize())`
- [ ] **黑名单页 `Blacklist`** — 路由注册了、`RelationExRepository.getBlacklist` API 已有，**Screen 文件完全缺失**
- [ ] **`SendDynamicScreen`** — 只能发纯文本，缺图片 / @ / 话题 / 投票 / 预约

---

## 直播

- [ ] **WebSocket 直播弹幕** — `LiveRoomScreen` 只显示房间信息，没接 WebSocket 连接 + 32s heartbeat + 弹幕渲染。`ILiveApi` + `ILiveExApi.getDanmuInfo`（HTTP 历史弹幕）有，Orbit 有完整 `PlayerDanmuClientListener` 实时弹幕协议。
- [ ] **直播发送弹幕** — `ILiveApi` 缺 `sendDanmu`；Orbit 有
- [ ] **关注直播列表** — `ILiveApi.getFollowedLive` API 有，没接 UI
- [ ] **直播表情 / 关注直播** — `LiveApi.sendLiveEmote / getEmotes`、`getFollowedLive` 全没接
- [ ] **SuperChat 列表** — `ILiveExApi.getSuperChatMessages` 没接
- [ ] **直播弹幕历史** — `ILiveExApi.getDanmakuHistory` 零调用

## 动态 / 发布

- [ ] **动态发图** — `IUploadApi.uploadBfs / uploadImage` 零调用
- [ ] **@用户搜索 / mention** — `DynamicRepository.mentionSearch` 有、UI 没接
- [ ] **话题 / 话题页** — `ITopicApi` 整层零调用（`/dynTopic`、`/dynTopicRcmd`）
- [ ] **投票** — `IVoteApi` 零调用
- [ ] **直播预约** — `IReserveApi` 零调用
- [ ] **动态删除 / 置顶 / 编辑** — `IDynamicExApi.removeDynamic / editDynamic / setTopDynamic / rmTopDynamic` 全没接
- [ ] **未读动态数** — `IDynamicExApi.getUnreadDynamic` 没接

## 评论 / 弹幕

- [ ] **弹幕屏蔽面板** — `IDmApi` (filter/add/del) 整层零调用
- [ ] **弹幕举报 / 评论举报** — `IReportApi` 零调用
- [ ] **表情包选择器** — `IEmoteApi` Repo 有，WriteReplyScreen / SendDynamicScreen 没接
- [ ] **Opus 收藏** — `IOpusApi.favoriteOpus` 没接

## 视频 / 播放器

- [ ] **视频分P切换** — `IVideoApi.getVideoInfo` 返回 `pages[]`，VideoDetailScreen / PlayerScreen 完全没接
- [ ] **字幕** — `IVideoApi.getSubtitleContent` 写了没用
- [ ] **互动视频 (剧情树)** — Orbit 有 `InteractionVideoApi`，KiliKili 无
- [ ] **长按倍速 / 自定义倍速** — PlayerScreen 只有默认倍速设置
- [ ] **弹幕密度精细化** — 现在 `danmakuMaxCount` 一项；PiliPlus 22 项
- [ ] **心跳上报 / 在线观看人数** — `IAppApi.heartbeat / getOnlineTotal` 没接
- [ ] **Player V2 API** — `IPlayerV2Api` 接口文件缺失（只有 v1）

## 番剧 / PGC

- [ ] **番剧筛选 / 索引** — `IPgcExApi.getIndexCondition / getIndexResult` 没接
- [ ] **番剧排行榜** — `IPgcExApi.getPgcRank` 没接
- [ ] **追番 / 取消追番** — `IPgcExApi.followPgc / unfollowPgc` 没接
- [ ] **番剧短评 / 长评** — `IPgcReviewApi` 整层零调用

## 收藏 / 稍后再看 / 历史

- [ ] **收藏夹 CRUD + 批量** — `IFavoriteExApi.addFolder / editFolder / deleteFolder / batchDeal / getFavResourceList` 全没接
- [ ] **稍后再看批量 / 清空 / 复制** — `IWatchLaterExApi.clearWatchLater / copyToWatchLater / batchDeleteWatchLater` 没接
- [ ] **历史暂停 (shadow) + 单条删除** — `IHistoryExApi.setPauseHistory / getHistoryStatus / deleteHistoryEntry` Repo 有，UI 没接
- [ ] **历史搜索** — `IHistoryExApi.searchHistory` 没接

## 搜索

- [ ] **搜索建议 (输入弹词)** — `ISearchApi.getSearchSuggestions` Repo 有，UI 没接
- [ ] **默认热搜词** — `ISearchExApi.getDefaultSearch` 缺 UI 入口
- [ ] **热词榜** — `ISearchExApi.getHotWords` 没接

## 关注 / 黑名单 / 关系

- [ ] **同关注** — `IRelationExApi.getSameFollowing` 零调用
- [ ] **搜索关注中** — `IRelationExApi.searchFollowing` 零调用
- [ ] **关注分组管理**（加/移成员 / 排序） — `IFollowTagApi` Screen 在，UI 不全

## 登录 / 账号

- [ ] **登录方式扩充** — `ILoginApi` 已有密码/短信/Captcha/Geetest 全套端点，目前只用 HD QR + Cookie import
- [ ] **Cookie 导出到剪贴板** — 设置里没导出按钮
- [ ] **登录设备管理** — `IMemberApi.getLoginRecord` 只能看记录，不能踢出

## 用户 / 空间

- [ ] **充电榜** — `IElectricApi` Repo 有，UserProfile 没显示
- [ ] **最近点赞 / 最近投币视频** — `IUserSpaceApi.getRecentCoinVideos / getRecentLikeVideos` 零调用
- [ ] **用户专栏 tab** — `IUserApi.getUserArticles` API 有，UserProfile 缺入口

## 通用 / 运营

- [ ] **`ICommonApi.simpleAction`** — 通用动作（Opus 收藏、话题点赞等复用）零调用
- [ ] **`IArticleApi.getArticle`** — 专栏（已被 deprecated 标记，让位 Opus）但 Screen 完全缺
- [ ] **未读消息轮询** — `IMessageApi.getUnreadCount` 没接，菜单图标没红点
- [ ] **大会员签到** — `IVipApi.addExperience` 一行调用，没接

## WearOS 特色（可探索）

- [ ] **沉浸式 Curved Time Text** — Orbit `WysTimeText` 是 WearOS 招牌，KiliKili 缺
- [ ] **WearOS 表盘 Complication / Tile** — WearOS 原生特色，可探索

## 零调用 API 速查（按文件）

```
BiliWebApi/src/main/java/com/huanli233/biliwebapi/api/interfaces/

IDmApi                 — 弹幕屏蔽
IReportApi             — 举报
INoteApi               — 视频笔记
IVoteApi               — 投票
IReserveApi            — 直播预约
ITopicApi              — 话题
IFavoriteExApi         — 收藏夹 CRUD/批量
IWatchLaterExApi       — 稍后再看批量
IDynamicExApi          — 动态删除/置顶/编辑/未读
IPgcExApi              — PGC 筛选/排行/追番
IPgcReviewApi          — 番剧短评
ICommonApi             — 通用动作
IUploadApi             — 图片上传
IElectricApi           — 充电榜（仅 Repo）
IArticleApi            — 专栏 (deprecated)
IVipApi.addExperience  — 大会员签到
IAppApi.heartbeat      — 播放心跳
IAppApi.getOnlineTotal — 在线人数
ILiveApi.getFollowedLive — 关注直播
ILiveExApi.getSuperChatMessages — SuperChat
ILiveExApi.getDanmakuHistory  — 直播弹幕历史
IRelationExApi.getSameFollowing — 同关注
IRelationExApi.searchFollowing  — 搜索关注中
```
