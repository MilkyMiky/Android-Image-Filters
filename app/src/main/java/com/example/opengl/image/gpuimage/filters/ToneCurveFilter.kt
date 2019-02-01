package com.example.opengl.image.gpuimage.filters

import android.graphics.PointF
import jp.co.cyberagent.android.gpuimage.filter.GPUImageToneCurveFilter
import java.util.ArrayList

class ToneCurveFilter(points: ArrayList<PointF>) : GPUImageToneCurveFilter() {

    init {
        handlePoints(points)
    }

    private fun handlePoints(points: ArrayList<PointF>) {
        if (points.first().x > 0)
            points.add(0, PointF(0.0f, points.first().y))
        if (points.last().x < 1)
            points.add(PointF(1.0f, points.last().y))
        super.setRgbCompositeControlPoints(points.toTypedArray())
    }
}