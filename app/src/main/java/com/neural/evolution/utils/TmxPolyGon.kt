package com.neural.evolution.utils

class TmxPolyGon(private val text:String) {

     val points= mutableListOf<Pair<Float,Float>>()
    init {
       val list=text.split(" ")
       list.forEach {
           val array=it.split(",")
           points.add(Pair(array[0].toFloat(),array[1].toFloat()))
       }

    }



}