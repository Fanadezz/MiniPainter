package com.uxstate

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.core.content.res.ResourcesCompat

//Create a custom view and passing a context
class MyCanvasView(context: Context) : View(context) {


    //member variables for the Canvas and Bitmap




    //initialize background color variable
    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)



    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitMap: Bitmap //for caching what had been drawn before

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        //recycle extraBitmap before creating a new one to avoid memory leak
        if (::extraBitMap.isInitialized){
            extraBitMap.recycle()
        }

        //create an instance of bitmap with new width and height
        extraBitMap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888)

        //create a Canvas instance from Bitmap and assign it to extraCanvas
        extraCanvas = Canvas(extraBitMap)

        //specify the background color in which to fill extraCanvas
        extraCanvas.drawColor(backgroundColor)

    }




}