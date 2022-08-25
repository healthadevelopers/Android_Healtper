package com.umc.healthper.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import com.umc.healthper.data.remote.AuthService
import com.umc.healthper.databinding.ActivityLoginBinding
import com.umc.healthper.ui.MainActivity
import com.umc.healthper.util.VarUtil
import com.umc.healthper.util.VarUtil.Companion.glob
import com.umc.healthper.util.getAutoLogin
import com.umc.healthper.util.saveAutoLogin
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class LoginActivity : AppCompatActivity() {
    lateinit var binding : ActivityLoginBinding

    override fun onDestroy() {
        super.onDestroy()
        Log.d("loginActivity", "destroy")
        ActivityCompat.finishAffinity(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("LoginActivity", "create")


        //세션키 확인

//        var packageInfo: PackageInfo? = null
//        try{
//            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
//        } catch (e: PackageManager.NameNotFoundException) {
//            e.printStackTrace()
//        }
//        if (packageInfo == null) {
//            Log.d("hashKey", "null")
//        }
//        packageInfo?.signatures?.forEach {
//            try {
//                val md = MessageDigest.getInstance("SHA")
//                md.update(it.toByteArray())
//                Log.d("hashKey", Base64.encodeToString(md.digest(), Base64.DEFAULT))
//            } catch (e: NoSuchAlgorithmException) {
//                e.printStackTrace()
//                Log.e("KeyHash", "Unable to get MessageDigest. signature=$it", e)
//            }
//        }


        val spf = this.getSharedPreferences("isAuto", MODE_PRIVATE)
        var autoLogin = getAutoLogin()
        setAutoLogin(autoLogin)

        binding.loginAutoTv.setOnClickListener {
            Log.d("spf", spf!!.getBoolean("isAuto", false).toString())
            autoLogin = getAutoLogin()

            if (autoLogin) {
                saveAutoLogin(false)
                binding.loginAutoTv.text = "auto login : no"
            }
            else {
                saveAutoLogin(true)
                binding.loginAutoTv.text = "auto login : yes"
            }
        }

//        if (autoLogin) {
//            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
//                if (error != null) {
//                    Toast.makeText(this, "토큰 정보 보기 실패", Toast.LENGTH_SHORT).show()
//                }
//                else if (tokenInfo != null) {
//                    Toast.makeText(this, "토큰 정보 보기 성공", Toast.LENGTH_SHORT).show()
//                    val intent = Intent(this, MainActivity::class.java)
//                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
//                    Log.d("ID tokeninfo", tokenInfo.id.toString())
//                    // api 들어갈 자리
//                    val authService = AuthService()
//                    authService.login(tokenInfo.id.toString())
////                    authService.isLogin()
//                    finish()
//                }
//            }
//        }

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.d("token", error.toString())
                when {
                    error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                        Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                        Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                        Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                        Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                        Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                        Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.ServerError.toString() -> {
                        Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                        Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                    }
                    else -> { // Unknown
                        Toast.makeText(this, "기타 에러", Toast.LENGTH_SHORT).show()
                        Log.d("error", error.toString())
                    }
                }
            }
            else if (token != null) {
                Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                UserApiClient.instance.accessTokenInfo{ tokenInfo, error ->
                    if (error != null) {
                        Toast.makeText(this, "토큰 정보 보기 실패", Toast.LENGTH_SHORT).show()
                    }
                    else if (tokenInfo != null) {
                        Toast.makeText(this, "토큰 정보 보기 성공", Toast.LENGTH_SHORT).show()
                        Log.d("ID tokeninfo no auto", tokenInfo.id.toString())
                        // api 들어갈 자리
                        val authService = AuthService()
                        authService.login(tokenInfo.id.toString())
//                        authService.isLogin()
                    }
                }
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finish()
            }
        }

        val kakao_login_button = binding.kakaoLoginTv // 로그인 버튼

        kakao_login_button.setOnClickListener {
            if(LoginClient.instance.isKakaoTalkLoginAvailable(this)){
                LoginClient.instance.loginWithKakaoTalk(this, callback = callback)
            }else{
                LoginClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }


        //임시
//        binding.googleLoginTv.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
//            finish()
//        }
    }

    @SuppressLint("SetTextI18n")
    private fun setAutoLogin(autoLogin : Boolean) {
        Log.d("spf", autoLogin.toString())

        if (autoLogin)
            binding.loginAutoTv.text = "auto login : yes"
        else
            binding.loginAutoTv.text = "auto login : no"
    }
}
//package com.umc.healthper.ui.login
//
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.content.SharedPreferences
//import android.os.Bundle
//import android.util.Log
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.kakao.sdk.auth.LoginClient
//import com.kakao.sdk.auth.model.OAuthToken
//import com.kakao.sdk.common.model.AuthErrorCause
//import com.kakao.sdk.user.UserApiClient
//import com.umc.healthper.data.remote.AuthService
//import com.umc.healthper.databinding.ActivityLoginBinding
//import com.umc.healthper.ui.MainActivity
//import com.umc.healthper.util.VarUtil
//import com.umc.healthper.util.VarUtil.Companion.glob
//import com.umc.healthper.util.getAutoLogin
//import com.umc.healthper.util.saveAutoLogin
//
//class LoginActivity : AppCompatActivity() {
//    lateinit var binding : ActivityLoginBinding
//
//    override fun onResume() {
//        super.onResume()
//        login()
//    }
//
//    @SuppressLint("SetTextI18n")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityLoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val spf = this.getSharedPreferences("isAuto", MODE_PRIVATE)
//        var autoLogin = getAutoLogin()
//        setAutoLogin(autoLogin)
//
//        binding.loginAutoTv.setOnClickListener {
//            Log.d("spf", spf!!.getBoolean("isAuto", false).toString())
//            autoLogin = getAutoLogin()
//
//            if (autoLogin) {
//                saveAutoLogin(false)
//                binding.loginAutoTv.text = "auto login : no"
//            }
//            else {
//                saveAutoLogin(true)
//                binding.loginAutoTv.text = "auto login : yes"
//            }
//        }
//
//        //임시
//        binding.googleLoginTv.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
//            finish()
//        }
//    }
//
//    fun login(){
////        if (getAutoLogin()) {
////            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
////                if (error != null) {
////                    Toast.makeText(this, "토큰 정보 보기 실패", Toast.LENGTH_SHORT).show()
////                } else if (tokenInfo != null) {
////                    Toast.makeText(this, "토큰 정보 보기 성공", Toast.LENGTH_SHORT).show()
////                    val intent = Intent(this, MainActivity::class.java)
////                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
////                    Log.d("ID tokeninfo", tokenInfo.id.toString())
////                    // api 들어갈 자리
////                    val authService = AuthService()
////                    authService.login(tokenInfo.id.toString())
//////                    authService.isLogin()
////                    finish()
////                }
////            }
////        }
//
//        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
//            if (error != null) {
//                when {
//                    error.toString() == AuthErrorCause.AccessDenied.toString() -> {
//                        Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
//                    }
//                    error.toString() == AuthErrorCause.InvalidClient.toString() -> {
//                        Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
//                    }
//                    error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
//                        Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                    error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
//                        Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
//                    }
//                    error.toString() == AuthErrorCause.InvalidScope.toString() -> {
//                        Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
//                    }
//                    error.toString() == AuthErrorCause.Misconfigured.toString() -> {
//                        Toast.makeText(
//                            this,
//                            "설정이 올바르지 않음(android key hash)",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                    error.toString() == AuthErrorCause.ServerError.toString() -> {
//                        Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
//                    }
//                    error.toString() == AuthErrorCause.Unauthorized.toString() -> {
//                        Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
//                    }
//                    else -> { // Unknown
//                        Toast.makeText(this, "기타 에러", Toast.LENGTH_SHORT).show()
//                        Log.d("error", error.toString())
//                    }
//                }
//            } else if (token != null) {
//                Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
//                val intent = Intent(this, MainActivity::class.java)
////                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
//                UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
//                    if (error != null) {
//                        Toast.makeText(this, "토큰 정보 보기 실패", Toast.LENGTH_SHORT).show()
//                    } else if (tokenInfo != null) {
//                        Toast.makeText(this, "토큰 정보 보기 성공", Toast.LENGTH_SHORT).show()
//                        Log.d("ID tokeninfo no auto", tokenInfo.id.toString())
//                        // api 들어갈 자리
//                        val authService = AuthService()
//                        authService.login(tokenInfo.id.toString())
////                        authService.isLogin()
//                    }
//                }
//                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
//                finish()
//            }
//        }
//
//        val kakao_login_button = binding.kakaoLoginTv // 로그인 버튼
//
//        kakao_login_button.setOnClickListener {
//            if(LoginClient.instance.isKakaoTalkLoginAvailable(this)){
//            LoginClient.instance.loginWithKakaoTalk(this, callback = callback)
//            }else{
//                LoginClient.instance.loginWithKakaoAccount(this, callback = callback)
//            }
//        }
//    }
//
//    @SuppressLint("SetTextI18n")
//    private fun setAutoLogin(autoLogin : Boolean) {
//        Log.d("spf", autoLogin.toString())
//
//        if (autoLogin)
//            binding.loginAutoTv.text = "auto login : yes"
//        else
//            binding.loginAutoTv.text = "auto login : no"
//    }
//}