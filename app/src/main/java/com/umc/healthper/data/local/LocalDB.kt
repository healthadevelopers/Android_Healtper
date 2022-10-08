package com.umc.healthper.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.umc.healthper.data.entity.*


@Database(entities = [Work::class, WorkPart::class, WorkFav::class, PrivacyInfo::class], version = 5)
abstract class LocalDB: RoomDatabase() {
    abstract fun WorkDao(): WorkDao
    abstract fun WorkPartDao(): WorkPartDao
    abstract fun WorkFavDao(): WorkFavDao
    abstract fun privacyInfoDao():PrivacyInfoDao

    companion object {
        private var instance: LocalDB? = null

        @Synchronized
        fun getInstance(context: Context): LocalDB? {
            if (instance == null) {
                synchronized(LocalDB::class) { //synchronized block-> 클래스자체를 동기화->클래스 사용해 생성하는 모든 쓰레드 동기화
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        LocalDB::class.java,
                        "local_database"
                    ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
                }
            }
            return instance
        }
    }

}