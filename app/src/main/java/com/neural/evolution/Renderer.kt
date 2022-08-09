package com.neural.evolution

import android.content.Context
import android.opengl.GLES32
import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.Camera2D
import com.graphics.glcanvas.engine.GLRendererView
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.structures.Line
import com.neural.evolution.algebra.Collision

class Renderer(private val context: Context,width:Float,height:Float):GLRendererView(width, height) {

   private val track=Track()
   private val batch=Batch()
   private val camera=Camera2D(10f)
   private val car=Car(100f,100f,25f,25f)
    override fun prepare() {
         batch.initShader(context)
         camera.setOrtho(getCanvasWidth(), getCanvasHeight())
         car.setColor(ColorRGBA.blue)
    }

    override fun draw() {
       GLES32.glClear(GLES32.GL_DEPTH_BUFFER_BIT or GLES32.GL_COLOR_BUFFER_BIT)
       GLES32.glClearColor(0.0f,0.0f,0.0f,1f)
        batch.begin(camera)
        track.getBlocks().forEach {
          //  it.draw(batch)
        }
        track.getBorderLine().forEach {
            batch.draw(it)
        }
        car.draw(batch)
        batch.end()

    }


    override fun update(delta: Long) {
           car.update(delta)
        for(ray1 in car.getRays())
        for(ray2 in track.getBorderLine()){
            val d=Collision.detect_line_collision(ray1,ray2)
            if(Collision.do_lines_intersect(d)){
                Collision.setInterSectionPoint(d,ray1)
            }
        }
    }


    //clear resources here
    override fun onRelease() {

    }
}