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
import android.view.Menu
import android.view.MenuItem
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.otaliastudios.opengl.core.EglCore
import com.otaliastudios.opengl.draw.*
import com.otaliastudios.opengl.program.GlFlatProgram
import com.otaliastudios.opengl.scene.GlScene
import com.otaliastudios.opengl.surface.EglWindowSurface
import kotlin.math.roundToInt

class VideoActivity : AppCompatActivity() {

    private lateinit var surfaceView: SurfaceView
    private var eglCore: EglCore? = null
    private var eglSurface: EglWindowSurface? = null

    private val scene = GlScene()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

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
        eglCore = EglCore()
        eglSurface = EglWindowSurface(eglCore!!, surfaceView.holder.surface!!)
        eglSurface!!.makeCurrent()
    }

    private fun onSurfaceDestroyed() {
        eglSurface?.release()
        eglCore?.release()
        eglSurface = null
        eglCore = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_video, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        startActivity(Intent(this, ShapesActivity::class.java))
        onSurfaceDestroyed()
        finish()
        return true
    }
}
