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
    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var prevScale : Float = 0f, var dir : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class CCNode(var i : Int, val state : State = State()) {

        private var next : CCNode? = null
        private var prev : CCNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = CCNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint, sc : Float) {
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            val r : Float = Math.min(w, h) / 2
            var x = 0f
            if (sc != 0f) {
                x = w * (1 - sc)
            }
            paint.color = Color.parseColor(colors[i])
            canvas.save()
            canvas.translate(r - w * state.scale + x, h / 2)
            canvas.drawCircle(0f, 0f, r, paint)
            canvas.restore()
            if (state.scale != 0f) {
                next?.draw(canvas, paint, state.scale)
            }
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : CCNode {
            var curr : CCNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class ColoredCircles(var i : Int) {

        private var root : CCNode = CCNode(0)
        private var curr : CCNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint, 0f)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir, {
                    dir *= -1
                })
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : ColoredCircleView) {

        private val animator : Animator = Animator(view)
        private val cc : ColoredCircles = ColoredCircles(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            cc.draw(canvas, paint)
            animator.animate {
                cc.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            cc.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity: Activity) : ColoredCircleView {
            val view : ColoredCircleView = ColoredCircleView(activity)
            activity.setContentView(view)
            return view
        }
    }
}