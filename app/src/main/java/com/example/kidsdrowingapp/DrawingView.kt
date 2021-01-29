package com.example.kidsdrowingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context : Context, attrs : AttributeSet) : View(context, attrs) {
        private var mpath : CustomPath? = null
        private var bitmap : Bitmap? = null
        private var mCanvasPaint : Paint? = null
          private var mDrowPaint : Paint? = null
    private var mBrushSize : Float = 0.toFloat()
    private var color = Color.BLACK
    private var canvas :  Canvas? = null
    private val mPathList = ArrayList<CustomPath>()
    private val undoPath = ArrayList<CustomPath>()

    init{
            setUpDesign()
    }

    fun onUndoClick(){
        if(mPathList.size > 0)
        {
            undoPath.add(mPathList.removeAt(mPathList.size-1))
            invalidate()
        }
    }

    private fun setUpDesign() {
        mDrowPaint = Paint()
        mpath = CustomPath(color, mBrushSize)
        mDrowPaint!!.color = color
        mDrowPaint!!.style = Paint.Style.STROKE
        mDrowPaint!!.strokeJoin = Paint.Join.ROUND
        mDrowPaint!!.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        //mBrushSize = 20.toFloat() //default value
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        bitmap = Bitmap.createBitmap(h,  w, Bitmap.Config.ARGB_8888)
            canvas = Canvas(bitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(bitmap!!, 0f, 0f, mCanvasPaint)
        for(path in mPathList){
            mDrowPaint!!.strokeWidth = path.brushThickness
            mDrowPaint!!.color = path.color
            canvas.drawPath(path, mDrowPaint!!)
        }

        if(!mpath!!.isEmpty){
            mDrowPaint!!.strokeWidth = mpath!!.brushThickness
            mDrowPaint!!.color = mpath!!.color
            canvas.drawPath(mpath!!, mDrowPaint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event?.x
        val y = event?.y

        when(event?.action){
            MotionEvent.ACTION_DOWN->{
                mpath!!.brushThickness = mBrushSize
                mpath!!.color = color

                mpath!!.reset()
                mpath!!.moveTo(x!!,y!!)
            }
            MotionEvent.ACTION_MOVE->{
               if(x!=null){
                   if(y!=null){
                       mpath!!.lineTo(x,y)
                   }
               }
            }
            MotionEvent.ACTION_UP ->{
                mPathList.add(mpath!!)
                mpath = CustomPath(color, mBrushSize)
            }
            else-> return false
        }
        invalidate()
        return true
    }
    internal inner class  CustomPath(var color : Int, var brushThickness : Float) : Path(){

    }
    fun setBrushSize(brushSize : Float){
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, brushSize, resources.displayMetrics)
        mDrowPaint!!.strokeWidth = mBrushSize
    }

    fun setColor(setSelectedolor: String){
        color = Color.parseColor(setSelectedolor)
        mDrowPaint!!.color = color
    }
}