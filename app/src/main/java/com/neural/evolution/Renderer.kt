package com.neural.evolution

import android.content.Context
import android.opengl.GLES32
import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.BatchQueue
import com.graphics.glcanvas.engine.Camera2D
import com.graphics.glcanvas.engine.GLRendererView
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.structures.Line
import com.graphics.glcanvas.engine.structures.PolyLine
import com.graphics.glcanvas.engine.structures.RectF
import com.neural.evolution.ai.NeuralNetwork
import com.neural.evolution.algebra.Collision
import kotlin.math.min
import kotlin.random.Random

class Renderer(private val context: Context,width:Float,height:Float):GLRendererView(width, height) {

   private val track=Track()
   private val batch=Batch()
   private val camera=Camera2D(10f)
   private val cars= MutableList(10,init = {Car(200f,100f,25f,25f)})
   private val crashedCars= mutableListOf<Car>()
    override fun prepare() {
         batch.initShader(context)
         camera.setOrtho(getCanvasWidth(), getCanvasHeight())
         cars.forEach { car->
             car.setColor(ColorRGBA.blue)
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
          crashedCars.sortBy { it.score }
          val children= mutableListOf<Car>()
          for (i in 0 until crashedCars.size/2) {
              //Selection: randomly pick a parent
              val parent =crashedCars[min(crashedCars.size/2+
                      Random.nextInt(crashedCars.size/2),crashedCars.size-1)]
              val car=Car(100f,100f,25f,25f)
                  car.neuralNetwork.copy(parent.neuralNetwork)
                  NeuralNetwork.mutate(car.neuralNetwork,0.5f)
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

    val poly=PolyLine()
    val points= mutableListOf<RectF>()
    override fun draw() {
       GLES32.glClear(GLES32.GL_DEPTH_BUFFER_BIT or GLES32.GL_COLOR_BUFFER_BIT)
       GLES32.glClearColor(0.0f,0.0f,0.0f,1f)
        batch.setMode(BatchQueue.UNORDER)
       /* batch.begin(camera)
        track.getBlocks().forEach {
          //  it.draw(batch)
        }
        track.getBorderLine().forEach {
            batch.draw(it)
        }
        cars.forEach {car->
            car.draw(batch)
        }

        batch.end()*/

        batch.begin(camera)
      /*  moveTo(200f+0,488f+116)
        lineTo(200f+0,488f+116)
        moveTo(200f+0,488f+116)
        lineTo(200f+0,488f+94)
        moveTo(200f+0,488f+94)
        lineTo(200f+22,488f+106)
        moveTo(200f+22,488f+106)
        lineTo(200f+0,488f+116)*/
   //     points.add(RectF(1030+14f,72+36f,10f,10f))
        points.add(RectF(1030+14f,72-6f,10f,10f))
        points.add(RectF(1030-36f,72+128f,10f,10f))
        points.add(RectF(1030+150f,72+36f,10f,10f))

        moveTo(1030f+14f,72f-6f)
        lineTo(1030f-36f,72f+128f)
        moveTo(1030f-36f,72f+128f)
        lineTo(1030f+150f,72f+36f)
        moveTo(1030f+150f,72f+36f)
        lineTo(1030f+14f,72f-6f)
        batch.draw(poly)
        batch.end()


        points.clear()
        poly.reset()

    }

    private fun moveTo(x:Float,y:Float){
        poly.moveTo(x,y)
    }

    private fun lineTo(x:Float,y:Float){
        poly.lineTo(x,y)
    }
    private fun testCollision(car:Car){
        // ray to wall collision
        for(ray1 in car.getRays()) {
            for (ray2 in track.getBorderLine()) {
                val d = Collision.detect_line_collision(ray1, ray2)
                if (Collision.do_lines_intersect(d)) {
                    Collision.setInterSectionPoint(d, ray1)
                }
            }
        }

        //car bound to wall collision
        for(ray1 in car.getBounds()){
            for (ray2 in track.getBorderLine()) {
                val d = Collision.detect_line_collision(ray1, ray2)
                if (Collision.do_lines_intersect(d)) {
                    car.crashed=true
                    break
                }
            }
        }

    }

    override fun update(delta: Long) {
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