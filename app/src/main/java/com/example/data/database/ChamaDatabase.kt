package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.ChamaDao
import com.example.data.model.ContributionEntity
import com.example.data.model.LoanEntity
import com.example.data.model.MeetingRecordEntity
import com.example.data.model.MemberEntity
import com.example.data.model.PayoutEntity

@Database(
    entities = [
        MemberEntity::class,
        ContributionEntity::class,
        LoanEntity::class,
        PayoutEntity::class,
        MeetingRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ChamaDatabase : RoomDatabase() {

    abstract fun chamaDao(): ChamaDao

    companion object {
        @Volatile
        private var INSTANCE: ChamaDatabase? = null

        fun getDatabase(context: Context): ChamaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChamaDatabase::class.java,
                    "chama_ledger_clean_v1"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
