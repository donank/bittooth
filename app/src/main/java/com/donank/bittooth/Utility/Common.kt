package com.donank.bittooth.Utility

import android.graphics.Color
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.View
import android.widget.TextView

fun Fragment.showFragment(container: Int, fragmentManager: FragmentManager,
                          addToBackStack: Boolean = false) {
    val fm = fragmentManager.beginTransaction()
    fm.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out)
    fm.replace(container, this, this.javaClass.simpleName)
    if (addToBackStack) fm.addToBackStack(null)
    fm.commit()
}

fun showInSnack(view: View, text: String, duration: Int = Snackbar.LENGTH_LONG) {
    val snackbar = Snackbar.make(view, text, duration)
    val sbView = snackbar.view
    val textView = sbView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
    textView.setTextColor(Color.WHITE)
    textView.textSize = 12f
    snackbar.setAction("OK") { snackbar.dismiss() }
    snackbar.show()
}