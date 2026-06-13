@file:Suppress("NOTHING_TO_INLINE")
package rj.kilikili.utils.extensions

import android.view.View
import android.widget.ImageView
import rj.kilikili.R

inline fun View.gone() {
    visibility = View.GONE
}
inline fun View.visible() {
    visibility = View.VISIBLE
}
inline fun View.invisible() {
    visibility = View.INVISIBLE
}

inline fun ImageView.showError() {
    setImageResource(R.drawable.loading_2233_error)
}