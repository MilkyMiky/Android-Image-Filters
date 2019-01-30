package com.example.opengl.image.gpuimage


import android.graphics.PointF
import com.example.opengl.image.gpuimage.filters.GrainFilter
import jp.co.cyberagent.android.gpuimage.filter.*
import java.util.ArrayList

class FilterService {

    class ImageSize(
        var width : Float = 0.0f,
        var height  : Float = 0.0f
    )

    fun getFilter(filter: FilterType, progress: Int, imageSize: ImageSize = ImageSize(), points: ArrayList<PointF> = ArrayList()): GPUImageFilter {
        return when (filter) {
            FilterType.EXPOSURE -> GPUImageExposureFilter(scaleProgress(progress, -10.0f, 10.0f))
            FilterType.HIGHLIGHTS -> GPUImageHighlightShadowFilter(0.0f, scaleProgress(progress, 0.0f, 1.0f))
            FilterType.SHADOWS -> GPUImageHighlightShadowFilter(scaleProgress(progress, 0.0f, 1.0f), 1.0f)
            FilterType.CONTRAST -> GPUImageContrastFilter(scaleProgress(progress, 0.0f, 2.0f))
            FilterType.SATURATION -> GPUImageSaturationFilter(scaleProgress(progress, 0.0f, 2.0f))
            FilterType.TEMPERATURE -> GPUImageWhiteBalanceFilter(scaleTemperatureValues(progress), 0.0f)
            FilterType.TINT -> GPUImageWhiteBalanceFilter(5000.0f, scaleTintValues(progress))
            FilterType.CLARITY -> GPUImageSharpenFilter(scaleProgress(progress, -0.1f, 0.2f))
            FilterType.GRAIN -> GrainFilter(imageSize.width, imageSize.height, scaleProgress(progress, 0.0f, 2.0f) / 2,
               scaleProgress(progress, 0.0f, 2.0f))
            FilterType.WHITES -> getWhitesFilter(progress)
            FilterType.BLACKS -> getBlacksFilter(progress)
            FilterType.TONE_CURVE -> getToneCurveFilter(points)

        }
    }

    private fun getToneCurveFilter(points: ArrayList<PointF>): GPUImageFilter {
        val filter = GPUImageToneCurveFilter()
        if (points.first().x > 0)
            points.add(0, PointF(0.0f, points.first().y))
        if (points.last().x < 1)
            points.add(PointF(1.0f, points.last().y))
        filter.setRgbCompositeControlPoints(points.toTypedArray())
        return filter
    }


    private fun getWhitesFilter(progress: Int): GPUImageFilter {
        val filter = GPUImageToneCurveFilter()
        val scaledPercentage = progress / 100.0f
        val delta = (scaledPercentage - 0.5f) * 0.6f

        val lastPoint = if (delta > 0) PointF(1.0f - delta, 1.0f) else PointF(1.0f, 1.0f + delta)

        val points = arrayListOf(
            PointF(0.0f, 0.0f),
            PointF(0.1f, 0.1f),
            PointF(0.11f, 0.11f),
            PointF(0.12f, 0.12f),
            lastPoint
        )

        filter.setRgbCompositeControlPoints(points.toTypedArray())
        return filter
    }

    private fun getBlacksFilter(progress: Int): GPUImageFilter {
        val filter = GPUImageToneCurveFilter()
        val scaledPercentage = progress / 100.0f
        val delta = (scaledPercentage - 0.5f) / 4

        val points = arrayListOf(
            PointF(0.0f, 0.0f),
            PointF(0.125f, 0.125f + delta),
            PointF(0.998f, 0.998f),
            PointF(1.0f, 1.0f)
        )

        filter.setRgbCompositeControlPoints(points.toTypedArray())
        return filter
    }

    private fun scaleProgress(percentage: Int, start: Float, end: Float): Float =
        (end - start) * percentage / 100.0f + start

    private fun scaleTemperatureValues(percentage: Int): Float {
        val scaledPercentage = percentage / 100.0f
        return if (scaledPercentage < 0.5) 4000 + scaledPercentage * 2000
        else 40000 * scaledPercentage * scaledPercentage * scaledPercentage
    }

    private fun scaleTintValues(percentage: Int): Float {
        val scaledPercentage = percentage / 100.0f
        return -200 + scaledPercentage * 400
    }
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
}
