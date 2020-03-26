package com.example.androidxdogs.util

import android.content.Context
import android.media.Image
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.androidxdogs.R

val PERMISSION_SEND_SMS = 123

fun getProgressDrawable(context: Context): CircularProgressDrawable{
     return CircularProgressDrawable(context).apply {
         strokeWidth = 10f
         centerRadius = 50f
         start()
     }
}

fun ImageView.loadImage(uri: String?,progressDrawable: CircularProgressDrawable){
    val  option = RequestOptions()
        .placeholder(progressDrawable)
        .error(R.mipmap.ic_launcher_round)
    Glide.with(context)
        .setDefaultRequestOptions(option).
        load(uri)
        .into(this)
}
@BindingAdapter("android:imageUrl")
fun loadImage(view: ImageView,url: String?){
    view.loadImage(url, getProgressDrawable(view.context))
}