package com.example.opengl.image.gpuimage.filters

import android.opengl.GLES20
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

const val GRAIN_FRAGMENT_SHADER = "" +
        "precision highp float;\n" +

        "uniform float width;\n" +
        "uniform float height;\n" +
        "uniform sampler2D inputImageTexture;\n" +
        "uniform sampler2D inputImageTexture2;\n" +
        "uniform lowp float contrast;\n" +
        "uniform lowp float brightness;\n" +
        "varying highp vec2 textureCoordinate;\n" +

        "const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);\n" +

        "float rand(vec2 n)\n" +
        "{\n" +
        "  return fract(sin(dot(n.xy, vec2(12.9898, 78.233)))* 43758.5453);\n" +
        "}\n" +

        "void main() \n" +
        "{\n" +
        "     float x = rand(vec2(gl_FragCoord.xy / vec2(width, height)));\n" +
        "     float y = rand(vec2(x,x));\n" +
        "     float z = rand(vec2(y,y));\n" +
        "     vec4 randomColor = vec4(x, y, z, 1.0);\n" +
        "     vec4 contrastColor = vec4(((randomColor.rgb - vec3(0.5)) * contrast + vec3(0.5)), randomColor.w);\n" +

        "     mediump vec4 base = vec4((contrastColor.rgb + vec3(brightness)), contrastColor.w);\n" +
        "     mediump vec4 overlay = texture2D(inputImageTexture, textureCoordinate);\n" +

        "\n" +
        "     highp float ra;\n" +
        "     if (2.0 * overlay.r < overlay.a) {\n" +
        "         ra = 2.0 * overlay.r * base.r + overlay.r * (1.0 - base.a) + base.r * (1.0 - overlay.a);\n" +
        "     } else {\n" +
        "         ra = overlay.a * base.a - 2.0 * (base.a - base.r) * (overlay.a - overlay.r) + overlay.r * (1.0 - base.a) + base.r * (1.0 - overlay.a);\n" +
        "     }\n" +
        "     \n" +
        "     highp float ga;\n" +
        "     if (2.0 * overlay.g < overlay.a) {\n" +
        "         ga = 2.0 * overlay.g * base.g + overlay.g * (1.0 - base.a) + base.g * (1.0 - overlay.a);\n" +
        "     } else {\n" +
        "         ga = overlay.a * base.a - 2.0 * (base.a - base.g) * (overlay.a - overlay.g) + overlay.g * (1.0 - base.a) + base.g * (1.0 - overlay.a);\n" +
        "     }\n" +
        "     \n" +
        "     highp float ba;\n" +
        "     if (2.0 * overlay.b < overlay.a) {\n" +
        "         ba = 2.0 * overlay.b * base.b + overlay.b * (1.0 - base.a) + base.b * (1.0 - overlay.a);\n" +
        "     } else {\n" +
        "         ba = overlay.a * base.a - 2.0 * (base.a - base.b) * (overlay.a - overlay.b) + overlay.b * (1.0 - base.a) + base.b * (1.0 - overlay.a);\n" +
        "     }\n" +
        "     \n" +
        "     gl_FragColor = vec4(ra, ga, ba, 1.0);\n" +
        "}"

class GrainFilter(
    private val imageSize: ImageSize,
    private val brightness: Float,
    private val contrast: Float
) : GPUImageFilter(GPUImageFilter.NO_FILTER_VERTEX_SHADER, GRAIN_FRAGMENT_SHADER) {

    private var widthLocation = 0
    private var heightLocation = 0
    private var contrastLocation = 0
    private var brightnessLocation = 0

    override fun onInit() {
        super.onInit()
        widthLocation = GLES20.glGetUniformLocation(program, "width")
        heightLocation = GLES20.glGetUniformLocation(program, "height")
        contrastLocation = GLES20.glGetUniformLocation(program, "contrast")
        brightnessLocation = GLES20.glGetUniformLocation(program, "brightness")
    }

    override fun onInitialized() {
        super.onInitialized()
        setLocations()
    }

    private fun setLocations() {
        setFloat(widthLocation, imageSize.width)
        setFloat(heightLocation, imageSize.height)
        setFloat(contrastLocation, contrast)
        setFloat(brightnessLocation, brightness)
    }

}

class ImageSize(
    var width: Float = 0.0f,
    var height: Float = 0.0f
)