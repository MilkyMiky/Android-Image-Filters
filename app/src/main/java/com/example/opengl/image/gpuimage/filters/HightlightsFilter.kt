package com.example.opengl.image.gpuimage.filters

import android.opengl.GLES20
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter


const val HIGHLIGHT_SHADOW_FRAGMENT_SHADER = "" +
        " uniform sampler2D inputImageTexture;\n" +
        " varying highp vec2 textureCoordinate;\n" +
        "  \n" +
//        " uniform lowp float shadows;\n" +
        " uniform lowp float highlights;\n" +
        " \n" +
        " const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +
        " \n" +
        " void main()\n" +
        " {\n" +
        " 	lowp vec4 source = texture2D(inputImageTexture, textureCoordinate);\n" +
        " 	mediump float luminance = dot(source.rgb, luminanceWeighting);\n" +
        " \n" +
//        " 	mediump float shadow = clamp((pow(luminance, 1.0/(shadows+1.0)) + (-0.76)*pow(luminance, 2.0/(shadows+1.0))) - luminance, 0.0, 1.0);\n" +
        " 	mediump float highlight = clamp((1.0 - (pow(1.0-luminance, 1.0/(2.0-highlights)) + (-0.8)*pow(1.0-luminance, 2.0/(2.0-highlights)))) - luminance, -1.0, 0.0);\n" +
        " 	lowp vec3 result = vec3(0.0, 0.0, 0.0) + ((luminance + highlight) - 0.0) * ((source.rgb - vec3(0.0, 0.0, 0.0))/(luminance - 0.0));\n" +
        " \n" +
        " 	gl_FragColor = vec4(result.rgb, source.a);\n" +
        " }"

class HightlightsFilter(private val highlights: Float) :
    GPUImageFilter(GPUImageFilter.NO_FILTER_VERTEX_SHADER, HIGHLIGHT_SHADOW_FRAGMENT_SHADER) {

    private var highlightsLocation: Int = 0

    override fun onInit() {
        super.onInit()
        highlightsLocation = GLES20.glGetUniformLocation(program, "highlights")
//        shadowsLocation = GLES20.glGetUniformLocation(program, "shadows")
    }

    override fun onInitialized() {
        super.onInitialized()
        setFloat(highlightsLocation, this.highlights)
    }

}