package com.inspirationdriven.fbphotochanger.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.inspirationdriven.fbphotochanger.R
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private lateinit var fbCallbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val token = AccessToken.getCurrentAccessToken()

        if (token != null && !token.isExpired){
            showMainActivity()
            return
        }

        fbCallbackManager = CallbackManager.Factory.create()
        with(login_button){
            setReadPermissions("user_photos","public_profile", "user_posts")
            registerCallback(fbCallbackManager, object: FacebookCallback<LoginResult>{
                override fun onSuccess(result: LoginResult) {
                    login_button.visibility = View.GONE
                    showMainActivity()
                }

                override fun onCancel() {
                }

                override fun onError(error: FacebookException) {
                    error.printStackTrace()
                }

            })
        }
    }

    private fun showMainActivity(){
        MainActivity.show(this)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        fbCallbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
