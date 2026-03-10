package com.done.weather.ui.compose_ui.camera_screen_vision

import android.annotation.SuppressLint
import android.content.Context
import android.util.Size
import android.view.ViewGroup
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    lensFacing: Int = CameraSelector.LENS_FACING_BACK,
    onFrame: (ImageProxy) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val executor: Executor = remember { ContextCompat.getMainExecutor(context) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    // PreviewView должен быть 1 и жить столько же, сколько composable
    val previewView = remember {
        createPreviewView(context)
    }

    AndroidView(
        modifier = modifier,
        factory = { previewView },
        update = { /* nothing */ }
    )

    LaunchedEffect(lensFacing) {
        val cameraProvider = cameraProviderFuture.get()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        val preview = Preview.Builder()
            // можно оставить 16:9 как чаще всего на девайсах
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

        val analysis = ImageAnalysis.Builder()
            // чтобы анализ не тормозил превью
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            // можно так, чтобы не было сверх тяжёлого размера
            .setTargetResolution(Size(1280, 720))
            .build().also { useCase ->
                useCase.setAnalyzer(executor) { image ->
                    try {
                        onFrame(image)
                    } finally {
                        // ВАЖНО: если ты в onFrame НЕ закрываешь imageProxy — закрываем тут
                        // Если твой viewModel сам закрывает — тогда убери этот close из finally.
                        image.close()
                    }
                }
            }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                analysis
            )
        } catch (t: Throwable) {
            // если хочешь — пробрось это в viewModel через callback
            t.printStackTrace()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            runCatching {
                cameraProviderFuture.get().unbindAll()
            }
        }
    }
}

@SuppressLint("InflateParams")
private fun createPreviewView(context: Context): PreviewView {
    return PreviewView(context).apply {
        // ✅ главное: заполняем весь контейнер
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // ✅ главное: без “рамок”
        scaleType = PreviewView.ScaleType.FILL_CENTER

        // можно оставить COMPATIBLE (стабильнее) или PERFORMANCE (быстрее)
        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
    }
}
