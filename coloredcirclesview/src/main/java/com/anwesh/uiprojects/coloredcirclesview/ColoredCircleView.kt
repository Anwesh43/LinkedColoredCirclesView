package com.anwesh.uiprojects.coloredcirclesview

/**
 * Created by anweshmishra on 28/08/19.
 */

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.Color
import android.content.Context

val scGap : Float = 0.05f
val colors : Array<String> = arrayOf("#4527A0", "#0D47A1", "#f44336", "#00C853", "#E65100")
val backColor : Int = Color.parseColor("#BDBDBD")

class ColoredCircleView(ctx : Context) : View(ctx){

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}