package io.github.gmathi.novellibrary.util

import android.content.ContextWrapper
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import com.bumptech.glide.load.model.GlideUrl
import io.github.gmathi.novellibrary.adapter.GenericAdapter
import io.github.gmathi.novellibrary.adapter.GenericAdapterWithDragListener
import io.github.gmathi.novellibrary.dataCenter
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator
import com.bumptech.glide.load.model.LazyHeaders
import io.github.gmathi.novellibrary.network.HostNames


fun ViewGroup.inflate(layoutRes: Int): View {
    //view.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT)
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun String.addToSearchHistory() {
    val list = dataCenter.loadSearchHistory()
    if (!list.contains(this))
        list.add(0, this)
    dataCenter.saveSearchHistory(list)
}

fun String.writableFileName(): String {
    var fileName = this.replace(Regex.fromLiteral("[^a-zA-Z0-9.-]"), "_").replace("/", "_").replace(" ", "")
    if (fileName.length > 150)
        fileName = fileName.substring(0, 150)
    return fileName
}

fun String.getGlideUrl(): GlideUrl {
    val builder = LazyHeaders.Builder()
            .addHeader("User-Agent", HostNames.USER_AGENT)
            .addHeader("Cookie", dataCenter.cfCookiesString)

    return GlideUrl(this, builder.build())
}

/**
 * Sets the Adapter, LayoutManager, Animations and fixesSize flag.
 */
fun <T> RecyclerView.setDefaults(adapter: GenericAdapter<T>): RecyclerView {
    val animator = SlideInRightAnimator(OvershootInterpolator(1f))
    animator.addDuration = 1000
    animator.removeDuration = 1000
    animator.changeDuration = 0
    animator.moveDuration = 200

    this.setHasFixedSize(true)
    this.layoutManager = SnappingLinearLayoutManager(context)
    this.itemAnimator = animator
    this.adapter = adapter

    return this
}

fun RecyclerView.setDefaultsNoAnimation(adapter: RecyclerView.Adapter<*>): RecyclerView {
    this.setHasFixedSize(true)
    this.layoutManager = SnappingLinearLayoutManager(context)
    this.adapter = adapter
    return this
}

fun <T> RecyclerView.setDefaults(adapter: GenericAdapterWithDragListener<T>): RecyclerView {
    val animator = SlideInRightAnimator(OvershootInterpolator(1f))
    animator.addDuration = 1000
    animator.removeDuration = 1000
    animator.changeDuration = 0
    animator.moveDuration = 200

    this.setHasFixedSize(true)
    this.layoutManager = SnappingLinearLayoutManager(context)
    this.itemAnimator = animator
    this.adapter = adapter

    return this
}

fun Uri.getFileName(): String {
    return (this.lastPathSegment + this.toString().substringAfter("?", "")).writableFileName()
}

fun ContextWrapper.sendBroadcast(extras: Bundle, action: String) {
    val localIntent = Intent()
    localIntent.action = action
    localIntent.putExtras(extras)
    localIntent.addCategory(Intent.CATEGORY_DEFAULT)
    sendBroadcast(localIntent)
}

fun TextView.applyFont(assetManager: AssetManager?): TextView {
    assetManager?.let {
        typeface = Typeface.createFromAsset(it, "fonts/source_sans_pro_regular.ttf")
    }
    return this
}

fun TextView.setTypeface(style: Int): TextView {
    setTypeface(typeface, style)
    return this
}


private fun String?.contains(chapter: String?): Boolean {
    return (this != null) && (chapter != null) && this.contains(chapter)
}


