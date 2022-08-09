package com.neural.evolution

import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.Update
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.structures.Circle
import com.graphics.glcanvas.engine.structures.Line
import kotlin.math.cos
import kotlin.math.sin

class Ray(startX:Float,startY:Float,stopX:Float,stopY:Float):Line(startX, startY, stopX, stopY),Update {

      private val edge=Circle(stopX,stopY,5f)
      var angle=0f
      var pAngle=0f
      var showEdge=false
      init {
        edge.setColor(ColorRGBA.red)
      }


    constructor():this(0f,0f,0f,0f){

    }


    fun project(distance:Float,startX:Float,startY: Float){
        val radians=Math.toRadians(angle.toDouble()+pAngle).toFloat()
        setStopX(startX+distance* sin(radians))
        setStopY(startY+distance* cos(radians))
        setStartX(startX)
        setStartY(startY)
    }

    override fun draw(batch: Batch) {
        edge.set(getStopX(),getStopY())
           batch.draw(this)
          if(showEdge)
           batch.draw(edge)
       // println(" "+getStopX()+" "+getStopY())
    }

    override fun update(delta: Long) {

    }

}