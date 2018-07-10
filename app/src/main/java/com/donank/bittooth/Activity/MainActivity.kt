package com.donank.bittooth.Activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.donank.bittooth.Fragments.Dashboard
import com.donank.bittooth.R
import com.donank.bittooth.Utility.showFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showFragment(
                Fragment.instantiate(
                        this,
                        Dashboard::class.java.name
                ),
                addToBackStack = false
        )
    }

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        fragment.showFragment(container = R.id.fragment_container,
                fragmentManager = supportFragmentManager,
                addToBackStack = addToBackStack)
    }

    override fun onStart(){
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }
}