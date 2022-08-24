package com.umc.healthper.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import com.umc.healthper.R
import com.umc.healthper.data.entity.Work
import com.umc.healthper.data.entity.WorkPart
import com.umc.healthper.data.local.LocalDB
import com.umc.healthper.data.remote.AuthService
import com.umc.healthper.data.remote.GetDayDetailFirst
import com.umc.healthper.ui.login.LoginActivity
import com.umc.healthper.ui.main.view.DetailFirstView
import java.io.InputStream
import com.umc.healthper.util.VarUtil
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class SplashActivity : AppCompatActivity(), DetailFirstView {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        initDb(applicationContext)

        val conn = AuthService()
        conn.dayInfoData = this
        val now = Calendar.getInstance()
        val y = now.get(Calendar.YEAR)
        val m = now.get(Calendar.MONTH) + 1
        val d = now.get(Calendar.DATE)
        conn.dayInfo("$y-$m-$d")

        Handler().postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        },DURATION)



    }
    companion object {
        private const val DURATION : Long = 1500
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
    fun initDb(context: Context) {
        val db = LocalDB.getInstance(context)!!
        val manager: AssetManager = context.assets
        if (db.WorkPartDao().getFirst() == null) {
            val partData = manager.open("workPartList.txt")

            partData.bufferedReader().readLines().forEach {
                val dataList = it.split(" ")
                val id = dataList[0].toInt()
                val partEng = dataList[1]
                val partKor = dataList[2]
                val partColor = dataList[3]

                val partData = WorkPart(id, partKor, partColor)
                db.WorkPartDao().insert(partData)
            }
        }
        if (db.WorkDao().getFirst() == null) {
            val input: InputStream = manager.open("workList.txt")
            var part = ""
            var partId = 0
            var isPart = false
            input.bufferedReader().readLines().forEach {
                val work = it
                if (isPart) {
                    part = work
                    partId = db.WorkPartDao().getWorkPartIdbyPartName(part)
                    isPart = false
                }
                else if (work == "-") isPart = true
                else if (!isPart) {
                    var inp = Work(
                        0,work, partId, 0, 0, 0
                    )
                    db.WorkDao().insert(inp)
//                        CoroutineScope(Dispatchers.IO).launch {
//
//                        }
                }
            }
        }

    }

    override fun onDetailFirstGetSuccess(data: ArrayList<GetDayDetailFirst>) {
        VarUtil.glob.detailFirstList = data
    }

    override fun onDetailFirstGetFailure() {
    }
}