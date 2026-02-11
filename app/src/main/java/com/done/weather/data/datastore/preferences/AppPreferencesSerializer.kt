package com.done.weather.data.datastore.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.done.weather.AppConstant
import com.done.weather.data.datastore.preferences.entity.AppPreferencesEntity
import com.done.weather.di.jsonDeserializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import java.io.InputStream
import java.io.OutputStream

typealias AppPreferencesDatastore = DataStore<AppPreferencesEntity>

val Context.appPreferencesDatastore: AppPreferencesDatastore by dataStore(
    AppConstant.PREFERENCES_FILENAME,
    AppPreferencesSerializer
)

object AppPreferencesSerializer : Serializer<AppPreferencesEntity> {
    override val defaultValue: AppPreferencesEntity
        get() = AppPreferencesEntity()

    override suspend fun readFrom(input: InputStream): AppPreferencesEntity {
        return withContext(Dispatchers.IO) {
            try {
                jsonDeserializer.decodeFromString(
                    deserializer = AppPreferencesEntity.serializer(),
                    string = input.readBytes().decodeToString()
                )
            } catch (e: SerializationException) {
                e.printStackTrace()
                defaultValue
            }
        }
    }

    override suspend fun writeTo(t: AppPreferencesEntity, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                jsonDeserializer.encodeToString(
                    serializer = AppPreferencesEntity.serializer(),
                    value = t
                ).encodeToByteArray()
            )
        }
    }
}