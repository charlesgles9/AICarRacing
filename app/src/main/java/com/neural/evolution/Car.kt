package com.neural.evolution

import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.Update
import com.graphics.glcanvas.engine.structures.RectF
import com.neural.evolution.ai.NeuralNetwork

class Car(x:Float,y:Float,width:Float,height:Float):RectF(x, y, width, height),Update{

    private val rays= mutableListOf<Ray>()
    private val neuralNetwork=NeuralNetwork(360/45,8,3)
    private val raySize=4000f
    init {
        for(angle in 0 until 360 step 45){
            val ray=Ray()
            ray.setRotationZ(angle.toFloat())
            ray.showEdge=true
        }
    }


    fun getRays():MutableList<Ray>{
        return rays
    }
    override fun draw(batch: Batch) {

        rays.forEach {
            it.draw(batch)
        }
        batch.draw(this)
    }


    override fun update(delta: Long) {
        super.update(delta)
        //reset the ray project every frame
        rays.forEach {
            it.project(raySize,getX(),getY())
        }

    }




}