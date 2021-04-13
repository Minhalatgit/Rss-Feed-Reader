package com.free.printable.coupons.views

import android.os.Bundle
import androidx.navigation.NavDirections
import com.free.printable.R
import kotlin.Int
import kotlin.String

public class RssFeedFragmentDirections private constructor() {
  private data class ActionRssFeedFragmentToWebViewFragment(
    public val url: String
  ) : NavDirections {
    public override fun getActionId(): Int = R.id.action_rssFeedFragment_to_webViewFragment

    public override fun getArguments(): Bundle {
      val result = Bundle()
      result.putString("url", this.url)
      return result
    }
  }

  public companion object {
    public fun actionRssFeedFragmentToWebViewFragment(url: String): NavDirections =
        ActionRssFeedFragmentToWebViewFragment(url)
  }
}
