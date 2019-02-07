package com.example.opengl.image.gpuimage.filters

import android.opengl.GLES20
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

const val SPLIT_TONE_FRAGMENT_SHADER = "" +
        " varying highp vec2 textureCoordinate;\n" +
        "uniform sampler2D inputImageTexture;\n" +

        "uniform highp float redHighlightsShift;\n" +
        "uniform highp float greenHighlightsShift;\n" +
        "uniform highp float blueHighlightsShift;\n" +

        "uniform highp float redShadowsShift;\n" +
        "uniform highp float greenShadowsShift;\n" +
        "uniform highp float blueShadowsShift;\n" +

        "uniform highp float border;\n" +

        "highp float RGBToL(highp vec3 color)\n" +
        "    {\n" +
        "        highp float fmin = min(min(color.r, color.g), color.b);    //Min. value of RGB\n" +
        "        highp float fmax = max(max(color.r, color.g), color.b);    //Max. value of RGB\n" +
        "        return (fmax + fmin) / 2.0; // Luminance\n" +
        "    }\n" +

        "highp vec3 RGBToHSL(highp vec3 color)\n" +
        "    {\n" +
        "        highp vec3 hsl; // init to 0 to avoid warnings ? (and reverse if + remove first part)\n" +
        "        highp float fmin = min(min(color.r, color.g), color.b);    //Min. value of RGB\n" +
        "        highp float fmax = max(max(color.r, color.g), color.b);    //Max. value of RGB\n" +
        "        highp float delta = fmax - fmin;             //Delta RGB value\n" +
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
        "            highp float deltaR = (((fmax - color.r) / 6.0) + (delta / 2.0)) / delta;\n" +
        "            highp float deltaG = (((fmax - color.g) / 6.0) + (delta / 2.0)) / delta;\n" +
        "            highp float deltaB = (((fmax - color.b) / 6.0) + (delta / 2.0)) / delta;\n" +
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

        "highp float HueToRGB(highp float f1, highp float f2, highp float hue)\n" +
        "    {\n" +
        "        if (hue < 0.0)\n" +
        "            hue += 1.0;\n" +
        "        else if (hue > 1.0)\n" +
        "            hue -= 1.0;\n" +
        "        highp float res;\n" +
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

        "highp vec3 HSLToRGB(highp vec3 hsl)\n" +
        "    {\n" +
        "        highp vec3 rgb;\n" +
        "        if (hsl.y == 0.0)\n" +
        "            rgb = vec3(hsl.z); // Luminance\n" +
        "        else\n" +
        "        {\n" +
        "            highp float f2;\n" +
        "            if (hsl.z < 0.5)\n" +
        "                f2 = hsl.z * (1.0 + hsl.y);\n" +
        "            else\n" +
        "                f2 = (hsl.z + hsl.y) - (hsl.y * hsl.z);\n" +
        "            highp float f1 = 2.0 * hsl.z - f2;\n" +
        "            rgb.r = HueToRGB(f1, f2, hsl.x + (1.0/3.0));\n" +
        "            rgb.g = HueToRGB(f1, f2, hsl.x);\n" +
        "            rgb.b= HueToRGB(f1, f2, hsl.x - (1.0/3.0));\n" +
        "        }\n" +
        "        return rgb;\n" +
        "    }\n" +

        "void main()\n" +
        "{\n" +
        "   highp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
        "   highp float lightness = RGBToL(textureColor.rgb);\n" +

        "   highp vec3 highlightsShift = vec3(redHighlightsShift, greenHighlightsShift, blueHighlightsShift);\n" +
        "   highp vec3 shadowsShift = vec3(redShadowsShift, greenShadowsShift, blueShadowsShift);\n" +

        "   const highp float a = 0.25;\n" +
        "   const highp float b = 0.333;\n" +
        "   const highp float scale = 0.7;\n" +

        "   highp vec3 shadows = (clamp((lightness - border) /  -a + 0.5, 0.0, 1.0) * scale) * shadowsShift;\n" +
        "   highp vec3 highlights = (clamp((lightness + (1.0 - border) - 1.0) /  a + 0.5, 0.0, 1.0) * scale) * highlightsShift;\n" +

        "   highp vec3 newColor = textureColor.rgb + shadows + highlights;\n" +
        "   newColor = clamp(newColor, 0.0, 1.0);\n" +

        "   highp vec3 newHSL = RGBToHSL(newColor);\n" +
        "   highp float oldLum = RGBToL(textureColor.rgb);\n" +
        "   textureColor.rgb = HSLToRGB(vec3(newHSL.x, newHSL.y, oldLum));\n" +

        "   gl_FragColor = textureColor;\n" +
        "}\n"

class SplitToningFilter(
    private val highlightsRGB: RGB,
    private val shadowsRGB: RGB,
    private val border: Float
) : GPUImageFilter(GPUImageFilter.NO_FILTER_VERTEX_SHADER, SPLIT_TONE_FRAGMENT_SHADER) {

    private var redHighlightsLocation = 0
    private var greenHighlightsLocation = 0
    private var blueHighlightsLocation = 0
    private var redShadowsLocation = 0
    private var greenShadowsLocation = 0
    private var blueShadowsLocation = 0
    private var borderLocation = 0

    override fun onInit() {
        super.onInit()

        redHighlightsLocation = GLES20.glGetUniformLocation(program, "redHighlightsShift")
        greenHighlightsLocation = GLES20.glGetUniformLocation(program, "greenHighlightsShift")
        blueHighlightsLocation = GLES20.glGetUniformLocation(program, "blueHighlightsShift")
        redShadowsLocation = GLES20.glGetUniformLocation(program, "redShadowsShift")
        greenShadowsLocation = GLES20.glGetUniformLocation(program, "greenShadowsShift")
        blueShadowsLocation = GLES20.glGetUniformLocation(program, "blueShadowsShift")
        borderLocation = GLES20.glGetUniformLocation(program, "border")
    }

    override fun onInitialized() {
        super.onInitialized()
        setLocations()
    }

    private fun setLocations() {
        setFloat(redHighlightsLocation, highlightsRGB.r)
        setFloat(greenHighlightsLocation, highlightsRGB.g)
        setFloat(blueHighlightsLocation, highlightsRGB.b)
        setFloat(redShadowsLocation, shadowsRGB.r)
        setFloat(greenShadowsLocation, shadowsRGB.g)
        setFloat(blueShadowsLocation, shadowsRGB.b)
        setFloat(borderLocation, border)
    }
}


class HSV(
    var hue: Float = 0.0f,
    var saturation: Float = 0.5f,
    var value: Float = 0.0f
) {

    companion object {

        fun customHsvToRgb(hsv: HSV): RGB {
            var rgb = customHueToRgb(hsv.hue)
            rgb = customSaturationToRgb(hsv.saturation, rgb)
            return rgb
        }

        private fun customSaturationToRgb(saturation: Float, rgb: RGB): RGB = RGB(
            r = rgb.r * saturation,
            g = rgb.g * saturation,
            b = rgb.b * saturation
        )

        private fun customHueToRgb(hue: Float): RGB {
            val percentageOfOneCoefficient = 2.55f
            val percentageOfSixtyCoefficient = 1.66f
            val hundredPercent = 100
            val colorRed = 0
            val colorYellow = 60
            val colorGreen = 120
            val colorCyan = 180
            val colorBlue = 240
            val colorMagenta = 300

            if (colorRed <= hue && hue < colorYellow) {
                val huePercent = hue * percentageOfSixtyCoefficient
                return RGB(
                    r = (hundredPercent - huePercent) * percentageOfOneCoefficient,
                    b = -huePercent * percentageOfOneCoefficient
                )
            } else if (colorYellow <= hue && hue < colorGreen) {
                val huePercent = (hue - colorYellow) * percentageOfSixtyCoefficient
                return RGB(
                    b = -(hundredPercent - huePercent) * percentageOfOneCoefficient,
                    g = huePercent * percentageOfOneCoefficient
                )
            } else if (colorGreen <= hue && hue < colorCyan) {
                val huePercent = (hue - colorGreen) * percentageOfSixtyCoefficient
                return RGB(
                    r = -huePercent * percentageOfOneCoefficient,
                    g = (hundredPercent - huePercent) * percentageOfOneCoefficient
                )
            } else if (colorCyan <= hue && hue < colorBlue) {
                val huePercent = (hue - colorCyan) * percentageOfSixtyCoefficient
                return RGB(
                    r = -(hundredPercent - huePercent) * percentageOfOneCoefficient,
                    b = huePercent * percentageOfOneCoefficient
                )
            } else if (colorBlue <= hue && hue < colorMagenta) {
                val huePercent = (hue - colorBlue) * percentageOfSixtyCoefficient
                return RGB(
                    g = -huePercent * percentageOfOneCoefficient,
                    b = (hundredPercent - huePercent) * percentageOfOneCoefficient
                )
            } else {
                val huePercent = (hue - colorMagenta) * percentageOfSixtyCoefficient
                return RGB(
                    r = huePercent * percentageOfOneCoefficient,
                    g = -(hundredPercent - huePercent) * percentageOfOneCoefficient
                )
            }
        }
    }
}

class RGB(
    var r: Float = 0.0f,
    var g: Float = 0.0f,
    var b: Float = 0.0f
)

// MARK: - Split toning
//self.SPlightsChain = FilterChain { (imageBlock: CIImage?) -> CIImage? in
//    if self.originParams.splitToning == self.params.splitToning {
//        return imageBlock
//    }
//    let settings = self.params.splitToning
//    var balance = self.params.splitToning.balance
//    var resultSaturation: CGFloat = 0.5
//
//    balance = balance == 0 ? CGFloat.leastNormalMagnitude : balance
//
//
//    if balance > 0 {
//        resultSaturation = CGFloat(settings.lightsColor.saturation) * balance
//    }
//
//    let hLights = LightRoom.ColorAdjustment.WhitePointAdjust.init(color:
//    CIColor(color: UIColor(hue: CGFloat(settings.lightsColor.hue),
//    saturation: resultSaturation,
//    brightness: CGFloat(settings.lightsColor.brightness),
//    alpha: CGFloat(settings.lightsColor.alpha))))
//
//    let result = imageBlock >>> hLights
//            return result.outputImage
//}

//const val SPLIT_TONE_FRAGMENT_SHADER = "" +
//        "varying highp vec2 textureCoordinate;\n" +
//
//        "uniform sampler2D inputImageTexture;\n" +
//        "uniform highp float brightness;\n" +
//        "uniform mediump float hueAdjust;\n" +
//
//        "const highp vec4 kRGBToYPrime = vec4 (0.299, 0.587, 0.114, 0.0);\n" +
//        "const highp vec4 kRGBToI = vec4 (0.595716, -0.274453, -0.321263, 0.0);\n" +
//        "const highp vec4 kRGBToQ = vec4 (0.211456, -0.522591, 0.31135, 0.0);\n" +
//        "\n" +
//        "const highp vec4 kYIQToR = vec4 (1.0, 0.9563, 0.6210, 0.0);\n" +
//        "const highp vec4 kYIQToG = vec4 (1.0, -0.2721, -0.6474, 0.0);\n" +
//        "const highp vec4 kYIQToB = vec4 (1.0, -1.1070, 1.7046, 0.0);\n" +
//        "\n" +
//
//        " void main()\n" +
//        " {\n" +
////        "     lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
////        "     vec4 brightnessColor = vec4((textureColor.rgb + vec3(brightness)), textureColor.w);\n" +
////
////        "    highp vec4 color = brightnessColor;\n" +
//        "    highp vec4 color = texture2D(inputImageTexture, textureCoordinate);\n" +
//
//        "    // Convert to YIQ\n" +
//        "    highp float YPrime = dot (color, kRGBToYPrime);\n" +
//        "    highp float I = dot (color, kRGBToI);\n" +
//        "    highp float Q = dot (color, kRGBToQ);\n" +
//        "\n" +
//        "    // Calculate the hue and chroma\n" +
//        "    highp float hue = atan (Q, I);\n" +
//        "    highp float chroma = sqrt (I * I + Q * Q);\n" +
//        "\n" +
//        "    // Make the user's adjustments\n" +
//        "    hue += (hueAdjust); //why negative rotation?\n" +
//        "\n" +
//        "    // Convert back to YIQ\n" +
//        "    Q = chroma * sin (hue);\n" +
//        "    I = chroma * cos (hue);\n" +
//        "\n" +
//        "    // Convert back to RGB\n" +
//        "    highp vec4 yIQ = vec4 (YPrime, I, Q, 0.0);\n" +
//        "    color.r = dot (yIQ, kYIQToR);\n" +
//        "    color.g = dot (yIQ, kYIQToG);\n" +
//        "    color.b = dot (yIQ, kYIQToB);\n" +
//        "\n" +
//        "    gl_FragColor = color;\n" +
//        " }"
//
//const val SPLIT_TONE_TEST_FRAGMENT_SHADER = "" +
//        "varying highp vec2 textureCoordinate;\n" +
//        "uniform sampler2D inputImageTexture;\n" +
//        "uniform mediump float hueAdjust;\n" +
//        "uniform lowp float saturation;\n" +
//
//        "vec3 convertRGB2HSV( vec3 rgbcolor )\n" +
//        "{\n" +
//        "   float h, s, v;\n" +
//        "   float r = rgbcolor.r;\n" +
//        "   float g = rgbcolor.g;\n" +
//        "   float b = rgbcolor.b;\n" +
//        "   float maxval = max( r, max( g, b )); \n" +
//
//        "   v = maxval;\n" +
//        "   float minval = min( r, min( g, b ));\n" +
//        "   if (maxval==0.) s = 0.0;\n" +
//        "   else s = (maxval - minval) / maxval;\n" +
//        "   if (s == 0.) h = 0.0; \n" +
//        "   else\n" +
//        "   {\n" +
//        "       float delta = maxval - minval;\n" +
//        "       if (r==maxval) h =(g - b) / delta;\n" +
//        "       else\n" +
//        "           if (g == maxval) h = 2.0 + (b - r)/delta;\n" +
//        "           else\n" +
//        "               if (b == maxval) h = 4.0 + (r - g)/delta;\n" +
//        "    h *= 60.0;\n" +
//        "   if (h < 0.0) h += 360.0;\n" +
//        "   }\n" +
//
//        "   return vec3( h, s, v );\n" +
//        "}\n" +
//
//        "vec3 convertHSV2RGB( vec3 hsvcolor )\n" +
//        "{\n" +
//        "   float h = hsvcolor.x;\n" +
//        "   float s = hsvcolor.y;\n" +
//        "   float v = hsvcolor.z;\n" +
//        "   if (s == 0.0) return vec3(v,v,v); \n" +
//
//        "   else // chromatic case\n" +
//        "   {\n" +
//        "    if (h > 360.0) h = 360.0; // h must be in [0, 360)\n" +
//        "    if (h < 0.0) h = 0.0; // h must be in [0, 360)\n" +
//        "    h /= 60.;\n" +
//        "    int k = int(h);\n" +
//        "    float f = h - float(k);\n" +
//        "    float p = v * (1.0 - s);\n" +
//        "    float q = v * (1.0 - (s * f));\n" +
//        "    float t = v * (1.0 - (s * (1.0 - f)));" +
//        "    if (k == 0) return vec3(v,t,p);\n" +
//        "    if (k == 1) return vec3(q,v,p);\n" +
//        "    if (k == 2) return vec3(p,v,t);\n" +
//        "    if (k == 3) return vec3(p,q,v);\n" +
//        "    if (k == 4) return vec3(t,p,v);\n" +
//        "    if (k == 5) return vec3(v,p,q);\n" +
//        "   }\n" +
//        "}\n" +
//
//        "void main()\n" +
//        "{\n" +
//        "   vec3 irgb = texture2D(inputImageTexture, textureCoordinate).rgb;\n" +
//        "   vec3 ihsv = convertRGB2HSV(irgb);\n" +
//        "   if(ihsv.z > 0.8 )\n" +
//        "   {\n" +
//        "       float saturation = saturation / 2.0;\n" +
//        "       ihsv.x = hueAdjust;\n" +
//        "       ihsv.y = saturation;\n" +
//        "   }\n" +
////        "   if (ihsv.x > 360.) ihsv.x -= 360.; //add to hue\n" +
////        "   if (ihsv.x < 0.) \t\t ihsv.x += 360.; //add to hue\n" +
//        "   irgb = convertHSV2RGB( ihsv );\n" +
//        "   gl_FragColor = vec4( irgb, 1. );\n" +
//        "}"