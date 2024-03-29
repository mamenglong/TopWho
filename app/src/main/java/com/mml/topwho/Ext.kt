package com.mml.topwho

import android.app.Activity
import android.os.Looper
import android.widget.Toast
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView


fun log(tag:String?="Log",msg: String){
     if (BuildConfig.DEBUG){
         Log.i(tag,msg)
     }
 }

fun showToast(msg:String)= run {
    val looper=Looper.getMainLooper()
    if (looper== Looper.myLooper()){
            Toast.makeText(TopWhoApplication.application,msg,Toast.LENGTH_SHORT).show()
    }
}
inline fun <reified T : Activity> Context.startActivity() {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}
fun Context.showDebugToast(msg:String)= run {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
}
fun View.extSetVisibility(visible: Boolean) = if (visible) {
    this.visibility = View.VISIBLE
} else {
    this.visibility = View.INVISIBLE
}

fun View.extGetVisibility() = when (visibility) {
    View.VISIBLE -> {
        true
    }
    else -> {
        false
    }
}

//扩展函数，view隐藏
fun View.gone() {
    visibility = View.GONE
}

//扩展函数，view显示
fun View.visible() {
    visibility = View.VISIBLE
}


/**
 * 设置颜色直接使用colors.xml中定义的颜色即可
 */
fun TextView.setColor(resId: Int) {
    this.setTextColor(resources.getColor(resId))
}

fun View.setDrawableLeft(resId: Int) {
    when (this) {
        is Button -> {
            var drawable = this.context.resources.getDrawable(resId)
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            this.setCompoundDrawables(drawable, null, null, null)
        }
        is TextView -> {
            var drawable = this.context.resources.getDrawable(resId)
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            this.setCompoundDrawables(drawable, null, null, null)
        }
        else -> {
            throw Exception("this method does not support you .")
        }
    }
}

fun TextView.setDrawableRight(resId: Int) {
    when (this) {
        is Button -> {
            var drawable = this.context.resources.getDrawable(resId)
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            this.setCompoundDrawables(null, null, drawable, null)
        }
        is TextView -> {
            var drawable = this.context.resources.getDrawable(resId)
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            this.setCompoundDrawables(null, null, drawable, null)
        }
        else -> {
            throw Exception("this method does not support you .")
        }
    }

}

fun TextView.setDrawableTop(resId: Int) {
    var drawable = this.context.resources.getDrawable(resId)
    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    this.setCompoundDrawables(null, drawable, null, null)
}

fun TextView.setDrawableBottom(resId: Int) {
    var drawable = this.context.resources.getDrawable(resId)
    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    this.setCompoundDrawables(null, null, null, drawable)
}
