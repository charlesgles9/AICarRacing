package com.neural.evolution

import com.graphics.glcanvas.engine.Batch

import com.graphics.glcanvas.engine.structures.RectF

class Block(x:Float,y:Float,width:Float,height:Float):RectF(x,y,width,height) {

     fun draw(batch: Batch) {
         batch.draw(this)
    }


    override fun update(delta:Long){
              super.update(delta)

    }

}