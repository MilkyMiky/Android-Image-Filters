package com.example.opengl.image.gpuimage.filters

import android.opengl.GLES20
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

const val COLOR_MIX_FRAGMENT_SHADER = "" +
        "precision lowp float;\n" +
        "varying lowp vec2 textureCoordinate;\n" +
        "uniform sampler2D inputImageTexture;\n" +

        "uniform lowp vec3 redHSV;\n" +
        "uniform lowp vec3 orangeHSV;\n" +
        "uniform lowp vec3 yellowHSV;\n" +
        "uniform lowp vec3 greenHSV;\n" +
        "uniform lowp vec3 cyanHSV;\n" +
        "uniform lowp vec3 blueHSV;\n" +
        "uniform lowp vec3 violetHSV;\n" +
        "uniform lowp vec3 magentaHSV;\n" +

        "const float hueRedStart = 330. / 360.;\n" +
        "const float hueClearRed = 0. / 360.;\n" +
        "const float hueRedEnd = 20. / 360.;\n" +

        "const float hueOrangeStart = 20. / 360.;\n" +
        "const float hueClearOrange = 30. / 360.;\n" +
        "const float hueOrangeEnd = 40. / 360.;\n" +

        "const float hueYellowStart = 40. / 360.;\n" +
        "const float hueClearYellow = 60. / 360.;\n" +
        "const float hueYellowEnd = 70. / 360.;\n" +

        "const float hueGreenStart = 80. / 360.;\n" +
        "const float hueClearGreen = 120. / 360.;\n" +
        "const float hueGreenEnd = 160. / 360.;\n" +

        "const float hueCyanStart = 170. / 360.;\n" +
        "const float hueClearCyan = 180. / 360.;\n" +
        "const float hueCyanEnd = 190. / 360.;\n" +

        "const float hueBlueStart = 190. / 360.;\n" +
        "const float hueClearBlue = 240. / 360.;\n" +
        "const float hueBlueEnd = 280. / 360.;\n" +

        "const float hueVioletStart = 280. / 360.;\n" +
        "const float hueClearViolet = 300. / 360.;\n" +
        "const float hueVioletEnd = 310. / 360.;\n" +

        "const float hueMagentaStart = 310. / 360.;\n" +
        "const float hueClearMagenta = 315. / 360.;\n" +
        "const float hueMagentaEnd = 330. / 360.;\n" +

        " float RGBToL(lowp vec3 color)\n" +
        "    {\n" +
        "         float fmin = min(min(color.r, color.g), color.b);    //Min. value of RGB\n" +
        "         float fmax = max(max(color.r, color.g), color.b);    //Max. value of RGB\n" +
        "        return (fmax + fmin) / 2.0; // Luminance\n" +
        "    }\n" +

        "lowp vec3 RGBToHSL(lowp vec3 color)\n" +
        "    {\n" +
        "        lowp vec3 hsl; // init to 0 to avoid warnings ? (and reverse if + remove first part)\n" +
        "         float fmin = min(min(color.r, color.g), color.b);    //Min. value of RGB\n" +
        "         float fmax = max(max(color.r, color.g), color.b);    //Max. value of RGB\n" +
        "         float delta = fmax - fmin;             //Delta RGB value\n" +
        "        hsl.z = (fmax + fmin) / 2.0; // Luminance\n" +
        "        if (delta == 0.0)\t\t//This is a gray, no chroma...\n" +
        "        {\n" +
        "            hsl.x = 0.0;\t// Hue\n" +
        "            hsl.y = 0.0;\t// Saturation\n" +
        "        }\n" +
        "        else                                    //Chromatic data...\n" +
        "        {\n" +
        "            if (hsl.z < 0.5)\n" +
        "                hsl.y = delta / (fmax + fmin); // Saturation\n" +
        "            else\n" +
        "                hsl.y = delta / (2.0 - fmax - fmin); // Saturation\n" +
        "            \n" +
        "             float deltaR = (((fmax - color.r) / 6.0) + (delta / 2.0)) / delta;\n" +
        "             float deltaG = (((fmax - color.g) / 6.0) + (delta / 2.0)) / delta;\n" +
        "             float deltaB = (((fmax - color.b) / 6.0) + (delta / 2.0)) / delta;\n" +
        "            \n" +
        "            if (color.r == fmax )\n" +
        "                hsl.x = deltaB - deltaG; // Hue\n" +
        "            else if (color.g == fmax)\n" +
        "                hsl.x = (1.0 / 3.0) + deltaR - deltaB; // Hue\n" +
        "            else if (color.b == fmax)\n" +
        "                hsl.x = (2.0 / 3.0) + deltaG - deltaR; // Hue\n" +
        "            \n" +
        "            if (hsl.x < 0.0)\n" +
        "                hsl.x += 1.0; // Hue\n" +
        "            else if (hsl.x > 1.0)\n" +
        "                hsl.x -= 1.0; // Hue\n" +
        "        }\n" +
        "        return hsl;\n" +
        "    }\n" +

        " float HueToRGB( float f1,  float f2,  float hue)\n" +
        "    {\n" +
        "        if (hue < 0.0)\n" +
        "            hue += 1.0;\n" +
        "        else if (hue > 1.0)\n" +
        "            hue -= 1.0;\n" +
        "         float res;\n" +
        "        if ((6.0 * hue) < 1.0)\n" +
        "            res = f1 + (f2 - f1) * 6.0 * hue;\n" +
        "        else if ((2.0 * hue) < 1.0)\n" +
        "            res = f2;\n" +
        "        else if ((3.0 * hue) < 2.0)\n" +
        "            res = f1 + (f2 - f1) * ((2.0 / 3.0) - hue) * 6.0;\n" +
        "        else\n" +
        "            res = f1;\n" +
        "        return res;\n" +
        "    }\n" +

        "lowp vec3 HSLToRGB(lowp vec3 hsl)\n" +
        "    {\n" +
        "        lowp vec3 rgb;\n" +
        "        if (hsl.y == 0.0)\n" +
        "            rgb = vec3(hsl.z); // Luminance\n" +
        "        else\n" +
        "        {\n" +
        "             float f2;\n" +
        "            if (hsl.z < 0.5)\n" +
        "                f2 = hsl.z * (1.0 + hsl.y);\n" +
        "            else\n" +
        "                f2 = (hsl.z + hsl.y) - (hsl.y * hsl.z);\n" +
        "             float f1 = 2.0 * hsl.z - f2;\n" +
        "            rgb.r = HueToRGB(f1, f2, hsl.x + (1.0/3.0));\n" +
        "            rgb.g = HueToRGB(f1, f2, hsl.x);\n" +
        "            rgb.b= HueToRGB(f1, f2, hsl.x - (1.0/3.0));\n" +
        "        }\n" +
        "        return rgb;\n" +
        "    }\n" +

        "lowp vec3 mixHue(lowp vec3 colorHSV, float hue, float colorStart, float colorEnd, float colorRight, float colorLeft) {\n" +
        "   float deltaColor = 0.; \n " +

        "    if((colorHSV.x >= hueRedStart)) {\n" +
        "       if(hue < 0.){ \n " +
        "           float percent = (colorHSV.x - colorStart) * 100. / (colorEnd + 1.0 - colorStart);\n" +
        "           float nearestColorValue = percent * (colorStart - colorRight) / 100. + colorRight;\n" +
        "           deltaColor = (colorHSV.x - nearestColorValue) * hue / 100.;\n" +
        "       } else if(hue > 0.) {\n" +
        "           float percent = (colorHSV.x - colorStart) * 100. / (colorEnd + 1.0 - colorStart);\n" +
        "           float nearestColorValue = (percent) * (colorLeft - colorEnd) / 100. + colorEnd;\n" +
        "           colorHSV.x = (1. - colorHSV.x) * hue / 100.;\n" +
        "       } \n" +
        "   } else if((colorHSV.x < hueRedEnd)) {\n" +
        "       if(hue < 0.) {\n" +
        "           float percent = (colorHSV.x) * 100. / (colorEnd);\n" +
        "           float nearestColorValue = percent * (colorStart - colorRight) / 100. + colorRight;\n" +
        "           colorHSV.x = (1. - nearestColorValue) * hue / 100.;\n" +
        "       } \n" +
        "       else  if(hue > 0.){ \n " +
        "           float percent = (colorHSV.x - colorStart) * 100. / (colorEnd - colorStart);\n" +
        "           float nearestColorValue = percent * (colorLeft) / 100.;\n" +
        "           deltaColor = (nearestColorValue - colorHSV.x) * hue / 100.;\n" +
        "       } \n" +
        "   } else { \n" +
        "       if(hue < 0.){ \n " +
        "           float percent = (colorHSV.x - colorStart) * 100. / (colorEnd - colorStart);\n" +
        "           float nearestColorValue = percent * (colorStart - colorLeft) / 100. + colorLeft;\n" +
        "           deltaColor = (colorHSV.x - nearestColorValue) * hue / 100.;\n" +
        "       } else if(hue > 0.){\n" +
        "           float percent = (colorHSV.x - colorStart) * 100. / (colorEnd - colorStart);\n" +
        "           float nearestColorValue = (100. - percent) * (colorRight - colorEnd) / 100. + colorEnd;\n" +
        "           deltaColor = (nearestColorValue - colorHSV.x) * hue / 100.;\n" +
        "       } \n" +
        "   } \n" +
        "   return vec3(colorHSV.x + deltaColor, colorHSV.y, colorHSV.z); \n" +
        "}\n" +

        "lowp vec3 mixValue(lowp vec3 newColorRGB, float value) {\n" +
        "   vec3 color = vec3((newColorRGB.rgb + vec3(value)));\n" +
        "   return color;\n" +
        "}\n" +

        "lowp vec3 mixSaturation(lowp vec3 textureColor, lowp vec3 colorRGB, float saturation) {\n" +
        "   const lowp vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +
        "   float luminance = dot(textureColor.rgb, luminanceWeighting);\n" +
        "   lowp vec3 greyScaleColor = vec3(luminance);\n" +
        "   vec3 color = vec3(mix(greyScaleColor, colorRGB.rgb, saturation));\n" +
        "   return color;\n" +
        "}\n" +

        "lowp vec3 colorMix(lowp vec3 textureColor, lowp vec3 colorHSV, lowp vec3 changeHSV, float colorStart, float colorEnd, float colorRight, float colorLeft) {\n" +
        "    vec3 newColorHSV = mixHue(colorHSV, changeHSV.x, colorStart, colorEnd, colorRight, colorLeft);\n" +
        "    vec3 newColorRGB = HSLToRGB(newColorHSV); \n" +
        "    newColorRGB = mixSaturation(textureColor, newColorRGB, changeHSV.y);\n" +
        "    newColorRGB = mixValue(newColorRGB, changeHSV.z);\n" +
        "    return newColorRGB;\n" +
        "}\n" +

        "void main()\n" +
        "{\n" +
        "   lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
        "   lowp vec3 newColor = textureColor.rgb; \n" +
        "   lowp vec3 colorHSL = RGBToHSL(textureColor.rgb);\n" +

        "    if((colorHSL.x >= hueRedStart) || (colorHSL.x < hueRedEnd)){\n      " +
        "        newColor = colorMix(textureColor.rgb, colorHSL, redHSV, hueRedStart, hueRedEnd, hueClearMagenta, hueClearOrange);\n " +
        "    }\n" +

        "    if((colorHSL.x >= hueOrangeStart) && (colorHSL.x < hueOrangeEnd )){\n      " +
        "        newColor = colorMix(textureColor.rgb, colorHSL, orangeHSV, hueOrangeStart, hueOrangeEnd, hueClearYellow, 0.);\n " +
        "    }\n" +

        "    if((colorHSL.x >= hueYellowStart) && (colorHSL.x < hueYellowEnd )){\n" +
        "        newColor = colorMix(textureColor.rgb, colorHSL, yellowHSV, hueYellowStart, hueYellowEnd, hueClearGreen, hueClearOrange);\n " +
        "    }\n" +

        "    if((colorHSL.x >= hueGreenStart) && (colorHSL.x < hueGreenEnd )){\n      " +
        "        newColor = colorMix(textureColor.rgb, colorHSL, greenHSV, hueGreenStart, hueGreenEnd, hueClearCyan, hueClearYellow);\n " +
        "    }\n" +

        "    if((colorHSL.x >= hueCyanStart) && (colorHSL.x < hueCyanEnd )){\n      " +
        "        newColor = colorMix(textureColor.rgb, colorHSL, cyanHSV, hueCyanStart, hueCyanEnd, hueClearBlue, hueClearGreen);\n " +
        "    }\n" +

        "    if((colorHSL.x >= hueBlueStart) && (colorHSL.x < hueBlueEnd )){\n      " +
        "        newColor = colorMix(textureColor.rgb, colorHSL, blueHSV, hueBlueStart, hueBlueEnd, hueClearViolet, hueClearCyan);\n " +
        "    }\n" +

        "    if((colorHSL.x >= hueVioletStart) && (colorHSL.x < hueVioletEnd )){\n      " +
        "        newColor = colorMix(textureColor.rgb, colorHSL, violetHSV, hueVioletStart, hueVioletEnd, hueClearMagenta, hueClearBlue);\n " +
        "    }\n" +

        "    if((colorHSL.x >= hueMagentaStart) && (colorHSL.x < hueMagentaEnd )){\n      " +
        "        newColor = colorMix(textureColor.rgb, colorHSL, magentaHSV, hueMagentaStart, hueMagentaEnd, 1., hueClearViolet);\n " +
        "    }\n" +

        "     gl_FragColor = vec4(newColor, textureColor.w);\n" +
        "}\n"

class ColorMixInput(
    var redHSV: HSV = HSV(),
    var orangeHSV: HSV = HSV(),
    var yellowHSV: HSV = HSV(),
    var greenHSV: HSV = HSV(),
    var cyanHSV: HSV = HSV(),
    var blueHSV: HSV = HSV(),
    var violetHSV: HSV = HSV(),
    var magentaHSV: HSV = HSV()
)

class ColorMixFilter(
    private val colorMixInput: ColorMixInput
) : GPUImageFilter(GPUImageFilter.NO_FILTER_VERTEX_SHADER, COLOR_MIX_FRAGMENT_SHADER) {
    private val colorRedStart = 330.0f
    private val colorRedEnd = 20.0f

    private val colorOrangeStart = 20.0f
    private val colorOrangeEnd = 40.0f

    private val colorYellowStart = 40.0f
    private val colorYellowEnd = 70.0f

    private val colorGreenStart = 80.0f
    private val colorGreenEnd = 160.0f

    private val colorCyanStart = 170.0f
    private val colorCyanEnd = 190.0f

    private val colorBlueStart = 190.0f
    private val colorBlueEnd = 280.0f

    private val colorVioletStart = 280.0f
    private val colorVioletEnd = 310.0f

    private val colorMagentaStart = 310.0f
    private val colorMagentaEnd = 320.0f

    private var hsvRedLocation = 0
    private var hsvOrangeLocation = 0
    private var hsvYellowLocation = 0
    private var hsvGreenLocation = 0
    private var hsvCyanLocation = 0
    private var hsvBlueLocation = 0
    private var hsvVioletLocation = 0
    private var hsvMagentaLocation = 0

    override fun onInit() {
        super.onInit()
        hsvRedLocation = GLES20.glGetUniformLocation(program, "redHSV")
        hsvOrangeLocation = GLES20.glGetUniformLocation(program, "orangeHSV")
        hsvYellowLocation = GLES20.glGetUniformLocation(program, "yellowHSV")
        hsvGreenLocation = GLES20.glGetUniformLocation(program, "greenHSV")
        hsvCyanLocation = GLES20.glGetUniformLocation(program, "cyanHSV")
        hsvBlueLocation = GLES20.glGetUniformLocation(program, "blueHSV")
        hsvVioletLocation = GLES20.glGetUniformLocation(program, "violetHSV")
        hsvMagentaLocation = GLES20.glGetUniformLocation(program, "magentaHSV")
    }

    override fun onInitialized() {
        super.onInitialized()
        setLocations()
    }

    private fun setLocations() {
        setFloatVec3(
            hsvRedLocation,
            floatArrayOf(colorMixInput.redHSV.hue, colorMixInput.redHSV.saturation, colorMixInput.redHSV.value)
        )
        setFloatVec3(
            hsvOrangeLocation,
            floatArrayOf(colorMixInput.orangeHSV.hue, colorMixInput.orangeHSV.saturation, colorMixInput.orangeHSV.value)
        )

        setFloatVec3(
            hsvYellowLocation,
            floatArrayOf(colorMixInput.yellowHSV.hue, colorMixInput.yellowHSV.saturation, colorMixInput.yellowHSV.value)
        )

        setFloatVec3(
            hsvGreenLocation,
            floatArrayOf(colorMixInput.greenHSV.hue, colorMixInput.greenHSV.saturation, colorMixInput.greenHSV.value)
        )

        setFloatVec3(
            hsvCyanLocation,
            floatArrayOf(colorMixInput.cyanHSV.hue, colorMixInput.cyanHSV.saturation, colorMixInput.cyanHSV.value)
        )

        setFloatVec3(
            hsvBlueLocation,
            floatArrayOf(colorMixInput.blueHSV.hue, colorMixInput.blueHSV.saturation, colorMixInput.blueHSV.value)
        )

        setFloatVec3(
            hsvVioletLocation,
            floatArrayOf(colorMixInput.violetHSV.hue, colorMixInput.violetHSV.saturation, colorMixInput.violetHSV.value)
        )

        setFloatVec3(
            hsvMagentaLocation,
            floatArrayOf(
                colorMixInput.magentaHSV.hue,
                colorMixInput.magentaHSV.saturation,
                colorMixInput.magentaHSV.value
            )
        )
    }
}

