package com.neural.evolution

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.graphics.glcanvas.engine.GLCanvasSurfaceView
import com.neural.evolution.utils.FileUtility

class MainActivity : AppCompatActivity() {
    private var surface:GLCanvasSurfaceView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val renderer =Renderer(this,1280f,720f)
        surface=GLCanvasSurfaceView(this,renderer)
        setContentView(surface)
        FileUtility.grantStoragePermission(this)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        surface?.onRelease()
    }
}