package com.neural.evolution.utils


class Timer(private val delay:Long) {


    private var tick=0
    private var pt=0L
    fun update(time:Long){
        val delta= pt + delay
        if(delta<=time){
            pt =time
            tick++
        }
    }

    fun getTick():Int{
        return tick
    }

    fun reset(){
        tick=0
    }

}