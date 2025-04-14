package com.lonwulf.ideamanager.core.database.converter

import android.media.Image
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.time.LocalDate
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(timestamp: Long?): Date? = timestamp?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromEpochDay(epochDay: Long?): LocalDate? = epochDay?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter
    fun localDateToEpochDay(localDate: LocalDate?): Long? = localDate?.toEpochDay()

    class ImageTypeConverter : Serializable {
        private val gson = Gson()

        @TypeConverter
        fun imageToString(image: Image): String = gson.toJson(image)

        @TypeConverter
        fun stringToImage(value: String?): Image {
            val type = object : TypeToken<Image>() {}.type
            return gson.fromJson(value, type)
        }
    }
}