package com.otaliastudios.opengl.demo

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.RectF
import android.opengl.GLES20
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.otaliastudios.opengl.core.EglCore
import com.otaliastudios.opengl.draw.GlRect
import com.otaliastudios.opengl.program.GlFlatProgram
import com.otaliastudios.opengl.scene.GlScene
import com.otaliastudios.opengl.surface.EglWindowSurface
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var surfaceView: SurfaceView
    private var eglCore: EglCore? = null
    private var eglSurface: EglWindowSurface? = null
    private var eglFlatProgram: GlFlatProgram? = null

    private val eglScene = GlScene()
    private val eglRect = GlRect()
    private val rect = RectF()

    private val drawAnimator = ValueAnimator.ofFloat(0F, 1F).also {
        it.repeatCount = ValueAnimator.INFINITE
        it.repeatMode = ValueAnimator.REVERSE
        it.duration = 1200
        it.interpolator = FastOutSlowInInterpolator()
        it.addUpdateListener { draw() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Using a handler just because the holder callback runs inside a try-catch.
        // We prefer to crash if there's something wrong.
        val handler = Handler()
        surfaceView = findViewById(R.id.surface_view)
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder?) {
                handler.post { onSurfaceCreated() }
            }

            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                GLES20.glViewport(0, 0, width, height)
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                handler.post { onSurfaceDestroyed() }
            }
        })
    }

    private fun onSurfaceCreated() {
        eglCore = EglCore()
        eglSurface = EglWindowSurface(eglCore!!, surfaceView.holder.surface!!)
        eglSurface!!.makeCurrent()
        eglFlatProgram = GlFlatProgram()
        drawAnimator.start()
    }

    private fun onSurfaceDestroyed() {
        drawAnimator.cancel()
        eglFlatProgram?.release()
        eglSurface?.release()
        eglCore?.release()
        eglFlatProgram = null
        eglSurface = null
        eglCore = null
    }

    private fun draw() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // Animate the rect.
        rect.bottom = floatValue(-0.4F, -1F)
        rect.left = floatValue(-0.4F, -1F)
        rect.top = floatValue(0.4F, 1F)
        rect.right = floatValue(0.4F, 1F)
        eglRect.setVertexArray(rect)

        // Animate the color.
        eglFlatProgram!!.setColor(Color.rgb(
                intValue(0, 50),
                intValue(150, 250),
                intValue(100, 150)
        ))

        // Draw and publish.
        eglScene.draw(eglFlatProgram!!, eglRect)
        eglSurface!!.swapBuffers()
    }

    private fun intValue(start: Int, end: Int): Int {
        return floatValue(start.toFloat(), end.toFloat()).roundToInt()
    }

    private fun floatValue(start: Float, end: Float): Float {
        return start + drawAnimator.animatedFraction * (end - start)
    }
}
