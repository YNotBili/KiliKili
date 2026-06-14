package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.httplib.annotation.Csrf
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/** 笔记/批注 API */
interface INoteApi {

    @GET("/x/note/list/archive")
    suspend fun getArchiveNotes(
        @Query("oid") oid: Long,
        @Query("type") type: Int = 1
    ): ApiResponse<NoteListResult>

    @GET("/x/note/publish/list/user")
    suspend fun getUserNotes(
        @Query("mid") mid: Long,
        @Query("pn") page: Int = 1,
        @Query("ps") pageSize: Int = 20
    ): ApiResponse<NoteListResult>

    @POST("/x/note/add")
    @FormUrlEncoded @Csrf
    suspend fun addNote(
        @Field("oid") oid: Long,
        @Field("type") type: Int = 1,
        @Field("title") title: String,
        @Field("content") content: String
    ): ApiResponse<NoteActionResult>

    @POST("/x/note/del")
    @FormUrlEncoded @Csrf
    suspend fun deleteNote(
        @Field("note_id") noteId: Long
    ): ApiResponse<Unit>

    data class NoteListResult(val list: List<NoteItem> = emptyList())
    data class NoteItem(val note_id: Long = 0, val title: String = "", val content: String = "", val ctime: Long = 0)
    data class NoteActionResult(val note_id: Long = 0)
}