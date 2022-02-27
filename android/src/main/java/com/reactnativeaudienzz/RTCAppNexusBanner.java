package com.reactnativeaudienzz;

import android.content.Context;
import android.util.AttributeSet;
import com.appnexus.opensdk.BannerAdView;

public class RTCAppNexusBanner extends BannerAdView {

  private boolean isLoading = false;
  private int isVisible = Constants.BANNER_NOT_VISIBLE;
  private int percentVisibility = Constants.PERCENT_VISIBILITY;

  public void setIsVisible(int isVisible) {
    this.isVisible = isVisible;
  }

  public void setPercentVisibility(int percentVisibility) {
    this.percentVisibility = percentVisibility;
  }

  public int getPercentVisibility() {
    return this.percentVisibility;
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

  public RTCAppNexusBanner(Context context) {
    super(context);
  }

  public RTCAppNexusBanner(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public RTCAppNexusBanner(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public RTCAppNexusBanner(Context context, int refresh_interval) {
    super(context, refresh_interval);
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
      layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }
  };
}
