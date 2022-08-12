package com.neural.evolution.utils

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.graphics.glcanvas.engine.utils.ResourceLoader
import com.neural.evolution.ai.NeuralNetwork
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*

import kotlin.collections.HashMap

class AIMetaData(private val network: NeuralNetwork) {



    companion object {
        fun saveDataExists(path: String, context: Context): Boolean {
            val storage = context.getExternalFilesDirs(null)[0]
            val file = File(storage, path)
            return file.exists()
        }
    }

    fun saveData(context: Context, path:String){
        val storage=context.getExternalFilesDirs(null)[0]
        val file=File(storage,path)
        val parent=file.parentFile
        if(parent?.exists()==false)
            parent.mkdir()
        if(!file.exists())
            file.createNewFile()
        val jsonObject=JSONObject()
        jsonObject.put("WeightsIH",network.weightsInputHidden.toArray())
        jsonObject.put("WeightsHO",network.weightsHiddenOutput.toArray())
        jsonObject.put("BiasHidden",network.biasHidden.toArray())
        jsonObject.put("BiasOutput",network.biasOutput.toArray())
        val output=JSONArray().put(jsonObject)
        writeJson(file,output)
    }

    fun loadSaveData(context: Context, path: String){
        try {
            val jArray = loadJson(path, context)
            if(jArray!=null)
                populateDataToNetwork(jArray)
            else{
                populateDataFromAssets(context)
            }
        }catch (e:JSONException){ }
    }

    fun populateDataFromAssets(context: Context){
        val text=ResourceLoader().loadTextFromAssets(context,"data/data.json")
        populateDataToNetwork(JSONArray(text))
    }

    fun resetNetwork(context: Context, path: String){
        network.weightsHiddenOutput.reset()
        network.weightsInputHidden.reset()
        saveData(context,path)
    }

    private fun populateDataToNetwork(jArray: JSONArray){
        val jObject=jArray.getJSONObject(0)
        val inputHidden=jObject.getString("WeightsIH")
        val hiddenOutput=jObject.getString("WeightsHO")
        val biasHidden=jObject.getString("BiasHidden")
        val biasOutput=jObject.getString("BiasOutput")
        network.weightsInputHidden.copy(splitString(inputHidden.substring(1,inputHidden.length-1)))
        network.weightsHiddenOutput.copy( splitString(hiddenOutput.substring(1,hiddenOutput.length-1)))
        network.biasHidden.copy(splitString(biasHidden.substring(1,biasHidden.length-1)))
        network.biasOutput.copy(splitString(biasOutput.substring(1,biasOutput.length-1)))
    }

    private fun splitString(string: String):Array<Double>{
        val array=string.split(",")
        val list= Array(array.size,init = {0.0})
        for (i in array.indices) {
          val text=array[i]
           if(TextUtils.isDigitsOnly(text))
            list[i] = text.trim().toDouble()
        }
        return list
    }

    private fun writeJson(file:File,jsonArray: JSONArray){
        val writer=BufferedWriter(FileWriter(file))
        writer.write(jsonArray.toString(1))
        writer.flush()
        writer.close()
    }

    private fun loadJson(path:String,context: Context):JSONArray?{
        val storage=context.getExternalFilesDirs(null)[0]
        val file=File(storage,path)
        if(!file.exists())
            return null
        val text=StringBuffer()
        val br=BufferedReader(FileReader(file))
        var line:String?=""
        while ((br.readLine().also {
                line = it })!=null){
            text.append(line)
            //  text.append('\n')
        }
        br.close()
        return JSONArray(text.toString())
    }

}