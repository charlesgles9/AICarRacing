package com.neural.evolution.utils

class TmxObject(private val x:Float, y:Float) {
   var tmxPolygon:TmxPolyGon?=null
   fun createTmxPolygon(points:String){
       tmxPolygon= TmxPolyGon(points)
   }

}