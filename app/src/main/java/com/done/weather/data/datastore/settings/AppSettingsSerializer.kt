package com.done.weather.data.datastore.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.done.weather.AppConstant
import com.done.weather.data.datastore.settings.entity.AppSettingsEntity
import com.done.weather.di.jsonDeserializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import java.io.InputStream
import java.io.OutputStream

typealias AppSettingsDatastore = DataStore<AppSettingsEntity>

val Context.appSettingsDatastore: AppSettingsDatastore by dataStore(
    AppConstant.SETTINGS_FILENAME,
    AppSettingsSerializer
)

object AppSettingsSerializer : Serializer<AppSettingsEntity> {

    override val defaultValue: AppSettingsEntity
        get() = AppSettingsEntity()

    override suspend fun readFrom(input: InputStream): AppSettingsEntity {
        return withContext(Dispatchers.IO) {
            try {
                jsonDeserializer.decodeFromString(
                    deserializer = AppSettingsEntity.serializer(),
                    string = input.readBytes().decodeToString()
                )
            } catch (e: SerializationException) {
                e.printStackTrace()
                defaultValue
            }
        }
    }

    override suspend fun writeTo(t: AppSettingsEntity, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                jsonDeserializer.encodeToString(
                    serializer = AppSettingsEntity.serializer(),
                    value = t
                ).encodeToByteArray()
            )
        }
    }
}