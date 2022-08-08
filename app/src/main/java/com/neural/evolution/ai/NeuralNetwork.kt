package com.neural.evolution.ai
import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.Update
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.maths.Vector2f
import com.graphics.glcanvas.engine.structures.Circle
import com.graphics.glcanvas.engine.structures.Line
import com.neural.evolution.algebra.Matrix


class NeuralNetwork (val inputCount:Int, val hiddenCount:Int,  val outputCount:Int):Update{
    private val weightsInputHidden= Matrix(hiddenCount,inputCount)
    private val weightsHiddenOutput=Matrix(outputCount,hiddenCount)
    private val biasHidden=Matrix(hiddenCount,1)
    private val biasOutput=Matrix(outputCount,1)

    private val inputLayer= List(inputCount,init = {Circle(0f,0f,10f)})
    private val hiddenLayer=List(hiddenCount,init = {Circle(0f,0f,10f)})
    private val outputLayer=List(outputCount,init = {Circle(0f,0f,10f)})

    private val conInputHiddenLayer=List(hiddenCount*inputCount,init = {Line(0f,0f,0f,0f)})
    private val conHiddenOutputLayer=List(hiddenCount*inputCount,init = {Line(0f,0f,0f,0f)})
    val start=Vector2f(0f,0f)
    constructor(network: NeuralNetwork)
            :this(network.inputCount,network.hiddenCount, network.outputCount){
        copy(network)

    }

    fun copy(network: NeuralNetwork){
        weightsHiddenOutput.copy(network.weightsHiddenOutput)
        weightsInputHidden.copy(network.weightsInputHidden)
        biasHidden.copy(network.biasHidden)
        biasOutput.copy(network.biasOutput)
    }

    companion object {
        fun mutate(child: NeuralNetwork,rate:Float) {
            child.weightsInputHidden.mutate(rate)
            child.weightsHiddenOutput.mutate(rate)
            child.biasHidden.mutate(rate)
            child.biasOutput.mutate(rate)
        }
    }

    fun breed():NeuralNetwork{
        //create a child with parents genes
        return NeuralNetwork(this)
    }

    fun initGraphics(){

        val xOffset=100f
        val yOffset1=inputLayer.size*inputLayer[0].getRadius()*3.5f-
                hiddenLayer.size*hiddenLayer[0].getRadius()*3.5f
        val yOffset2=inputLayer.size*inputLayer[0].getRadius()*3.5f-
                outputLayer.size*outputLayer[0].getRadius()*3.5f
        for(i  in inputLayer.indices){
            val input= inputLayer[i]
            input.set(start.x+input.getRadius(),
                start.y+input.getRadius()*i*3.5f)

        }

        for(i in hiddenLayer.indices){
            val hidden=hiddenLayer[i]
            hidden.set(start.x+hidden.getRadius()+xOffset,
                start.y+hidden.getRadius()*i*3.5f+yOffset1*0.5f)
        }

        for(i in outputLayer.indices){
            val output=outputLayer[i]
            output.set(start.x+output.getRadius()+xOffset*2f,
                start.y+output.getRadius()*i*3.5f+yOffset2*0.5f)
        }

        for(i in inputLayer.indices){
            val input=inputLayer[i]
            for(j in hiddenLayer.indices){
                val line=conInputHiddenLayer[i*hiddenCount+j]
                val hidden=hiddenLayer[j]
                line.set(input.getX(),input.getY(),
                    hidden.getX(),hidden.getY())
                line.setColor(ColorRGBA.white)
            }
        }

        for(i in hiddenLayer.indices){
            val hidden=hiddenLayer[i]
            for(j in outputLayer.indices){
                val line=conHiddenOutputLayer[i*outputCount+j]
                val output=outputLayer[j]
                line.set(hidden.getX(),hidden.getY(),
                    output.getX(),output.getY())
                line.setColor(ColorRGBA.white)
            }
        }

    }

    //forward propagation
    fun predict(values:MutableList<Double>):MutableList<Double>{
        val input=Matrix.fromArray(values)
        val hidden=Matrix.multiply(weightsInputHidden,input)
        hidden.add(biasHidden)
        hidden.sigmoid()

        val output=Matrix.multiply(weightsHiddenOutput,hidden)
        output.add(biasOutput)
        output.sigmoid()

        for(i in 0 until values.size){
            val color=inputLayer[i].getColor()
            color.set(ColorRGBA.green)
            inputLayer[i].setColor(ColorRGBA.darken(values[i].toFloat(),color ))
        }

        for(i in 0 until hidden.size()){
            val color=hiddenLayer[i].getColor()
            color.set(ColorRGBA.cyan)
            hiddenLayer[i].setColor(ColorRGBA.darken(hidden.data[i][0].toFloat(),color ))
        }

        for(i in 0 until output.size()){
            val color=outputLayer[i].getColor()
            color.set(ColorRGBA.red)
            outputLayer[i].setColor(ColorRGBA.darken(output.data[i][0].toFloat(),color ))
        }
        return output.toArray()
    }

    override fun draw(batch: Batch) {

        conInputHiddenLayer.forEach {
            batch.draw(it)
        }
        conHiddenOutputLayer.forEach {
            batch.draw(it)
        }
        inputLayer.forEach {
            batch.draw(it)
        }
        hiddenLayer.forEach {
            batch.draw(it)
        }
        outputLayer.forEach {
            batch.draw(it)
        }


    }

    override fun update(delta: Long) {

    }
}