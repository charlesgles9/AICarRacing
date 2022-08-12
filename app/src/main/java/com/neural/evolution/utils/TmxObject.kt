package com.neural.evolution.utils

class TmxObject( val x:Float, val y:Float) {
    var width=0f
    var height=0f
   var polygons= mutableListOf<TmxPolyGon>()
   fun createTmxPolygon(points:String){
       polygons.add(TmxPolyGon(points))
   }

}