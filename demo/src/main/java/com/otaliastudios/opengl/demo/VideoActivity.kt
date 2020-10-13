package com.otaliastudios.opengl.demo

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.net.Uri
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoListener
import com.otaliastudios.opengl.core.EglConfigChooser
import com.otaliastudios.opengl.core.EglContextFactory
import com.otaliastudios.opengl.draw.*
import com.otaliastudios.opengl.extensions.*
import com.otaliastudios.opengl.program.GlTextureProgram
import com.otaliastudios.opengl.scene.GlScene
import com.otaliastudios.opengl.texture.GlTexture
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class VideoActivity : AppCompatActivity(), GLSurfaceView.Renderer {

    private lateinit var glSurfaceView: GLSurfaceView
    private var glTextureProgram: GlTextureProgram? = null
    private var surfaceTexture: SurfaceTexture? = null
    private var surface: Surface? = null

    private val scene = GlScene()
    private val glRect = GlRect()
    private val glRoundRect = GlRoundRect().apply { setCornersPx(200) }
    private val glMesh = Gl2dMesh().also {
        it.setPoints(listOf(
                PointF(0F, 0F),
                PointF(0F, 1F),
                PointF(1F, 0F),
                PointF(0F, -1F),
                PointF(-1F, 0F),
                PointF(0.9F, 0.7F),
                PointF(0.7F, 0.9F),
                PointF(-0.9F, -0.7F),
                PointF(-0.7F, -0.9F),
                PointF(0.9F, -0.7F),
                PointF(0.7F, -0.9F),
                PointF(-0.9F, 0.7F),
                PointF(-0.7F, 0.9F)
        ))
    }

    private val glDrawables = listOf(glRect, glRoundRect, glMesh)
    private var glDrawable = 0
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
                // .createMediaSource(Uri.parse("https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"))
                .createMediaSource(Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"))
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
            glDrawable++
            if (glDrawable > glDrawables.lastIndex) {
                glDrawable = 0
            }
            false
        }
        Toast.makeText(this, "Touch the screen to change shape.", Toast.LENGTH_LONG).show()
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        // Configure GL
        val glTexture = GlTexture()
        glTextureProgram = GlTextureProgram()
        glTextureProgram!!.texture = glTexture

        // Configure the player
        surfaceTexture = SurfaceTexture(glTexture.id)
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

    private var animation = 0F
    private val animationStep = 0.009F

    override fun onDrawFrame(gl: GL10) {
        val texture = surfaceTexture ?: return
        val program = glTextureProgram ?: return
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        texture.updateTexImage()
        texture.getTransformMatrix(program.textureTransform)

        val drawable = glDrawables[glDrawable]
        drawable.modelMatrix.clear()

        animation += animationStep
        if (animation > 1F) animation = animationStep
        Log.e("DEBUG", "Animation=$animation")
        val zoom = animation * 2F + 1F
        val scale = 1F / zoom
        program.zoom(zoom = zoom, focusX = 0F, focusY = 0F)
        program.revertZoom(zoom = zoom, focusX = 0F, focusY = 0F)

        // Scale works
        // program.scale(scale = scale, pivotX = 1F, pivotY = 0.5F)
        // drawable.scale(scale = 1F / scale, pivotX = 1F, pivotY = 0.5F)
        scene.draw(program, drawable)

    }

    // Works
    private fun GlDrawable.zoom(zoom: Float, focusX: Float, focusY: Float) {
        Log.e("DEBUG", "DRAWABLE_ZOOM zoom=$zoom")
        val transformedFocusX = focusX * 2F - 1F // -1..1
        val transformedFocusY = (1 - focusY) * 2F - 1F // -1..1
        // order matters
        modelMatrix.scale(x = zoom, y = zoom)
        modelMatrix.translate(x = -transformedFocusX, y = -transformedFocusY)
    }

    private fun GlDrawable.revertZoom(zoom: Float, focusX: Float, focusY: Float) {
        // Scale is enough in case of 0.5,0.5 (0,0)
        val transformedFocusX = focusX * 2F - 1F // -1..1
        val transformedFocusY = (1 - focusY) * 2F - 1F // -1..1
        modelMatrix.translate(x = transformedFocusX, y = transformedFocusY)
        modelMatrix.scale(x = 1F / zoom, y = 1F / zoom)
    }



    // Works
    private fun GlTextureProgram.zoom(zoom: Float, focusX: Float, focusY: Float) {
        Log.e("DEBUG", "TEXTURE_ZOOM zoom=$zoom")
        val newOriginX = focusX - (1F / zoom) / 2F
        val newOriginY = (1F - focusY) - (1F / zoom) / 2F
        textureTransform.translate(x = newOriginX, y = newOriginY)
        textureTransform.scale(x = 1F / zoom, y = 1F / zoom)
    }

    private fun GlTextureProgram.revertZoom(zoom: Float, focusX: Float, focusY: Float) {
        val newOriginX = focusX - (1F / zoom) / 2F
        val newOriginY = (1F - focusY) - (1F / zoom) / 2F
        textureTransform.scale(x = zoom, y = zoom)
        textureTransform.translate(x = -newOriginX, y = -newOriginY)
    }





    // Works
    private fun GlTextureProgram.scale(scale: Float, pivotX: Float, pivotY: Float) {
        Log.e("DEBUG", "TEXTURE_SCALE scale=$scale")
        val flippedY = 1F - pivotY
        textureTransform.translate(x = pivotX, y = flippedY)
        textureTransform.scale(x = scale, y = scale)
        textureTransform.translate(x = -pivotX, y = -flippedY)
    }

    // Works
    private fun GlDrawable.scale(scale: Float, pivotX: Float, pivotY: Float) {
        val pivotX = pivotX * 2F - 1F // -1..1
        val pivotY = (1 - pivotY) * 2F - 1F // -1..1
        Log.e("DEBUG", "DRAWABLE_SCALE scale=$scale pivotX=$pivotX pivotY=$pivotY")
        modelMatrix.translate(x = pivotX, y = pivotY)
        modelMatrix.scale(x = 1F / scale, y = 1F / scale)
        modelMatrix.translate(x = -pivotX, y = -pivotY)
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
        val viewport: RectF
        if (videoRatio > surfaceRatio) {
            // Video is wider. We should collapse height.
            val surfaceRealHeight = surfaceWidth / videoRatio
            val surfaceRealHeightEgloo = surfaceRealHeight / surfaceHeight * 2
            viewport = RectF(-1F, surfaceRealHeightEgloo / 2F,
                    1F, -surfaceRealHeightEgloo / 2F)
        } else if (videoRatio < surfaceRatio) {
            // Video is taller. We should collapse width
            val surfaceRealWidth = surfaceHeight * videoRatio
            val surfaceRealWidthEgloo = surfaceRealWidth / surfaceWidth * 2
            viewport = RectF(-surfaceRealWidthEgloo / 2F, 1F,
                    surfaceRealWidthEgloo / 2F, -1F)
        } else {
            viewport = RectF(-1F, 1F, 1F, -1F)
        }
        // glRect.setRect(viewport)
        // glRoundRect.setRect(viewport)
        glMesh.modelMatrix.makeIdentity()
        glMesh.modelMatrix.scaleX(viewport.width() / 2F)
        glMesh.modelMatrix.scaleY(-viewport.height() / 2F)
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
