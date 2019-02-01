package com.example.opengl.image.gpuimage.filters

import android.graphics.PointF
import jp.co.cyberagent.android.gpuimage.filter.GPUImageToneCurveFilter

class WhitesFilter(progress: Int) : GPUImageToneCurveFilter() {

    init {
        scaleProgress(progress)
    }

    private fun scaleProgress(progress: Int) {
        val scaledPercentage = progress / 100.0f
        val delta = (scaledPercentage - 0.5f) * 0.6f

        val points = arrayListOf(
            PointF(0.0f, 0.0f),
            PointF(0.1f, 0.1f),
            PointF(0.11f, 0.11f),
            PointF(0.12f, 0.12f),
            if (delta > 0) PointF(1.0f - delta, 1.0f) else PointF(1.0f, 1.0f + delta)
        )

        super.setRgbCompositeControlPoints(points.toTypedArray())
    }
}