package com.umc.healthper.util

import android.app.Application
import android.content.Context
import android.graphics.Point
import android.widget.TextView
import com.umc.healthper.data.entity.ExerciseInfo
import com.umc.healthper.data.entity.TotalData
import com.umc.healthper.data.entity.Work
import com.umc.healthper.data.entity.WorkRecord
import com.umc.healthper.data.remote.GetDayDetailFirst
import com.umc.healthper.data.remote.GetDayDetailSecond
import com.umc.healthper.ui.MainActivity
import com.umc.healthper.ui.main.adapter.WorkReadyListAdapter
import com.umc.healthper.ui.main.view.MainFragment
import com.umc.healthper.ui.mypage.adapter.ShowFavWorkRVAdapter

class GlobVar: Application() {
    lateinit var mainContext: Context
    lateinit var mainActivity: MainActivity
    lateinit var mainFragment: MainFragment
    lateinit var today: TextView
    lateinit var size: Point
    var restMinutes : Int = 60

    var selectedPart = ArrayList<String>()
    var unselectedPart = ArrayList<String>()
    var work : ArrayList<WorkRecord> = arrayListOf()
    var totalData : TotalData = TotalData("", ArrayList(), ExerciseInfo(0, 0))
    var setMain : Boolean = false
    var isWorkTime: Boolean = true // false -> partTime

    lateinit var workReadyAdapter: WorkReadyListAdapter
    var currentPart: String = ""
    var currentWork: String = ""

    //사용자 즐겨찾기 페이지 데이터
    var favWorkList = ArrayList<Work>()
    lateinit var favPageWorkListAdapter: ShowFavWorkRVAdapter


    //달력각날짜의상세데이터
    var detailFirstList = ArrayList<GetDayDetailFirst>()
    var mainCompList =(1..4).toMutableList()

    //현재 열람중인 운동정보
    var recordId: Int = 0
    var recordList = ArrayList<GetDayDetailSecond>()
}