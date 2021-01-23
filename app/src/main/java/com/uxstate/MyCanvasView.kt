package com.uxstate

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import androidx.core.content.res.ResourcesCompat

//stroke width
private const val STROKE_WIDTH = 12f // has to be a float
//Create a custom view and passing a context
class MyCanvasView(context: Context) : View(context) {
    //member variables for the Canvas and Bitmap
    //Canvas and Bitmap for caching what has been drawn before
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitMap: Bitmap //for caching what had been drawn before


    //initialize background color variable
    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)

    //drawColor holds the color to draw with
    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)

    //paint object for styling

    private val paint = Paint().apply {
        //drawing color
        color = drawColor
        //smooths out edges of what is drawn without affecting the shape
        isAntiAlias  = true

        //Dithering affects how colors with higher-precision that the device are down-sampled
        isDither = true

        style = Paint.Style.STROKE//default:FILL
        strokeJoin = Paint.Join.ROUND //default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH //default: Hairline -width (really thin)
    }


    //path stores the path that is drawn when following the user's touch on the screen
    private val path = Path()
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        //recycle extraBitmap before creating a new one to avoid memory leak
        if (::extraBitMap.isInitialized) {
            extraBitMap.recycle()
        }

        //create an instance of bitmap with new width and height

        /*ARGB_8888 stores each color in four bytes and is recommended*/
        extraBitMap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        //create a Canvas instance from Bitmap and assign it to extraCanvas
        extraCanvas = Canvas(extraBitMap)

        //specify the background color in which to fill extraCanvas
        extraCanvas.drawColor(backgroundColor)

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //args - bitMap, x, y coordinates(pixels for the top left corner, paint to be set later
        canvas?.drawBitmap(extraBitMap, 0f, 0f, null)


        /*canvas passed to onDraw and used by the system to display the bitmap is different from the one
        created on onSizeChanged()*/
    }


}