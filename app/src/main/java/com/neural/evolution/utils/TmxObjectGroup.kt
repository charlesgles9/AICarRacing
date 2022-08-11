package com.neural.evolution.utils

class TmxObjectGroup( val name:String) {

    private val objects= mutableListOf<TmxObject>()

    fun addObject(obj:TmxObject){
        objects.add(obj)
    }

    fun getObjects():MutableList<TmxObject>{
        return objects
    }
}