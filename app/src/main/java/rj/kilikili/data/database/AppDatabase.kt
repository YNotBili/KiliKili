package rj.kilikili.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import rj.kilikili.data.account.AccountDao
import rj.kilikili.data.account.AccountEntity
import rj.kilikili.data.account.CookieEntity
import rj.kilikili.data.account.CookiesDao
import rj.kilikili.data.download.DownloadDao
import rj.kilikili.data.download.DownloadEntity
import rj.kilikili.data.download.DownloadTypeConverters

@Database(
    entities = [AccountEntity::class, CookieEntity::class, DownloadEntity::class],
    version = 3
)
@TypeConverters(DownloadTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun cookiesDao(): CookiesDao
    abstract fun downloadDao(): DownloadDao
}