package com.neural.evolution

import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.Update
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.maths.Vector2f
import com.graphics.glcanvas.engine.structures.RectF
import com.neural.evolution.ai.NeuralNetwork
import kotlin.math.cos
import kotlin.math.sin

class Car(private val startX:Float,private val startY:Float,width:Float,height:Float):RectF(startX, startY, width, height),Update{

    private val rays= mutableListOf<Ray>()
    private val bounds= MutableList(4,init = {Ray()})
    private val neuralNetwork=NeuralNetwork(360/45,10,2)
    private val input= mutableListOf<Double>()
    private val raySize=5000f
    private val max_velocity=2f
    private val velocity=Vector2f(0f,0f)
     val crashed=false
    init {
        val maxAngle=360
        for(angle in 0 until maxAngle step 45){
            val ray=Ray()
            ray.setColor(ColorRGBA.red)
            ray.angle=angle.toFloat()
            ray.showEdge=true
            rays.add(ray)
        }

    }


    fun getRays():MutableList<Ray>{
        return rays
    }

    fun getBounds():MutableList<Ray>{
        return bounds
    }

   private fun setBounds(){
        //top bound
        bounds[0].set(getX()-getWidth()*0.5f,getY()-getHeight()*0.5f,getX()+getWidth()*0.5f,getY()-getHeight()*0.5f)
        //bottom bound
        bounds[1].set(getX()-getWidth()*0.5f,getY()+getHeight()*0.5f,getX()+getWidth()*0.5f,getY()+getHeight()*0.5f)
        //left bound
        bounds[2].set(getX()-getWidth()*0.5f,getY()-getHeight()*0.5f,getX()-getWidth()*0.5f,getY()+getHeight()*0.5f)
        //right bound
        bounds[3].set(getX()+getWidth()*0.5f,getY()-getHeight()*0.5f,getX()+getWidth()*0.5f,getY()+getHeight()*0.5f)

    }

    private fun applyVelocity(){
        val radians=Math.toRadians(getRotationZ().toDouble()).toFloat()
        set(getX()+velocity.x* sin(radians),getY()+velocity.y* cos(radians))
    }

     fun reset(){
         set(startX,startY)
    }

    override fun draw(batch: Batch) {

        rays.forEach {
            it.draw(batch)
        }
       bounds.forEach {
           it.draw(batch)
       }
       // batch.draw(this)
    }


    override fun update(delta: Long) {
        super.update(delta)
        setBounds()
        applyVelocity()
        //reset the ray project every frame
        rays.forEach {
            it.pAngle=getRotationZ()
            it.project(raySize,getX(),getY())
            input.add((it.angle+it.pAngle)%360.0)
        }

        val output=neuralNetwork.predict(input)
        if(output[0]>=0.5f){
            setRotationZ(getRotationZ()+1f)
        }else{
            setRotationZ(getRotationZ()-1f)
        }

        if(output[1]<=0.5f){
            velocity.set(max_velocity,max_velocity)
        }else{
            velocity.set(0f,0f)
        }


        input.clear()


    }

}