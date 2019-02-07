package com.example.opengl.image.gpuimage.widgets

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.graphics.PorterDuff
import android.graphics.ComposeShader
import android.graphics.LinearGradient
import android.graphics.Shader
import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import com.example.opengl.image.gpuimage.R


class ColorPickerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var lastX = Integer.MIN_VALUE
    private var lastY: Int = Integer.MAX_VALUE
    private val hsv = floatArrayOf(1f, 1f, 1f)

    private var paint: Paint? = null
    private val gradColors =
        intArrayOf(Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED)
    private val gradAlpha = intArrayOf(Color.WHITE, Color.BLACK)

    private var shader: Shader? = null
    private var pointerDrawable: Drawable? = null
    private val gradientRect = RectF()
    private var pointerHeight: Int = 32
    private var pointerWidth: Int = 32
    private var padding: Int = 16
    private var listener: OnColorPickedListener? = null

    fun setOnColorPickedListener(l: OnColorPickedListener) {
        listener = l
    }

    init {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        pointerDrawable = ContextCompat.getDrawable(context, R.drawable.pointer_simple)
        setLayerType(View.LAYER_TYPE_SOFTWARE, paint)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        gradientRect.set(
            paddingLeft.toFloat(),
            paddingTop.toFloat(),
            (right - left - paddingRight).toFloat(),
            (bottom - top - paddingBottom).toFloat()
        )

        if (changed) buildShader()
        if (pointerDrawable != null) {
            pointerDrawable!!.setBounds(0, 0, pointerWidth, pointerHeight)
            updatePointerPosition()
        }
    }

    private fun buildShader() {
        val gradientShader = LinearGradient(
            gradientRect.left,
            gradientRect.top,
            gradientRect.right,
            gradientRect.top,
            gradColors,
            null,
            Shader.TileMode.CLAMP
        )
        val alphaShader = LinearGradient(
            0f,
            gradientRect.top,
            0f,
            gradientRect.bottom,
            gradAlpha,
            null,
            Shader.TileMode.CLAMP
        )
        shader = ComposeShader(gradientShader, alphaShader, PorterDuff.Mode.MULTIPLY)
        paint!!.shader = shader
    }

    private fun updatePointerPosition() {
        if (gradientRect.width() != 0f && gradientRect.height() != 0f) {
            lastX = hueToPoint(hsv[0])
            lastY = saturationToPoint(hsv[1])
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        lastX = event!!.x.toInt()
        lastY = event.y.toInt()
        onUpdateColorSelection(lastX, lastY)
        invalidate()
        return true
    }

    private fun onUpdateColorSelection(cordX: Int, cordY: Int) {
        val x = Math.max(gradientRect.left, Math.min(cordX.toFloat(), gradientRect.right)).toInt()
        val y = Math.max(gradientRect.top, Math.min(cordY.toFloat(), gradientRect.bottom)).toInt()

        val hue = pointToHue(x.toFloat())
        val sat = pointToSaturation(y.toFloat())
        listener?.onColorPicked(hue, sat)
    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (shader != null) canvas!!.drawRect(gradientRect, paint!!)
        onDrawPointer(canvas!!)
    }

    private fun onDrawPointer(canvas: Canvas) {
        if (pointerDrawable != null) {

            var tx: Float = (lastX - pointerWidth).toFloat()
            var ty: Float = (lastY - pointerWidth).toFloat()

            tx = Math.max(gradientRect.left - padding, Math.min(tx, gradientRect.right + padding - pointerWidth))
            ty = Math.max(gradientRect.top - padding, Math.min(ty, gradientRect.bottom + padding - pointerHeight))

            canvas.translate(tx, ty)
            pointerDrawable!!.draw(canvas)
            canvas.translate(-tx, -ty)
        }
    }

    private fun pointToHue(cordX: Float): Float {
        var x = cordX
        x -= gradientRect.left
        return x * 360f / gradientRect.width()
    }

    private fun hueToPoint(hue: Float): Int {
        return (gradientRect.left + hue * gradientRect.width() / 360).toInt()
    }

    private fun pointToSaturation(sat: Float): Float {
        var y = sat
        y -= gradientRect.top
        return 1 - 1f / gradientRect.height() * y
    }

    private fun saturationToPoint(sat: Float): Int {
        val x = 1 - sat
        return (gradientRect.top + gradientRect.height() * x).toInt()
    }
}

interface OnColorPickedListener {
    fun onColorPicked(hue: Float, sat: Float)
}