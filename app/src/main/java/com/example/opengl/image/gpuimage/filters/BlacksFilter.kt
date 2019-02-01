package com.example.opengl.image.gpuimage.filters

import android.graphics.PointF
import jp.co.cyberagent.android.gpuimage.filter.GPUImageToneCurveFilter

class BlacksFilter(progress: Int) : GPUImageToneCurveFilter()  {

    init {
        scaleProgress(progress)
    }

    private fun scaleProgress(progress: Int) {
        val scaledPercentage = progress / 100.0f
        val delta = (scaledPercentage - 0.5f) / 4

        val points = arrayListOf(
            PointF(0.0f, 0.0f),
            PointF(0.125f, 0.125f + delta),
            PointF(0.998f, 0.998f),
            PointF(1.0f, 1.0f)
        )

        super.setRgbCompositeControlPoints(points.toTypedArray())
    }
}