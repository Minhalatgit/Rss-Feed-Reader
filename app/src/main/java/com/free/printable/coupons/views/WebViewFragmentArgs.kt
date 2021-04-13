package com.free.printable.coupons.views

import android.os.Bundle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.String
import kotlin.jvm.JvmStatic

public data class WebViewFragmentArgs(
  public val url: String
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putString("url", this.url)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): WebViewFragmentArgs {
      bundle.setClassLoader(WebViewFragmentArgs::class.java.classLoader)
      val __url : String?
      if (bundle.containsKey("url")) {
        __url = bundle.getString("url")
        if (__url == null) {
          throw IllegalArgumentException("Argument \"url\" is marked as non-null but was passed a null value.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"url\" is missing and does not have an android:defaultValue")
      }
      return WebViewFragmentArgs(__url)
    }
  }
}
