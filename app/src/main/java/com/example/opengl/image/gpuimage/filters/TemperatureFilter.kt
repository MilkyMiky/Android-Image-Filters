package com.example.opengl.image.gpuimage.filters

import jp.co.cyberagent.android.gpuimage.filter.GPUImageWhiteBalanceFilter

class TemperatureFilter(progress: Int) : GPUImageWhiteBalanceFilter() {

    init {
        scaleProgress(progress)
    }

    private fun scaleProgress(progress: Int) {
        val scaledPercentage = progress / 100.0f
        val temp =
            if (scaledPercentage < 0.5) 4000 + scaledPercentage * 2000
            else 40000 * scaledPercentage * scaledPercentage * scaledPercentage
        super.setTemperature(temp)
        super.setTint(0.0f)
    }

}