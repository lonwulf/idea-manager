package com.lonwulf.ideamanager.core.di.modules

import android.content.Context
import androidx.room.Room
import com.lonwulf.ideamanager.core.BuildConfig
import com.lonwulf.ideamanager.core.database.IdeaManagerDatabase
import com.lonwulf.ideamanager.core.util.DATABASE_KEY
import com.lonwulf.ideamanager.core.util.DATABASE_NAME
import com.lonwulf.ideamanager.core.util.SQLCipherUtils
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.io.IOException

@Synchronized
fun provideDatabase(context: Context): IdeaManagerDatabase {
    return if (BuildConfig.DEBUG) {
        Room
            .databaseBuilder(context, IdeaManagerDatabase::class.java, DATABASE_NAME)
            .build()
    } else {
        val passphrase: ByteArray =
            SQLiteDatabase.getBytes(DATABASE_KEY.toCharArray())
        val factory = SupportFactory(passphrase)
        //check database encryption state
        val state: SQLCipherUtils.State =
            SQLCipherUtils.getDatabaseState(context, DATABASE_NAME)
        if (state == (SQLCipherUtils.State.UNENCRYPTED)) {
            try {
                SQLCipherUtils.encrypt(context, DATABASE_NAME, passphrase)

            } catch (e: IOException) {
                e.printStackTrace()
                return Room
                    .databaseBuilder(context, IdeaManagerDatabase::class.java, DATABASE_NAME)
                    .build()
            }
        }
        return Room
            .databaseBuilder(context, IdeaManagerDatabase::class.java, DATABASE_NAME)
            .openHelperFactory(factory)
            .build()
    }
}
