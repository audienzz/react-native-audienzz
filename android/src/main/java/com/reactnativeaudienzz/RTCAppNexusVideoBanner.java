package com.reactnativeaudienzz;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.SDKSettings;
import com.appnexus.opensdk.instreamvideo.Quartile;
import com.appnexus.opensdk.instreamvideo.ResultCallback;
import com.appnexus.opensdk.instreamvideo.VideoAd;
import com.appnexus.opensdk.instreamvideo.VideoAdLoadListener;
import com.appnexus.opensdk.instreamvideo.VideoAdPlaybackListener;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

public class RTCAppNexusVideoBanner extends FrameLayout {
  private boolean isLoading = false;
  private boolean isPlay = false;
  private int isVisible = Constants.BANNER_NOT_VISIBLE;
  private String placementId;
  private ReadableMap mapWords;
  private ReadableArray sizesAd;
  private int percentVisibility = Constants.PERCENT_VISIBILITY;

  public RTCAppNexusVideoBanner(Context context) {
    super(context);
  }

  public void setIsVisible(int isVisible) {
    this.isVisible = isVisible;
  }

  public void setPlacementId(String placementId) {
    this.placementId = placementId;
  }

  public String getPlacementId() {
    return this.placementId;
  }

  public void setSetKeywords(ReadableMap map) {
    this.mapWords = map;
  }

  public ReadableMap getKeywords() {
    return this.mapWords;
  }

  public void setSizesAd(ReadableArray sizesAd) {
    this.sizesAd = sizesAd;
  }

  public ReadableArray getSizesAd() {
    return this.sizesAd;
  }

  public int getIsVisible() {
    return this.isVisible;
  }

  public void setIsLoading(boolean isLoading) {
    this.isLoading = isLoading;
  }

  public boolean getIsLoading() {
    return this.isLoading;
  }

  public void setIsPlay(boolean isPlay) {
    this.isPlay = isPlay;
  }

  public boolean getIsPlay() {
    return this.isPlay;
  }

  public void setPercentVisibility(int percentVisibility) {
    this.percentVisibility = percentVisibility;
  }

  public int getPercentVisibility() {
    return this.percentVisibility;
  }

  @Override
  public void requestLayout() {
    super.requestLayout();
    // The spinner relies on a measure + layout pass happening after it calls requestLayout().
    // Without this, the widget never actually changes the selection and doesn't call the
    // appropriate listeners. Since we override onLayout in our ViewGroups, a layout pass never
    // happens after a call to requestLayout, so we simulate one here.
    post(measureAndLayout);
  }

  private final Runnable measureAndLayout = new Runnable() {
    @Override
    public void run() {
      measure(
        MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
      layout(getLeft(), getTop(), getRight(), getBottom());
    }
  };
}
