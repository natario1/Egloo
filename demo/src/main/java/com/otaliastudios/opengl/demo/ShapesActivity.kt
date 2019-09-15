package com.otaliastudios.opengl.demo

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.RectF
import android.opengl.GLES20
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.otaliastudios.opengl.core.EglCore
import com.otaliastudios.opengl.draw.*
import com.otaliastudios.opengl.program.GlFlatProgram
import com.otaliastudios.opengl.scene.GlScene
import com.otaliastudios.opengl.surface.EglWindowSurface
import kotlin.math.roundToInt

class ShapesActivity : AppCompatActivity() {

    private lateinit var surfaceView: SurfaceView
    private var eglCore: EglCore? = null
    private var eglSurface: EglWindowSurface? = null
    private var flatProgram: GlFlatProgram? = null

    private val scene = GlScene()
    private val roundRect = GlRoundRect()
    private val triangle = GlTriangle()
    private val circle = GlCircle()

    private val rectF = RectF()

    private val drawAnimator = ValueAnimator.ofFloat(0F, 1F).also {
        it.repeatCount = ValueAnimator.INFINITE
        it.repeatMode = ValueAnimator.REVERSE
        it.duration = 1200
        it.interpolator = FastOutSlowInInterpolator()
        it.addUpdateListener { draw() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shapes)

        surfaceView = findViewById(R.id.surface_view)
        surfaceView.setZOrderOnTop(true)
        surfaceView.holder.setFormat(PixelFormat.RGBA_8888)
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder?) {
                onSurfaceCreated()
            }

            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                GLES20.glViewport(0, 0, width, height)
                scene.setViewportSize(width, height)
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                onSurfaceDestroyed()
            }
        })
    }

    private fun onSurfaceCreated() {
        Log.e("SHAPES", "CREATED.")
        eglCore = EglCore()
        eglSurface = EglWindowSurface(eglCore!!, surfaceView.holder.surface!!)
        eglSurface!!.makeCurrent()
        flatProgram = GlFlatProgram()
        drawAnimator.start()
    }

    private fun onSurfaceDestroyed() {
        Log.e("SHAPES", "DESTROYING.")
        drawAnimator.cancel()
        flatProgram?.release()
        eglSurface?.release()
        eglCore?.release()
        flatProgram = null
        eglSurface = null
        eglCore = null
    }

    private fun draw() {
        Log.w("SHAPES", "drawing.")
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // Animate the background rect
        rectF.bottom = floatValue(-0.4F, -1F)
        rectF.left = floatValue(-0.4F, -1F)
        rectF.top = floatValue(0.4F, 1F)
        rectF.right = floatValue(0.4F, 1F)
        roundRect.setRect(rectF)
        roundRect.setCornersPx(intValue(50, 0))
        // Animate the color
        val roundRectStart = ContextCompat.getColor(this, R.color.roundRectStart)
        val roundRectEnd = ContextCompat.getColor(this, R.color.roundRectEnd)
        flatProgram!!.setColor(colorValue(roundRectStart, roundRectEnd))
        // Draw
        scene.draw(flatProgram!!, roundRect)

        // Draw the triangle.
        val triangleColor = ContextCompat.getColor(this, R.color.triangle)
        flatProgram!!.setColor(triangleColor)
        triangle.rotation += 3
        triangle.radius = floatValue(0.15F, 0.3F)
        scene.draw(flatProgram!!, triangle)

        // Draw the circle.
        val circleColor = ContextCompat.getColor(this, R.color.circle)
        flatProgram!!.setColor(circleColor)
        circle.radius = floatValue(0.15F, 0F)
        scene.draw(flatProgram!!, circle)

        // Publish.
        eglSurface!!.swapBuffers()
    }

    @ColorInt
    private fun colorValue(@ColorInt start: Int, @ColorInt end: Int): Int {
        return Color.rgb(
                intValue(Color.red(start), Color.red(end)),
                intValue(Color.green(start), Color.green(end)),
                intValue(Color.blue(start), Color.blue(end))
        )
    }

    private fun intValue(start: Int, end: Int): Int {
        return floatValue(start.toFloat(), end.toFloat()).roundToInt()
    }

    private fun floatValue(start: Float, end: Float): Float {
        return start + drawAnimator.animatedFraction * (end - start)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_shapes, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        startActivity(Intent(this, VideoActivity::class.java))
        onSurfaceDestroyed()
        finish()
        return true
    }
}
