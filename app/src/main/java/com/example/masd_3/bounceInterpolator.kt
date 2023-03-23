package com.example.masd_3

import android.view.animation.Interpolator


internal class bounceInterpolator(amplitude: Double, frequency: Double) :
    Interpolator {
    private var mAmplitude = 1.0
    private var mFrequency = 10.0
    override fun getInterpolation(input: Float): Float {
        return (-1 * Math.pow(Math.E, -input / mAmplitude) *
                Math.cos(mFrequency * input) + 1).toFloat()
    }

    init {
        mAmplitude = amplitude
        mFrequency = frequency
    }

}