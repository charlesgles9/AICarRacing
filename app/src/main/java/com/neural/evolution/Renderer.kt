package com.neural.evolution

import android.app.Activity
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
import com.graphics.glcanvas.engine.ui.*
import com.graphics.glcanvas.engine.utils.FpsCounter
import com.graphics.glcanvas.engine.utils.Texture
import com.graphics.glcanvas.engine.utils.TextureLoader
import com.neural.evolution.ai.NeuralNetwork
import com.neural.evolution.algebra.Collision
import com.neural.evolution.utils.*
import kotlin.concurrent.thread
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class Renderer(private val context: Context,width:Float,height:Float):GLRendererView(width, height) {


   private val batch=Batch()
   private val camera=Camera2D(10f)
   private val poly=PolyLine()
   private val cars= MutableList(100,init = {Car(poly,100f,130f,45f,25f)})
   private val crashedCars= mutableListOf<Car>()
   private var tmxMap= TmxParser(TmxLoader("raceTrack.tmx",context))
   private val checkpoints= mutableListOf<RectF>()
   private val timer= Timer(1000)
   private val axis=AxisABB()
   private var reset=false
   private var saving=false
   private var carTexture:Texture?=null
   private var checkPointTexture:Texture?=null
    //UI stuff
   private var timerLabel:GLLabel?=null
   private var nextGen:GLLabel?=null
   private var debugLayout=LinearLayoutConstraint(null,200f,80f)
   private var font:Font?=null
    override fun prepare() {
         batch.initShader(context)
         camera.setOrtho(getCanvasWidth(), getCanvasHeight())
         carTexture= Texture(context,"4x4_white.png")
         checkPointTexture= Texture(context,"checkPoint.png")
         font=Font(Font.CALIBRI,context)
         cars.forEach { car->
             car.setTexture(carTexture!!)
         }
          nextGen= GLLabel(100f,50f, font!!,"Next",0.4f)
          nextGen?.set(getCanvasWidth()-350f,80f)
          nextGen?.setBackgroundColor(ColorRGBA(0.8f,0f,0f,1f))
          nextGen?.roundedCorner(10f)
          nextGen?.setRippleColor(ColorRGBA(1f,0f,0f,1f))

         debugLayout.set(getCanvasWidth()-150f,80f)
         debugLayout.setBackgroundColor(ColorRGBA.transparent)
         val debugLabel= GLLabel(100f,50f, font!!,"Debug",0.3f)
         val debugMode=GLCheckBox(80f,80f, ColorRGBA.red)
             debugMode.setBackgroundColor(ColorRGBA.gray)
             debugMode.setCheckedColor(ColorRGBA.red)
             debugMode.getConstraints().alignCenterVertical(debugLabel)
          debugLayout.addItem(debugLabel)
          debugLayout.addItem(debugMode)

          timerLabel= GLLabel(250f,50f,font!!,"Time: 0",0.32f)
          timerLabel?.set(getCanvasWidth()*0.5f,80f)
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
                     val check=RectF(obj.x +obj.width*0.5f,obj.y +offsetY+obj.height*0.5f,obj.width,obj.height)
                         check.setTexture(checkPointTexture!!)
                      checkpoints.add(check)

                   }
             }

         }

        nextGen?.setOnClickListener(object :OnClickEvent.OnClickListener{
            override fun onClick() {
               reset=true
            }
        })

        debugMode.setOnClickListener(object :OnClickEvent.OnClickListener{
            override fun onClick() {

            }
        })

        getController()?.addEvent(nextGen!!)
        getController()?.addEvent(debugMode)

        if(!FileUtility.checkStoragePermissionDenied(context as Activity)&&AIMetaData.saveDataExists("/data.json",context)){
            //populate first car then copy contents to others
            val car=cars[0]
            AIMetaData(car.neuralNetwork).loadSaveData(context,"/data.json")
            for (i in 1 until cars.size){
                cars[i].neuralNetwork.copy(car.neuralNetwork)
                NeuralNetwork.mutate(cars[i].neuralNetwork,0.1f)
            }
        }else{
            //populate first car then copy contents to others
            val car=cars[0]
            AIMetaData(car.neuralNetwork).populateDataFromAssets(context)
            for (i in 1 until cars.size){
                cars[i].neuralNetwork.copy(car.neuralNetwork)
                NeuralNetwork.mutate(cars[i].neuralNetwork,0.1f)
            }
        }

    }


    private fun saveDataToCache(cars:MutableList<Car>){
        if(!saving&&cars.isNotEmpty()) {
                saving=true
                cars.sortBy { it.score.size }
                val bestCar=cars[cars.size-1]
            thread {
                if (!FileUtility.checkStoragePermissionDenied(context as Activity)) {
                    AIMetaData(bestCar.neuralNetwork).saveData(context, "/data.json")
                }
                saving = false
            }
        }
    }

  private fun geneticsAlgorithm(){
      for(car in cars)   {
          if(car.crashed&&!crashedCars.contains(car))
              crashedCars.add(car)
      }
      //remove all cars that crashed
      cars.removeAll { it.crashed }
      if(cars.isEmpty()) {
          timer.reset()
          crashedCars.sortBy { it.score .size}
          saveDataToCache(crashedCars)
          val children= mutableListOf<Car>()
          for (i in 0 until crashedCars.size/2) {
              //Selection: randomly pick a parent
              val parent =crashedCars[min(crashedCars.size/2+ Random.nextInt(crashedCars.size/2),crashedCars.size-1)]
              val car=Car(poly,100f,100f,45f,25f)
                  car.neuralNetwork.copy(parent.neuralNetwork)
                  NeuralNetwork.mutate(car.neuralNetwork,0.1f)
              car.setColor(parent.getColor())
              car.setTexture(carTexture!!)
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
            car.draw(batch,false)
        }
        crashedCars.forEach { car->
            car.draw(batch,false)
        }

        batch.draw(poly)
        batch.end()

        batch.begin(camera)
        nextGen?.draw(batch)
        timerLabel?.draw(batch)
        debugLayout.draw(batch)
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
        for(cpath in car.getPoly().getPaths()) {
            for (cend in cpath.getEndPoints()) {
                for (tpath in poly.getPaths()) {
                    for (tend in tpath.getEndPoints()) {
                        val d = Collision.detect_line_collision(
                            cpath.getStart().x, cpath.getStart().y, cend.x, cend.y,
                            tpath.getStart().x, tpath.getStart().y, tend.x, tend.y
                        )
                        if (Collision.do_lines_intersect(d)) {
                            car.crashed = true
                        }
                    }
                }
            }
        }

    }

    override fun update(delta: Long) {
        if(timer.getTick()>50||reset){
            timer.reset()
            reset=false
            saveDataToCache(cars)
            cars.forEach { it.crashed=true }

        }
        timerLabel?.setText("Lap Timer: "+ max(50-timer.getTick(),0))
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

        batch.cleanUp()
        TextureLoader.getInstance().clearTextures()
    }
}