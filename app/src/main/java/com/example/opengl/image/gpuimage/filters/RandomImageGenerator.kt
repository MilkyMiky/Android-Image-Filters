package com.example.opengl.image.gpuimage.filters


import android.opengl.GLES20
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

const val GENERATE_RANDOM_FRAGMENT_SHADER = "" +
        "precision highp float;\n" +
        "uniform float iWidth;\n" +
        "uniform float iHeight;\n" +

        "float rand(vec2 n)\n" +
        "{\n" +
        "  return fract(sin(dot(n.xy, vec2(12.9898, 78.233)))* 43758.5453);\n" +
        "}\n" +

        "void main() \n" +
        "{\n" +
        "    float x = rand(vec2(gl_FragCoord.xy / vec2(iWidth, iHeight)));\n" +
        "    float y = rand(vec2(x,x));\n" +
        "    float z = rand(vec2(y,y));\n" +
        "gl_FragColor = vec4(x, y, z, 1.0);\n" +
        "}"


class RandomImageGenerator(
    private val width: Float,
    private val height: Float
) : GPUImageFilter(GPUImageFilter.NO_FILTER_VERTEX_SHADER, GENERATE_RANDOM_FRAGMENT_SHADER) {

    private var wLoc = 0
    private var hLoc = 0

    override fun onInit() {
        super.onInit()
        wLoc = GLES20.glGetUniformLocation(program, "iWidth")
        hLoc = GLES20.glGetUniformLocation(program, "iHeight")
    }

    override fun onInitialized() {
        super.onInitialized()
        setWidth()
        setHeight()
    }

    private fun setWidth(){
        setFloat(wLoc, width)
    }

    private fun setHeight(){
        setFloat(hLoc, height)
    }
}

