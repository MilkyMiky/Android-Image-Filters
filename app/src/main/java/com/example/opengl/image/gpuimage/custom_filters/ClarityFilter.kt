package com.example.opengl.image.gpuimage.custom_filters

import android.opengl.GLES20
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

const val SHIFT_RIGHT_SHADER = "" +
        " varying highp vec2 textureCoordinate;\n" +
        " \n" +
        " uniform sampler2D inputImageTexture;\n" +
        " uniform highp float exposure;\n" +
        " \n" +
        " void main()\n" +
        " {\n" +
        "     highp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
        "     \n" +
        "     gl_FragColor = vec4(textureColor.rgb * pow(2.0, exposure), textureColor.w);\n" +
        " } "


class ClarityFilter : GPUImageFilter(GPUImageFilter.NO_FILTER_VERTEX_SHADER, SHIFT_RIGHT_SHADER) {

    private var clarityLocation: Int = 0

    private var clarity: Float = 0.toFloat()


    override fun onInit() {
        super.onInit()
        clarityLocation = GLES20.glGetUniformLocation(getProgram(), "clarity")
    }

    override fun onInitialized() {
        super.onInitialized()
        setClarity(clarity)
    }

    fun setClarity(clarity: Float) {
        this.clarity = clarity
        setFloat(clarityLocation, this.clarity)
    }
}