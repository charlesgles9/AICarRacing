package com.neural.evolution.utils

class TmxObject( val x:Float, val y:Float) {
   var polygons= mutableListOf<TmxPolyGon>()
   fun createTmxPolygon(points:String){
       polygons.add(TmxPolyGon(points))
   }

}