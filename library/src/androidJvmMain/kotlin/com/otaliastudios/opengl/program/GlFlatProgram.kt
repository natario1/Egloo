package com.otaliastudios.opengl.program

import android.graphics.Color
import androidx.annotation.ColorInt

public actual class GlFlatProgram : GlNativeFlatProgram() {

    public fun setColor(@ColorInt color: Int) {
        this.color = floatArrayOf(
                Color.red(color) / 255F,
                Color.green(color) / 255F,
                Color.blue(color) / 255F,
                Color.alpha(color) / 255F
        )
    }
}