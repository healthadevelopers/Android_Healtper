package com.umc.healthper.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.umc.healthper.data.remote.AuthService
import com.umc.healthper.data.remote.GetDayDetailFirst
import com.umc.healthper.databinding.FragmentMainBinding
import com.umc.healthper.ui.main.adapter.MainRVAdapter
import com.umc.healthper.ui.main.adapter.WorkReadyListAdapter
import com.umc.healthper.util.VarUtil
import java.util.*
import kotlin.collections.ArrayList

class MainFragment: Fragment(), DetailFirstView {

    lateinit var binding: FragmentMainBinding
    lateinit var adapter: MainRVAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMainBinding.inflate(inflater, container, false)

        adapter = MainRVAdapter()
        binding.mainRv.adapter = adapter
        return binding.root
    }

    override fun onResume() {
        super.onResume()

    }


    override fun onDetailFirstGetSuccess(data: ArrayList<GetDayDetailFirst>) {
        VarUtil.glob.detailFirstList = data

        val siz = data.size
        val originSiz = VarUtil.glob.mainCompList.size - 3
        if (siz != originSiz) {
            if (siz > originSiz) {
                for (i in originSiz until siz) {
                    VarUtil.glob.mainCompList.add(2, 3)
                }


            }
            else {
                for (i in siz until originSiz) {
                    VarUtil.glob.mainCompList.removeAt(2)
                }
            }
        }

        adapter.notifyDataSetChanged()
    }

    override fun onDetailFirstGetFailure() {
        TODO("Not yet implemented")
    }
}