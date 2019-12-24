package com.otaliastudios.opengl.demo

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.net.Uri
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Toast
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoListener
import com.otaliastudios.opengl.core.EglConfigChooser
import com.otaliastudios.opengl.core.EglContextFactory
import com.otaliastudios.opengl.core.EglCore
import com.otaliastudios.opengl.draw.*
import com.otaliastudios.opengl.program.GlFlatProgram
import com.otaliastudios.opengl.program.GlTextureProgram
import com.otaliastudios.opengl.scene.GlScene
import com.otaliastudios.opengl.surface.EglWindowSurface
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.roundToInt

class VideoActivity : AppCompatActivity(), GLSurfaceView.Renderer {

    private lateinit var glSurfaceView: GLSurfaceView
    private var glTextureProgram: GlTextureProgram? = null
    private var surfaceTexture: SurfaceTexture? = null
    private var surface: Surface? = null

    private val scene = GlScene()
    private val glRect = GlRect()
    private val glRoundRect = GlRoundRect().apply { setCornersPx(200) }
    private var glDrawable: GlDrawable = glRect
    private lateinit var player: SimpleExoPlayer

    private var videoWidth = -1
    private var videoHeight = -1
    private var surfaceWidth = -1
    private var surfaceHeight = -1

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        // Set up the player
        player = ExoPlayerFactory.newSimpleInstance(this)
        val dataSourceFactory = DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "Egloo"))
        val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse("https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"))
        player.prepare(videoSource)
        player.playWhenReady = true

        // Set up the gl surface view
        glSurfaceView = findViewById(R.id.gl_surface_view)
        glSurfaceView.setZOrderOnTop(true)
        glSurfaceView.holder.setFormat(PixelFormat.RGBA_8888)
        glSurfaceView.setEGLContextFactory(EglContextFactory.GLES2)
        glSurfaceView.setEGLConfigChooser(EglConfigChooser.GLES2)
        glSurfaceView.setRenderer(this)
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        glSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder?) {}
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                onSurfaceDestroyed()
            }
        })

        // Address the aspect ratio
        player.addVideoListener(object: VideoListener {
            override fun onVideoSizeChanged(width: Int, height: Int,
                                            unappliedRotationDegrees: Int,
                                            pixelWidthHeightRatio: Float) {
                videoWidth = width
                videoHeight = height
                onVideoOrSurfaceSizeChanged()
            }
        })

        // On touch, change the current drawable.
        glSurfaceView.setOnTouchListener { v, event ->
            glDrawable = if (glDrawable == glRect) glRoundRect else glRect
            false
        }
        Toast.makeText(this, "Touch the screen to change shape.", Toast.LENGTH_LONG).show()
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        // Configure GL
        glTextureProgram = GlTextureProgram()

        // Configure the player
        surfaceTexture = SurfaceTexture(glTextureProgram!!.textureId)
        surfaceTexture!!.setOnFrameAvailableListener {
            glSurfaceView.requestRender()
        }
        surface = Surface(surfaceTexture!!)
        glSurfaceView.post {
            player.setVideoSurface(surface)
        }
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        scene.setViewportSize(width, height)
        surfaceWidth = width
        surfaceHeight = height
        onVideoOrSurfaceSizeChanged()
    }

    override fun onDrawFrame(gl: GL10) {
        val texture = surfaceTexture ?: return
        val program = glTextureProgram ?: return
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        texture.updateTexImage()
        texture.getTransformMatrix(program.textureTransform)
        scene.draw(program, glDrawable)
    }

    private fun onSurfaceDestroyed() {
        player.setVideoSurface(null)
        glTextureProgram?.release()
        glTextureProgram = null
        surfaceTexture?.release()
        surfaceTexture = null
        surface?.release()
        surface = null
    }

    private fun onVideoOrSurfaceSizeChanged() {
        if (videoWidth == -1 || videoHeight == -1) return
        if (surfaceWidth == -1 || surfaceHeight == -1) return
        val videoRatio = videoWidth.toFloat() / videoHeight
        val surfaceRatio = surfaceWidth.toFloat() / surfaceHeight
        val rect: RectF
        if (videoRatio > surfaceRatio) {
            // Video is wider. We should collapse height.
            val surfaceRealHeight = surfaceWidth / videoRatio
            val surfaceRealHeightEgloo = surfaceRealHeight / surfaceHeight * 2
            rect = RectF(-1F, surfaceRealHeightEgloo / 2F,
                    1F, -surfaceRealHeightEgloo / 2F)
        } else if (videoRatio < surfaceRatio) {
            // Video is taller. We should collapse width
            val surfaceRealWidth = surfaceHeight * videoRatio
            val surfaceRealWidthEgloo = surfaceRealWidth / surfaceWidth * 2
            rect = RectF(-surfaceRealWidthEgloo / 2F, 1F,
                    surfaceRealWidthEgloo / 2F, -1F)
        } else {
            rect = RectF(-1F, 1F, 1F, -1F)
        }
        glRect.setVertexArray(rect)
        glRoundRect.setRect(rect)
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

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}
