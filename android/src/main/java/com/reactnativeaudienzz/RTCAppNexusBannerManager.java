package com.reactnativeaudienzz;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
// import android.support.annotation.NonNull;

import com.appnexus.opensdk.ANClickThroughAction;
import com.appnexus.opensdk.AdSize;
import com.appnexus.opensdk.SDKSettings;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.views.view.ReactViewGroup;
import java.util.ArrayList;
import java.util.Map;
import javax.annotation.Nullable;

class RTCAppNexusBannerManager extends ViewGroupManager<RTCAppNexusBannerView> {

    public static final String REACT_CLASS = "RCTAppNexusBanner";

    public RTCAppNexusBannerManager() {
        SDKSettings.useHttps(true);
    }

    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @NonNull
    @Override
    protected RTCAppNexusBannerView createViewInstance(@NonNull ThemedReactContext themedReactContext) {
        return new RTCAppNexusBannerView(themedReactContext);
    }

    @Nullable
    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.<String, Object>builder()
                .put("onAdLoadSuccess",
                        MapBuilder.of("registrationName", "onAdLoadSuccess"))
                .put("onAdLazyLoadSuccess",
                        MapBuilder.of("registrationName", "onAdLazyLoadSuccess"))
                .put("onAdLoadFail",
                        MapBuilder.of("registrationName", "onAdLoadFail"))
                .put("onEventChange",
                        MapBuilder.of("registrationName", "onEventChange"))
                .put("onAdVisibleChange",
                        MapBuilder.of("registrationName", "onAdVisibleChange"))
                .build();
    }

    @Nullable @Override
    public Map<String,Integer> getCommandsMap() {
        return MapBuilder.of(
                "loadAdBanner",
                Constants.COMMAND_LOAD_AD,
                "forceReloadBanner",
                Constants.FORCE_LOAD_AD,
                "lazyLoadAdBanner",
                Constants.COMMAND_LAZY_LOAD_AD,
                "viewLazyAdBanner",
                Constants.COMMAND_VISIBLE_AD
        );
    }

    public void receiveCommand(@NonNull RTCAppNexusBannerView bannerView, int commandId, @Nullable ReadableArray args) {
        switch (commandId) {
            case Constants.COMMAND_LOAD_AD:
                bannerView.loadBannerAd(bannerView, args);
                break;
            case Constants.FORCE_LOAD_AD:
                bannerView.loadBannerAd(bannerView, args);
                break;
            case Constants.COMMAND_LAZY_LOAD_AD:
                bannerView.lazyLoadBannerAd(bannerView, args);
                break;
            case Constants.COMMAND_VISIBLE_AD:
                bannerView.visibleLazyBannerAd(bannerView, args);
                break;
        }
    }

    @ReactProp(name = "keywords")
    public void setKeywords(final RTCAppNexusBannerView bannerView, ReadableMap map) {
        RTCAppNexusBanner banner = (RTCAppNexusBanner) bannerView.getChildAt(0);

        if (banner != null) {
            for (String key : map.toHashMap().keySet()) {
                banner.addCustomKeywords(key, map.getString(key));
                Log.d("testr", "add keyword key = " + key + ", val = " + map.getString(key));
            }
        }
    }

    @ReactProp(name = "sizes")
    public void setSizes(final RTCAppNexusBannerView bannerView, ReadableArray sizes) {
        RTCAppNexusBanner banner = (RTCAppNexusBanner) bannerView.getChildAt(0);

        if (banner != null) {
            ArrayList<AdSize> list = new ArrayList<>();
            for (int i = 0; i < sizes.size(); i++) {
                list.add(new AdSize(sizes.getArray(i).getInt(0), sizes.getArray(i).getInt(1)));
            }

            banner.setAdSizes(list);
        }
    }

    /**
     * This is your AppNexus placement ID.
     * @param bannerView
     * @param placement_id
     */
    @ReactProp(name="placementId")
    public void setPlacementId(final RTCAppNexusBannerView bannerView, String placement_id) {
        RTCAppNexusBanner banner = (RTCAppNexusBanner) bannerView.getChildAt(0);

        if (banner != null) {
            banner.setPlacementID(placement_id);
        }
    }

    @ReactProp(name="allowVideo")
    public void setAllowVideo(final RTCAppNexusBannerView bannerView, boolean allowVideo) {
        RTCAppNexusBanner banner = (RTCAppNexusBanner) bannerView.getChildAt(0);

        if (banner != null) {
            banner.setAllowVideoDemand(allowVideo);
        }
    }

    @ReactProp(name = "autoRefreshInterval", defaultInt = 60)
    public void setAutoRefreshInterval(final RTCAppNexusBannerView bannerView, int interval) {
        RTCAppNexusBanner banner = (RTCAppNexusBanner) bannerView.getChildAt(0);

        if (banner != null) {
            banner.setAutoRefreshInterval(interval);
        }
    }

    @Override
    public void onDropViewInstance(final RTCAppNexusBannerView bannerView) {
        RTCAppNexusBanner banner = (RTCAppNexusBanner) bannerView.getChildAt(0);

        if (banner != null) {
            super.onDropViewInstance(bannerView);
            banner.destroy();
        }
    }
}

