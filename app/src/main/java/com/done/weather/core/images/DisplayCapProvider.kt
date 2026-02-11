package info.verifeye.vgps.core.images

import android.content.Context
import kotlin.math.max

object DisplayCapProvider {
    fun capPx(context: Context): Int {
        val dm = context.resources.displayMetrics
        return max(dm.widthPixels, dm.heightPixels)
    }
}