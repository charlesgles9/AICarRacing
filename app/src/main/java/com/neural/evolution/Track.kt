package com.neural.evolution

class Track {

    private val text=StringBuffer()
    private val blocks= mutableListOf<Block>()
    init {
        text.append("1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1\n")
        text.append("1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1\n")
        text.append("1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1\n")
        text.append("1,1,1,1,1,1,1,1,1,0,0,1,0,0,1,1,0,0,1,1,1,1,0,0,1\n")
        text.append("1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,1,0,0,1,0,0,1\n")
        text.append("1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,1,0,0,1,0,0,1\n")
        text.append("1,0,0,1,0,0,1,1,1,0,0,1,0,0,0,1,0,0,1,0,0,1,0,0,1\n")
        text.append("1,0,0,1,0,0,1,1,1,0,0,1,0,0,0,1,0,0,1,0,0,1,0,0,1\n")
        text.append("1,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1\n")
        text.append("1,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1\n")
        text.append("1,0,0,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,0,0,1,1,1,1\n")
        text.append("1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,1\n")
        text.append("1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,1\n")
        text.append("1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,0,0,1,1,0,0,1,0,0,1\n")
        text.append("1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,1\n")
        text.append("1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,1\n")
        text.append("1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,0,0,1\n")
        text.append("1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1\n")
        text.append("1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1\n")
        text.append("1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1")
        createMap()
    }


    private fun createMap(){
       val array=text.toString().split("\n")
       for(i in array.indices){
           val segments=array[i].split(",")
           for (j in segments.indices){
               if(segments[j]=="0")
                   continue
               val x=j*50f
               val y=i*50f
               val block=Block(x+50f,y+50f,50f,50f)
               blocks.add(block)
           }
       }
    }

    fun getBlocks():MutableList<Block>{
        return blocks
    }
}