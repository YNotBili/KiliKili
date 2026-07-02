package rj.kilikili.data.repository

import com.huanli233.biliwebapi.api.interfaces.INoteApi
import com.huanli233.biliwebapi.api.interfaces.INoteApi.NoteItem
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor() {

    suspend fun getArchiveNotes(oid: Long, type: Int = 1): Result<List<NoteItem>> {
        return bilibiliApi.api(INoteApi::class) { getArchiveNotes(oid, type) }
            .apiResultNonNull().map { it.list }
    }

    suspend fun getUserNotes(mid: Long, page: Int = 1, pageSize: Int = 20): Result<List<NoteItem>> {
        return bilibiliApi.api(INoteApi::class) { getUserNotes(mid, page, pageSize) }
            .apiResultNonNull().map { it.list }
    }

    suspend fun addNote(oid: Long, title: String, content: String, type: Int = 1): Result<Long> {
        return bilibiliApi.api(INoteApi::class) { addNote(oid, type, title, content) }
            .apiResultNonNull().map { it.note_id }
    }

    suspend fun deleteNote(noteId: Long): Result<Unit> {
        return bilibiliApi.api(INoteApi::class) { deleteNote(noteId) }.apiResultNonNull()
    }
}