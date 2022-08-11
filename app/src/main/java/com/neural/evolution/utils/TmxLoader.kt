package com.neural.evolution.utils

import android.content.Context
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException

class TmxLoader (private val path:String,private val context: Context){

     val xmlFactory=XmlPullParserFactory.newInstance()
     val xmlParser=xmlFactory.newPullParser()

    init {
        load()
    }
    private fun load(){
        try {
            val stream = context.assets.open(path)
            xmlParser.setInput(stream, null)
        }catch (io:IOException){

        }catch (xmlp:XmlPullParserException){

        }

    }
}