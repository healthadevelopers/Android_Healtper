package com.umc.healthper.util

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.widget.TextView
import com.umc.healthper.ui.MainActivity

class GlobVar: Application() {
    lateinit var mainContext: Context
    lateinit var mainActivity: MainActivity
    lateinit var today: TextView
    var setRestTime : Int = 0
}