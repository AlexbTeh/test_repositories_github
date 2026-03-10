package com.done.weather.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.text.TextUtils
import com.done.weather.AppConstant
import kotlinx.datetime.Instant
import timber.log.Timber
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.util.Locale


object ResourceUtil {
    private const val TAG = "ResourceUtil"

    fun fileExist(path: String) = runCatching {
        val file = File(path)
        file.exists()
    }.getOrDefault(false)

    /**
     * Clear all data of application
     */
    fun clearApplicationData(context: Context) {
        Timber.tag(TAG).d("Clearing application data")
        runCatching {
            context.cacheDir?.deleteRecursively()
            context.externalCacheDir?.deleteRecursively()
            context.getExternalFilesDir(null)?.deleteRecursively()

            val filesDir = context.filesDir
            filesDir.listFiles()?.forEach { file ->
                if (file.name != "datastore") {
                    Timber.tag(TAG).d("Removing file: ${file.name}")
                    file.deleteRecursively()
                }
            }

            val datastoreDir = File(filesDir, "datastore")
            datastoreDir.listFiles()?.forEach { file ->
                if (file.name.startsWith("app_base_settings").not()) {
                    Timber.tag(TAG).d("Removing datastore file: ${file.name}")
                    file.deleteRecursively()
                }
            }

            val sharedPrefsDir = File(context.filesDir.parent, "MyAppPreferences")
            sharedPrefsDir.deleteRecursively()

            val applicationDirectory = File(context.cacheDir.parent ?: return)
            applicationDirectory.listFiles()?.forEach { file ->
                Timber.tag(TAG).d("Removing file: ${file.name}")

                if (file.name != "lib" && file.name != "databases" && file.name != "files") {
                    file.deleteRecursively()
                }
            }
        }.onFailure {
            Timber.tag(TAG).e(it, "Failed to clear application data")
        }
    }

    /**
     * Clear cache data
     */
    fun clearCacheData(context: Context) {
        val appCacheDirectory = context.appDataDirectory
        deleteFile(appCacheDirectory.absolutePath, null)
    }

    /**
     * Delete all Files
     */
    fun deleteFile(filePath: String, extension: String?): Boolean {
        return runCatching {
            if (!fileExist(filePath)) return true
            var deletedAll = true
            val file = File(filePath)
            if (file.isDirectory()) {
                val children = file.list()
                if (children != null) {
                    for (child in children) {
                        val path = file.absolutePath + "/" + child
                        deletedAll = deleteFile(path, extension) && deletedAll
                    }
                }
            }
            if (extension.isNullOrBlank()) deletedAll = file.delete()
            else if (!file.isDirectory() && file.getName().endsWith(extension)) deletedAll =
                file.delete()
            return deletedAll
        }.getOrDefault(false)
    }

    @JvmStatic
    fun getFullPathFromCache(context: Context, path: String): String {
        return String.format(Locale.getDefault(), "%s/%s", context.appDataDirectory, path)
    }

    /**
     * Reads a text file from cache and returns its content as a String.
     *
     * @param context The context to use for accessing cache.
     * @param fileName The name of the file to read.
     * @return The content of the file as a String, or null if the file does not exist or an error occurs.
     */
    fun readTextFileFromCache(context: Context, fileName: String): String? {
        return try {
            val file = File(getFullPathFromCache(context, fileName))
            if (!file.exists()) return null

            file.inputStream().bufferedReader(StandardCharsets.UTF_8).use { reader ->
                reader.readText()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Read text file from asset
     */
    fun readTextFileFromAsset(context: Context, fileName: String): String {
        val builder = StringBuilder()
        try {
            context.assets.open(fileName).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, "UTF-8")).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        builder.append(line)
                    }
                }
            }
        } catch (e: IOException) {
            Timber.tag(TAG).e(e, "Failed to read text file from asset: $fileName")
        }
        return builder.toString()
    }

    fun readTextFileFromResDir(filePath: String): String? {
        val fullFilePath = "$appResDir/$filePath"
        if (!fileExist(fullFilePath)) return null
        val builder = StringBuilder()
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(
                InputStreamReader(
                    FileInputStream(fullFilePath),
                    StandardCharsets.UTF_8
                )
            )
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                builder.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return if (builder.isEmpty()) null else builder.toString()
    }

    fun writeTextToResTexFile(text: String, filePath: String): Boolean {
        val fullFilePath = getResFile("/$filePath") ?: return false
        return try {
            BufferedWriter(
                OutputStreamWriter(
                    FileOutputStream(fullFilePath, false),
                    StandardCharsets.UTF_8
                )
            ).use { writer ->
                writer.write(text)
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun getBitmapFromCache(context: Context, fileName: String): Bitmap? {
        val filePath = getFullPathFromCache(context, fileName)
        return runCatching { BitmapFactory.decodeFile(filePath) }.getOrElse {
            Timber.w("getBitmapFromCache failed: $fileName")
            null
        }
    }

    @JvmStatic
    val appResDir: String
        get() {
            val companyDir =
                File(Environment.getExternalStorageDirectory().absolutePath + "/" + AppConstant.COMPANY_DIR)
            if (!companyDir.exists()) companyDir.mkdir()
            return companyDir.absolutePath + "/" + AppConstant.ROOT_DIR
        }

    @JvmStatic
    fun createDirectory(subDirName: String) {
        val appDir = File(appResDir)
        if (!appDir.exists()) appDir.mkdirs()
        val subDir = File(appResDir + subDirName)
        if (!subDir.exists()) subDir.mkdir()
    }

    @JvmStatic
    fun getResFile(fileName: String): String? {
        val tempFile = File(appResDir, fileName)
        Timber.tag(TAG).d("getResFilePath: ${tempFile.path}")

        val parentDir = tempFile.parentFile
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                Timber.tag(TAG).e("Failed to create directories: ${parentDir.path}")
                return null
            }
        }

        if (!tempFile.exists()) {
            try {
                tempFile.parentFile?.mkdirs()
                tempFile.createNewFile()
            } catch (e: IOException) {
                Timber.tag(TAG).e(e, "Failed to create new file: ${tempFile.path}")
                return null
            }
        }
        return tempFile.absolutePath
    }

    @JvmStatic
    fun copyAssetFile(context: Context, assetFilename: String, toFile: String) {
        try {
            context.assets.open(assetFilename).use { inputStream ->
                FileOutputStream(File(toFile)).use { outputStream ->
                    val buffer = ByteArray(1024)
                    var read: Int
                    while (inputStream.read(buffer).also { read = it } != -1) {
                        outputStream.write(buffer, 0, read)
                    }
                }
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "copyAssetFile failed")
        }
    }

    @JvmStatic
    fun copyFile(fromFilePath: String, toFilePath: String) {
        val fromFile = File(fromFilePath)
        val toFile = File(toFilePath)

        try {
            FileInputStream(fromFile).use { fromStream ->
                FileOutputStream(toFile).use { toStream ->
                    fromStream.channel.use { fromChannel ->
                        toStream.channel.use { toChannel ->
                            fromChannel.transferTo(0, fromChannel.size(), toChannel)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "copyFile failed")
        }
    }

    @JvmStatic
    val mediaList: ArrayList<String>
        get() {
            val rootDir = File(appResDir + AppConstant.CAMERA)
            val files = rootDir.listFiles().orEmpty()
            val mediaPaths = ArrayList<String>()
            files.forEach { file ->
                val path = file.absolutePath
                if (path.endsWith(AppConstant.EXT_JPG) || path.endsWith(AppConstant.EXT_3GP) || path.endsWith(
                        AppConstant.EXT_MP4
                    )
                ) mediaPaths.add(path)
            }
            return mediaPaths
        }

    @JvmStatic
    fun getFilenameWithoutExtension(filename: String): String {
        if (TextUtils.isEmpty(filename)) return filename
        val dotIndex = filename.lastIndexOf(".")
        return if (dotIndex < 0) filename else filename.substring(0, dotIndex)
    }

    /**
     * Check if the appDataDirectory exists and not empty
     */
    fun hasCourseFiles(context: Context): Boolean {
        val appDataDirectory = context.appDataDirectory
        return appDataDirectory.exists() && appDataDirectory.listFiles()?.isNotEmpty() == true
    }

}

@SuppressLint("DiscouragedApi")
fun Resources.getDimensionPixelSizeOrNull(name: String, defType: String, defPackage: String): Int? {
    val resId = getIdentifier(name, defType, defPackage)
    return if (resId > 0) getDimensionPixelSize(resId) else null
}

fun epochMsToIsoUtcNoMillis(ms: Long): String {
    val iso = Instant.fromEpochMilliseconds(ms).toString()
    // "2026-01-20T17:48:10.123Z" -> "2026-01-20T17:48:10Z"
    return iso.replace(Regex("\\.\\d{1,9}Z$"), "Z")
}
