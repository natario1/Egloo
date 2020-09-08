package bug

import com.otaliastudios.opengl.core.EglCore
import com.otaliastudios.opengl.draw.GlRoundRect

fun intermediate() = com.otaliastudios.opengl.core.EglCore.FLAG_RECORDABLE.also {
    GlRoundRect()
    EglCore()
}