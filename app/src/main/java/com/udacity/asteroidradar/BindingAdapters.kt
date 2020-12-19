package com.udacity.asteroidradar

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.main.AsteroidAdapter
import com.udacity.asteroidradar.network.NETWORK_STATUS

/**
 * Binded at the RecyclerView of activity_main.xml
 * This binding adapter will update the RecyclerView items when the asteroids list live data gets updated
 */
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Asteroid>?){
    val adapter = recyclerView.adapter as AsteroidAdapter
    adapter.submitList(data)
}

@BindingAdapter("loadUrl")
fun bindPictureOfDay(imageView: ImageView, imageUrl: String?) {
    imageUrl?.let {
        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.placeholder_picture_of_day)
            .error(R.drawable.placeholder_picture_of_day)
            .into(imageView)
    }
}

@BindingAdapter("swipeManager")
fun swipeManager(swipeControl: SwipeRefreshLayout, status: NETWORK_STATUS){
    when (status){
        NETWORK_STATUS.CONNECTED -> swipeControl.isRefreshing = false
        NETWORK_STATUS.DISCONNECTED -> swipeControl.isRefreshing = false
        NETWORK_STATUS.INITIALIZING -> swipeControl.isRefreshing = true
    }
}

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}
