package com.neural.evolution

import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.Update
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.maths.Vector2f
import com.graphics.glcanvas.engine.structures.PolyLine
import com.graphics.glcanvas.engine.structures.RectF
import com.neural.evolution.ai.NeuralNetwork
import com.neural.evolution.algebra.Collision
import com.neural.evolution.utils.Timer
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class Car(private val wall:PolyLine,private val startX:Float,private val startY:Float,width:Float,height:Float):RectF(startX, startY, width, height),Update{

    private val rays= mutableListOf<Ray>()
    private val bounds= MutableList(4,init = {Ray()})
    private val input= mutableListOf<Double>()
    private val raySize=5000f
    private val max_velocity=5f
    private var velocity=0f

    val neuralNetwork=NeuralNetwork(5+2,8,2)

     val score= mutableListOf<RectF>()
     var crashed=false
    init {
        val angles= arrayOf(180f,135f,90f,45f,0f)
        angles.forEach { angle->
            val ray=Ray()
            ray.setColor(ColorRGBA.red)
            ray.angle=angle
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
        set(getX()+velocity* cos(radians),getY()+velocity* sin(radians))
    }

    private fun steering(dir:Int){
        val rotation=getRotationZ()
        if(dir==0){
            setRotationZ( (rotation-1f)%360f)
        }else
            setRotationZ((rotation+1f)%360f)
    }

     fun reset(){
         score.clear()
         set(startX,startY)
         crashed=false
    }

    override fun draw(batch: Batch) {

        rays.forEach {
            it.draw(batch)
        }
       bounds.forEach {
           it.draw(batch)
       }
        batch.draw(this)
    }


    private fun collision(){
        // ray to wall collision
        for(ray1 in getRays()) {
            for(path in wall.getPaths()){
                for(end in path.getEndPoints()){
                    val d= Collision.detect_line_collision(ray1.getStartX(),ray1.getStartY(),ray1.getStopX(),ray1.getStopY(),
                        path.getStart().x,path.getStart().y,end.x,end.y)
                    if (Collision.do_lines_intersect(d)) {
                        Collision.setInterSectionPoint(d, ray1)
                    }
                }
            }
        }
    }
    override fun update(delta: Long) {
        super.update(delta)
        setBounds()
        applyVelocity()

        //reset the ray project every frame
        rays.forEach {
            it.pAngle=-getRotationZ()
            it.project(raySize,getX(),getY())
        }
        collision()
        rays.forEach {
            input.add(it.getDistance())
        }
        input.add(velocity.toDouble())
        input.add(getRotationZ().toDouble())

        val output=neuralNetwork.predict(input)

        if(output[0]>=0.5f){
            steering(0)
        }else
            steering(1)

        velocity=max_velocity- max_velocity*output[1].toFloat()

        input.clear()



    }

}