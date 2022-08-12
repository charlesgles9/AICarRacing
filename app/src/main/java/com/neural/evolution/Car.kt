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
    private val poly= PolyLine()
    private val input= mutableListOf<Double>()
    private val raySize=5000f
    private val max_velocity=8f
    private var velocity=0f
    val neuralNetwork=NeuralNetwork(5+2,4,2)
    val score= mutableListOf<RectF>()
    var crashed=false
    var angle=0f
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

    fun getPoly():PolyLine{
        return poly
    }

   private fun setBounds(){
       poly.reset()
       //top bound
        val top1=rotatePoints(getX()-getWidth()*0.5f,getY()-getHeight()*0.5f,getX(),getY())
        val top2=rotatePoints(getX()+getWidth()*0.5f,getY()-getHeight()*0.5f,getX(),getY())
       poly.moveTo(top1.first,top1.second)
       poly.lineTo(top2.first,top2.second)
       //bottom bound
       val bottom1=rotatePoints(getX()-getWidth()*0.5f,getY()+getHeight()*0.5f,getX(),getY())
       val bottom2=rotatePoints(getX()+getWidth()*0.5f,getY()+getHeight()*0.5f,getX(),getY())
       poly.moveTo(bottom1.first,bottom1.second)
       poly.lineTo(bottom2.first,bottom2.second)
       //left bound
       val left1=rotatePoints(getX()-getWidth()*0.5f,getY()-getHeight()*0.5f,getX(),getY())
       val left2=rotatePoints(getX()-getWidth()*0.5f,getY()+getHeight()*0.5f,getX(),getY())
       poly.moveTo(left1.first,left1.second)
       poly.lineTo(left2.first,left2.second)
       //right bound
       val right1=rotatePoints(getX()+getWidth()*0.5f,getY()-getHeight()*0.5f,getX(),getY())
       val right2=rotatePoints(getX()+getWidth()*0.5f,getY()+getHeight()*0.5f,getX(),getY())
       poly.moveTo(right1.first,right1.second)
       poly.lineTo(right2.first,right2.second)
       poly.setColor(ColorRGBA.green)

    }

    private fun rotatePoints(x:Float,y:Float,centerX:Float,centerY:Float ):Pair<Float,Float>{
        val radians=Math.toRadians(angle.toDouble()).toFloat()
        val nX=centerX+(x-centerX)*cos(radians)-(y-centerY)* sin(radians)
        val nY=centerY+(x-centerX)* sin(radians)+(y-centerY)* cos(radians)
        return Pair(nX,nY)
    }

    private fun applyVelocity(){
        val radians=Math.toRadians(angle.toDouble()).toFloat()
        set(getX()+velocity* cos(radians),getY()+velocity* sin(radians))
    }

    private fun steering(dir:Int){

        if(dir==0){
            angle-=1
        }else
            angle+=1
    }

     fun reset(){
         set(startX,startY)
         crashed=false
    }

    override fun draw(batch: Batch) {
        /*rays.forEach {
            it.draw(batch)
        }*/
       // batch.draw(poly)
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
            it.pAngle=-angle
            it.project(raySize,getX(),getY())
        }
        collision()
        rays.forEach {
            input.add(it.getDistance())
        }
        input.add(velocity.toDouble())
        input.add(angle.toDouble())

        val output=neuralNetwork.predict(input)

        if(output[0]>=0.5f){
            steering(0)
        }else {
            steering(1)
        }

        //direction faced by the agent
        setRotationZ(rays[2].pAngle)
        velocity=max_velocity- max_velocity*output[1].toFloat()
        input.clear()

    }

}