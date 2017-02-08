package com.smilehacker.autopunch

import android.os.Bundle
import com.smilehacker.Megatron.HostActivity

class MainActivity : HostActivity() {

    override fun getContainerID(): Int {
        return R.id.container
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            init()
        }
    }

    private fun init() {
        startFragment(MainFragment::class.java)
    }
}
