package com.ice.restring.activity

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.ice.restring.R
import com.ice.restring.Restring

class TestActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(Restring.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_AppCompat)
        setContentView(R.layout.test_layout)
    }
}
