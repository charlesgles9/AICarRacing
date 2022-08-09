package com.neural.evolution

import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.structures.Line

class Track {

    private val text=StringBuffer()
    private val blocks= mutableListOf<Block>()
    private val borderLines= mutableListOf<Ray>()
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
        val offset=50f
        //generate blocks
       val str=text.toString().split("\n")
       val array= mutableListOf<List<String>>()
        for(i in str.indices){
            array.add(str[i].split(","))
        }
       for(i in array.indices){
           for (j in array[i].indices){
               if(array[i][j]=="0")
                   continue
               val x=j*50f
               val y=i*50f
               val block=Block(x+offset,y+offset,50f,50f)
               blocks.add(block)
           }
       }

        val visitedNodes= mutableListOf<Int>()
        //generate lines
        for(i in array.indices){
            for (j in array[i].indices){
                val index=i*array.size+j
                if (array[i][j] == "0"||visitedNodes.contains(index))
                    continue

                var stopRight=j
                var stopBottom=i
                //walk to the right and see if there's a valid block
                    for(k in j+1 until array[i].size){
                        if(array[i][k]=="0")
                            break
                        visitedNodes.add(i*array.size+k)
                        stopRight=k
                    }
                //walk to the bottom and see if there's a valid block
                    for(k in (i+1) until array.size){
                        if(array[k][j]=="0")
                            break
                        visitedNodes.add(k*array.size+j)
                        stopBottom=k
                    }

               if(stopRight!=j)
                  borderLines.addAll( createBoundaryVertical(j*50f,i*50f,stopRight*50f,i*50f,offset))
                if(stopBottom!=i)
                 borderLines.addAll(createBoundaryHorizontal(j*50f,i*50f,j*50f,stopBottom*50f,offset))
            }
        }
    }

    private fun createBoundaryVertical(x:Float, y:Float, width:Float, height:Float, offset:Float):MutableList<Ray>{
        val topLine=Ray(x+offset-25f,y+offset-25f,width+offset+25f,y+offset-25f)
        val bottomLine=Ray(x+offset-25f,y+offset+25f,width+offset+25f,y+offset+25f)
        val leftLine=Ray(x+offset-25f,y+offset-25f,x+offset-25f,height+offset+25f)
        val rightLine=Ray(width+offset+25f,y+offset-25f,width+offset+25f,height+offset+25f)
        topLine.setColor(ColorRGBA.red)
        bottomLine.setColor(ColorRGBA.red)
        leftLine.setColor(ColorRGBA.red)
        rightLine.setColor(ColorRGBA.red)
        return mutableListOf(topLine,bottomLine,leftLine,rightLine)
    }

    private fun createBoundaryHorizontal(x:Float, y:Float, width:Float, height:Float, offset:Float):MutableList<Ray>{
        val leftLine=Ray(x+offset-25f,y+offset-25f,x+offset-25f,height+offset+25f)
        val rightLine=Ray(x+offset+25f,y+offset-25f,x+offset+25f,height+offset+25f)
        val bottomLine=Ray(x+offset-25f,y+offset-25f,x+offset+25f,y+offset-25f)
        val topLine=Ray(width+offset-25f,height+offset+25f,width+offset+25f,height+offset+25f)
        topLine.setColor(ColorRGBA.red)
        bottomLine.setColor(ColorRGBA.red)
        leftLine.setColor(ColorRGBA.red)
        rightLine.setColor(ColorRGBA.red)
        return mutableListOf(leftLine,rightLine,bottomLine,topLine)
    }

    fun getBlocks():MutableList<Block>{
        return blocks
    }

    fun getBorderLine():MutableList<Ray>{
        return borderLines
    }
}