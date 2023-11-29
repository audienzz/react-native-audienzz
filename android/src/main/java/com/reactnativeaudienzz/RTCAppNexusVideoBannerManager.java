package com.reactnativeaudienzz;

import androidx.annotation.NonNull;
import com.appnexus.opensdk.SDKSettings;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import java.util.Map;
import javax.annotation.Nullable;

class RTCAppNexusVideoBannerManager extends ViewGroupManager<RTCAppNexusVideoBannerView> {

  public static final String REACT_CLASS = "RCTAppNexusVideoBanner";

  public RTCAppNexusVideoBannerManager() {
  }

  @NonNull
  @Override
  public String getName() {
    return REACT_CLASS;
  }

  @NonNull
  @Override
  protected RTCAppNexusVideoBannerView createViewInstance(@NonNull ThemedReactContext themedReactContext) {
    return new RTCAppNexusVideoBannerView(themedReactContext);
  }

  @Nullable
  @Override
  public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
    return MapBuilder.<String, Object>builder()
      .put("onAdLoadSuccess",
        MapBuilder.of("registrationName", "onAdLoadSuccess"))
      .put("onAdLoadFail",
        MapBuilder.of("registrationName", "onAdLoadFail"))
      .put("onEventChange",
        MapBuilder.of("registrationName", "onEventChange"))
      .put("onAdVisibleChange",
        MapBuilder.of("registrationName", "onAdVisibleChange"))
      .build();
  }

  @Nullable
  @Override
  public Map<String, Integer> getCommandsMap() {
    return MapBuilder.of(
      "loadAdVideoBanner",
      Constants.COMMAND_LOAD_AD,
      "forceReloadBanner",
      Constants.FORCE_LOAD_AD,
      "viewAdVideoBanner",
      Constants.COMMAND_VISIBLE_AD
    );
  }

  public void receiveCommand(@NonNull RTCAppNexusVideoBannerView bannerView, int commandId, @Nullable ReadableArray args) {
    switch (commandId) {
      case Constants.COMMAND_LOAD_AD:
        bannerView.loadBannerAd(bannerView, args);
        break;
      case Constants.FORCE_LOAD_AD:
        bannerView.loadBannerAd(bannerView, args);
        break;
      case Constants.COMMAND_VISIBLE_AD:
        bannerView.viewVideoBannerAd(bannerView, args);
        break;
    }
  }

  @ReactProp(name = "keywords")
  public void setKeywords(final RTCAppNexusVideoBannerView bannerView, ReadableMap map) {
    RTCAppNexusVideoBanner banner = (RTCAppNexusVideoBanner) bannerView.getChildAt(0);

    if (banner != null) {
      banner.setSetKeywords(map);
    }
  }

  @ReactProp(name = "sizes")
  public void setSizes(final RTCAppNexusVideoBannerView bannerView, ReadableArray sizes) {
    RTCAppNexusVideoBanner banner = (RTCAppNexusVideoBanner) bannerView.getChildAt(0);

    if (banner != null) {
      banner.setSizesAd(sizes);
    }
  }

  /**
   * This is your AppNexus placement ID.
   *
   * @param bannerView
   * @param placement_id
   */
  @ReactProp(name = "placementId")
  public void setPlacementId(final RTCAppNexusVideoBannerView bannerView, String placement_id) {
    RTCAppNexusVideoBanner banner = (RTCAppNexusVideoBanner) bannerView.getChildAt(0);

    if (banner != null) {
      banner.setPlacementId(placement_id);
    }
  }

  @ReactProp(name = "percentVisibility")
  public void setPercentVisibility(final RTCAppNexusVideoBannerView bannerView, int percent) {
    RTCAppNexusVideoBanner banner = (RTCAppNexusVideoBanner) bannerView.getChildAt(0);

    if (banner != null) {
      banner.setPercentVisibility(percent);
    }
  }

  @Override
  public void onDropViewInstance(final RTCAppNexusVideoBannerView bannerView) {
    RTCAppNexusVideoBanner banner = (RTCAppNexusVideoBanner) bannerView.getChildAt(0);

    if (banner != null) {
      super.onDropViewInstance(bannerView);
      bannerView.getVideoAd().activityOnDestroy();
    }
  }
}

