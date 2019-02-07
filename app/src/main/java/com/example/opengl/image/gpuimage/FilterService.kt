package com.example.opengl.image.gpuimage


import android.graphics.Color
import android.graphics.PointF
import com.example.opengl.image.gpuimage.filters.*
import jp.co.cyberagent.android.gpuimage.filter.*
import java.util.ArrayList


const val EXPOSURE_SCALE_START = -10.0f
const val EXPOSURE_SCALE_END = 10.0f

const val HIGHLIGHTS_SCALE_START = 0.0f
const val HIGHLIGHTS_SCALE_END = 1.0f

const val SHADOWS_SCALE_START = 0.0f
const val SHADOWS_SCALE_END = 1.0f

const val CONTRAST_SCALE_START = 0.0f
const val CONTRAST_SCALE_END = 2.0f

const val SATURATION_SCALE_START = 0.0f
const val SATURATION_SCALE_END = 2.0f

const val CLARITY_SCALE_START = -0.1f
const val CLARITY_SCALE_END = 0.2f

const val GRAIN_SCALE_START = 0.0f
const val GRAIN_SCALE_END = 2.0f

class FilterService {

    fun getFilter(
        filter: FilterType,
        progress: Int = 0,
        imageSize: ImageSize = ImageSize(),
        points: ArrayList<PointF> = ArrayList(),
        HSV: HSV = HSV(),
        rgb  : RGB = RGB(),
        rgb2 : RGB = RGB(),
        balance : Float = 0.0f
    ): GPUImageFilter {
        return when (filter) {
            FilterType.EXPOSURE -> GPUImageExposureFilter(
                scaleProgress(
                    progress,
                    EXPOSURE_SCALE_START,
                    EXPOSURE_SCALE_END
                )
            )
            FilterType.HIGHLIGHTS -> HightlightsFilter(
                scaleProgress(progress, HIGHLIGHTS_SCALE_START, HIGHLIGHTS_SCALE_END)
            )
//GPUImageColorBlendFilter
            FilterType.SHADOWS -> GPUImageHighlightShadowFilter(
                scaleProgress(
                    progress,
                    SHADOWS_SCALE_START,
                    SHADOWS_SCALE_END
                ), 1.0f
            )
            FilterType.CONTRAST -> GPUImageContrastFilter(
                scaleProgress(
                    progress,
                    CONTRAST_SCALE_START,
                    CONTRAST_SCALE_END
                )
            )
            FilterType.SATURATION -> GPUImageSaturationFilter(
                scaleProgress(
                    progress,
                    SATURATION_SCALE_START,
                    SATURATION_SCALE_END
                )
            )
            FilterType.TEMPERATURE -> TemperatureFilter(progress)
            FilterType.TINT -> TintFilter(progress)
            FilterType.CLARITY -> GPUImageSharpenFilter(scaleProgress(progress, CLARITY_SCALE_START, CLARITY_SCALE_END))
            FilterType.GRAIN -> GrainFilter(
                imageSize,
                scaleProgress(progress, GRAIN_SCALE_START, GRAIN_SCALE_END) / 2,
                scaleProgress(progress, GRAIN_SCALE_START, GRAIN_SCALE_END)
            )
            FilterType.WHITES -> WhitesFilter(progress)
            FilterType.BLACKS -> BlacksFilter(progress)
            FilterType.TONE_CURVE -> ToneCurveFilter(points)
            FilterType.SPLIT_TONING -> SplitToningFilter(rgb, rgb2, balance)
        }
    }

    private fun scaleProgress(percentage: Int, start: Float, end: Float): Float =
        (end - start) * percentage / 100.0f + start

    private fun mockPoints() =
        arrayListOf(
            PointF(0.0090758824112391716f, 0.0f),
            PointF(0.19306930693069307f, 0.099835008677869763f),
            PointF(0.44719469429242731f, 0.39191421659866177f),
            PointF(0.67821782178217827f, 0.68811881188118806f),
            PointF(0.87211219863136213f, 0.86798681126962796f),
            PointF(0.98102308972047103f, 0.92244225681418235f)
        )
}


fun hsvToRgb(hue: Float, saturation: Float, value: Float): RGB {
    val outputColor = Color.HSVToColor(floatArrayOf(hue, saturation, value))

//    Log.d("log", "COLOR r=${Color.red(outputColor)}, g=${Color.green(outputColor)}, b=${Color.blue(outputColor)}")
    return RGB(
        Color.red(outputColor).toFloat(),
        Color.green(outputColor).toFloat(),
        Color.blue(outputColor).toFloat()
    )


}

enum class FilterType {
    EXPOSURE,
    HIGHLIGHTS,
    SHADOWS,
    CONTRAST,
    TEMPERATURE,
    TINT,
    SATURATION,
    CLARITY,
    GRAIN,
    WHITES,
    BLACKS,
    TONE_CURVE,
    SPLIT_TONING
}
