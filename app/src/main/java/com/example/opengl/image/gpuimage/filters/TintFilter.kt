package com.example.opengl.image.gpuimage.filters

import jp.co.cyberagent.android.gpuimage.filter.GPUImageWhiteBalanceFilter

class TintFilter(progress: Int) : GPUImageWhiteBalanceFilter() {

    init {
        scaleProgress(progress)
    }

    private fun scaleProgress(progress: Int) {
        val scaledPercentage = progress / 100.0f
        val tint = -200 + scaledPercentage * 400
        super.setTemperature(5000.0f)
        super.setTint(tint)
    }

}