package com.uxstate

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import kotlin.math.abs

//stroke width
private const val STROKE_WIDTH = 12f // has to be a float
//Create a custom view and passing a context
class MyCanvasView(context: Context) : View(context) {
    //member variables for the Canvas and Bitmap

    //Canvas and Bitmap for caching what has been drawn before
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitMap: Bitmap
    /*As the user draws on the screen, the app constructs the path and saves
    it in a bitMap - extraBitMap.The onDraw() method displays the extraBitMap in the
    View's canvas*/

    //initialize background color variable
    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)

    //drawColor holds the color to draw with
    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)

    //paint object for styling
    private val paint = Paint().apply {
        //drawing color
        color = drawColor
        //smooths out edges of what is drawn without affecting the shape
        isAntiAlias = true

        //Dithering affects how colors with higher-precision than the device are down-sampled
        isDither = true

        style = Paint.Style.STROKE//default:FILL
        strokeJoin = Paint.Join.ROUND //default: MITER
        strokeCap = Paint.Cap.ROUND// default: BUTT
        strokeWidth = STROKE_WIDTH //default: Hairline -width (really thin)
    }


    /*motion touch event X and motion touch event Y variables for
    caching the x and y coordinates of the current touch event*/

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f


    /*variables to cache the latest x and y values - after the
    user stops moving and lifts off their touch, these will be
    the starting points for the next path*/
    private var currentX = 0f
    private var currentY = 0f

    //path stores the path that is drawn when following the user's touch on the screen
    private val path = Path()

    /*using a path,  there is no need to draw every pixel and each time you request a refresh after display
    * Instead you can and will interpolate a path between points for much better
    * performance. If the finger has barely moved, there is no need to draw.
    *
    * If the finger has moved less than a touchTorelance distance don't draw
    *
    * ScaledTouchSlop returns the distance in pixels a touch can wander before the system thinks the
    * user is scrolling*/
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop


    //holds a rectangle object
    private lateinit var frame:Rect

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

        //inset calculates a rectangular frame around the picture
        val inset = 40

        //initialize rectangle with inset
        frame = Rect(inset,inset,width-inset, height -inset)

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //args - bitMap, x, y coordinates(pixels for the top left corner, paint to be set later
        canvas?.drawBitmap(extraBitMap, 0f, 0f, null)


        /*canvas passed to onDraw and used by the system to display the bitmap is different from the one
        created on onSizeChanged()*/


//Draw a frame around the canvas
        canvas?.drawRect(frame,paint)
    }

    /*override onTouchEvent method to cache the x and y coordinates of the passed in events*/
    override fun onTouchEvent(event: MotionEvent): Boolean {

        motionTouchEventX = event.x
        motionTouchEventY = event.y


        //when expression to handle motion events
        when (event.action) {

            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }


        return true
    }
    //called when the user first touches the screen
    private fun touchStart() {

        //Clear any lines and curves from the path, making it empty.
       path.reset()

        //Set the beginning of the next contour to the point (x,y).
        path.moveTo(motionTouchEventX, motionTouchEventY)
        //assign current x and current y to motionTouchEvent position x and y
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun touchMove() {

        //calculate the travelled distance - dX and dY

        //abs() returns the absolute value of the given value x.
        val dx = abs(motionTouchEventX - currentX)
        val dy = abs(motionTouchEventY - currentY)

       /*if the moved distance is further than touchTolerance,
       add a segment between two pints and store it in path*/


        if ((dx >= touchTolerance) || (dy >= touchTolerance)) {

            /*Add a quadratic bezier (curve) from the last point, approaching
            control point (x1,y1), and ending at (x2,y2).*/

               // path.lineTo(currentX, currentY)

            path.quadTo(
                    currentX, currentY, (motionTouchEventX + currentX) / 2,
                    (motionTouchEventY + currentY) / 2)

            /*using quadTo, instead of lineTo()  creates a smoothly drawn line
            * without corners*/

            //update the currentX and currentY  - set the starting point for the next segment to end point
            // of this segment
            currentX = motionTouchEventX
            currentY = motionTouchEventY

            //draw the path in the canvas to cache it
            extraCanvas.drawPath(path,paint)
        }

        //force draw
        invalidate()
    }

    private fun touchUp() {

        //reset the path so it doesn't get drawn again
       path.reset()

        /*When a user lifts their touch all that is needed is to rest the path
        * so it doesn't get drawn again.Nothing is drawn here so no invalidation
        * needed*/
    }

}