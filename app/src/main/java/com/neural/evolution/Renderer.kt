package com.neural.evolution

import android.content.Context
import android.opengl.GLES32
import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.BatchQueue
import com.graphics.glcanvas.engine.Camera2D
import com.graphics.glcanvas.engine.GLRendererView
import com.graphics.glcanvas.engine.maths.AxisABB
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.structures.Font
import com.graphics.glcanvas.engine.structures.Line
import com.graphics.glcanvas.engine.structures.PolyLine
import com.graphics.glcanvas.engine.structures.RectF
import com.graphics.glcanvas.engine.ui.GLButton
import com.graphics.glcanvas.engine.ui.GLLabel
import com.graphics.glcanvas.engine.ui.OnClickEvent
import com.neural.evolution.ai.NeuralNetwork
import com.neural.evolution.algebra.Collision
import com.neural.evolution.utils.Timer
import com.neural.evolution.utils.TmxLoader
import com.neural.evolution.utils.TmxParser
import kotlin.math.min
import kotlin.random.Random

class Renderer(private val context: Context,width:Float,height:Float):GLRendererView(width, height) {


   private val batch=Batch()
   private val camera=Camera2D(10f)
   private val poly=PolyLine()
   private val cars= MutableList(50,init = {Car(poly,100f,130f,25f,25f)})
   private val crashedCars= mutableListOf<Car>()
   private var tmxMap= TmxParser(TmxLoader("raceTrack.tmx",context))
   private val checkpoints= mutableListOf<RectF>()
   private val timer= Timer(1000)
   private var nextGen:GLLabel?=null
  private val axis=AxisABB()
    override fun prepare() {
         batch.initShader(context)
         camera.setOrtho(getCanvasWidth(), getCanvasHeight())
         cars.forEach { car->
             car.setColor(ColorRGBA.blue)
         }
          nextGen= GLLabel(100f,50f, Font(Font.CALIBRI,context),"Next",0.4f)
          nextGen?.set(getCanvasWidth()-180f,80f)
          nextGen?.setBackgroundColor(ColorRGBA(0.8f,0f,0f,1f))
          nextGen?.roundedCorner(10f)
          nextGen?.setRippleColor(ColorRGBA(1f,0f,0f,1f))
        //objectGroup
            tmxMap.data.forEach { group->
             //object

             for(obj in group.getObjects()){
                 val offsetY = 50f
                 //polygons
                 if(group.name=="track") {
                     for (poly in obj.polygons) {
                         //points
                         for (j in 0 until poly.points.size - 1) {
                             val a = poly.points[j]
                             val b = poly.points[j + 1]
                             moveTo(obj.x + a.first, obj.y + a.second + offsetY)
                             lineTo(obj.x + b.first, obj.y + b.second + offsetY)
                         }
                         //join the last object with the first
                         val a = poly.points[poly.points.size - 1]
                         val b = poly.points[0]
                         moveTo(obj.x + a.first, obj.y + a.second + offsetY)
                         lineTo(obj.x + b.first, obj.y + b.second + offsetY)

                     }
                 }else if(group.name=="checkPoint"){
                      checkpoints.add(RectF(obj.x ,obj.y +offsetY,80f,70f))

                   }
             }

         }


    }

  private fun geneticsAlgorithm(){
      for(car in cars)   {
          if(car.crashed)
              crashedCars.add(car)
      }
      //remove all cars that crashed
      cars.removeAll { it.crashed }
      if(cars.isEmpty()) {
          timer.reset()
          crashedCars.sortBy { it.score .size}
          val children= mutableListOf<Car>()

          for (i in crashedCars.size-1 until crashedCars.size-10) {
              //Selection: randomly pick a parent
              val parent =crashedCars[i]
              val car=Car(poly,100f,100f,25f,25f)
                  car.neuralNetwork.copy(parent.neuralNetwork)
                  NeuralNetwork.mutate(car.neuralNetwork,0.5f)
              car.setColor(parent.getColor())
              children.add(car)
          }

          cars.addAll(crashedCars)
          crashedCars.clear()
          for(i in 0 until children.size){
              cars[i]=children[i]
          }

          for(car in cars){
              car.reset()
          }
      }



  }

    override fun draw() {
       GLES32.glClear(GLES32.GL_DEPTH_BUFFER_BIT or GLES32.GL_COLOR_BUFFER_BIT)
       GLES32.glClearColor(0.0f,0.0f,0.0f,1f)
        batch.setMode(BatchQueue.UNORDER)
        batch.begin(camera)
        checkpoints.forEach {
            batch.draw(it)
        }
        cars.forEach {car->
            car.draw(batch)
        }

        batch.draw(poly)
        batch.end()

        batch.begin(camera)
        nextGen?.draw(batch)
        batch.end()




    }

    private fun moveTo(x:Float,y:Float){
        poly.moveTo(x,y)
    }

    private fun lineTo(x:Float,y:Float){
        poly.lineTo(x,y)
    }
    private fun testCollision(car:Car){

        for (point in checkpoints){
            if(axis.isIntersecting(point,car)&&!car.score.contains(point))
                car.score.add(point)
        }


        //car bound to wall collision
        for(ray1 in car.getBounds()){
            for(path in poly.getPaths()){

                for(end in path.getEndPoints()){
                    val d=Collision.detect_line_collision(ray1.getStartX(),ray1.getStartY(),ray1.getStopX(),ray1.getStopY(),
                        path.getStart().x,path.getStart().y,end.x,end.y)
                    if (Collision.do_lines_intersect(d)) {
                        car.crashed=true
                    }
                }
            }
        }

    }

    override fun update(delta: Long) {
        if(timer.getTick()>50){
            timer.reset()
            cars.forEach { it.crashed=true }
        }
        timer.update(delta)
        cars.forEach {car->
            if(!car.crashed){
                car.update(delta)
                testCollision(car)
            }
        }

        geneticsAlgorithm()


    }


    //clear resources here
    override fun onRelease() {

    }
}