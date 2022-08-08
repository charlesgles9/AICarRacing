package com.neural.evolution

import android.content.Context
import android.opengl.GLES32
import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.Camera2D
import com.graphics.glcanvas.engine.GLRendererView

class Renderer(private val context: Context,width:Float,height:Float):GLRendererView(width, height) {

   private val track=Track()
   private val batch=Batch()
   private val camera=Camera2D(10f)
    override fun prepare() {
         batch.initShader(context)
         camera.setOrtho(getCanvasWidth(), getCanvasHeight())

    }


    override fun draw() {
       GLES32.glClear(GLES32.GL_DEPTH_BUFFER_BIT or GLES32.GL_COLOR_BUFFER_BIT)
       GLES32.glClearColor(0.0f,0.0f,0.0f,1f)
        batch.begin(camera)
        track.getBlocks().forEach {
            it.draw(batch)
        }
        track.getBorderLine().forEach {
            batch.draw(it)
        }
        batch.end()

    }


    override fun update(delta: Long) {

    }


    //clear resources here
    override fun onRelease() {

    }
}