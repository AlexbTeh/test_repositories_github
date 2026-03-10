package com.done.weather.data.vision

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

class PersonDetector(
    appContext: Context
) {

    sealed class Result {
        data class Success(
            val boxes: List<Rect>,
            val imageWidth: Int,
            val imageHeight: Int
        ) : Result()

        data class Error(val message: String) : Result()
    }

    private companion object {
        // Подкрути если надо:
        // 0.45..0.70 (чем выше — тем меньше ложных срабатываний)
        const val PERSON_MIN_SCORE = 0.55f
    }

    private val detector: ObjectDetector

    init {
        val base = BaseOptions.builder()
            .setModelAssetPath("efficientdet_lite0.tflite") // app/src/main/assets/
            .build()

        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setBaseOptions(base)
            .setRunningMode(RunningMode.IMAGE)
            .setMaxResults(10)
            // общий порог — не слишком низкий
            .setScoreThreshold(0.30f)
            .build()

        detector = ObjectDetector.createFromOptions(appContext, options)
    }

    fun detectPeople(imageProxy: ImageProxy): Result {
        return try {
            val width = imageProxy.width
            val height = imageProxy.height

            val mpImage = imageProxyToMpImage(imageProxy)
            val res: ObjectDetectorResult = detector.detect(mpImage)

            val rects = res.detections()
                .filter { det ->
                    // Берём только категории "person" с нормальным score
                    det.categories().any { c ->
                        val name = c.categoryName()?.trim().orEmpty()
                        name.equals("person", ignoreCase = true) &&
                                c.score() >= PERSON_MIN_SCORE
                    }
                }
                .map { det ->
                    val b = det.boundingBox()
                    Rect(
                        b.left.roundToInt(),
                        b.top.roundToInt(),
                        b.right.roundToInt(),
                        b.bottom.roundToInt()
                    )
                }

            // ✅ ВАЖНО: никакого fallback на "all detections"
            Result.Success(rects, width, height)
        } catch (t: Throwable) {
            Result.Error(t.message ?: t.javaClass.simpleName)
        }
    }

    private fun imageProxyToMpImage(image: ImageProxy): MPImage {
        val bitmap = imageProxyToBitmapNv21(image)
        return BitmapImageBuilder(bitmap).build()
    }

    private fun imageProxyToBitmapNv21(image: ImageProxy): Bitmap {
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 85, out)
        val jpegBytes = out.toByteArray()

        return BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.size)
    }
}
