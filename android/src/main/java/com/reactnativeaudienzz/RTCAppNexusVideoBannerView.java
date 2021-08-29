package com.reactnativeaudienzz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.VideoView;

import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.SDKSettings;
import com.appnexus.opensdk.instreamvideo.Quartile;
import com.appnexus.opensdk.instreamvideo.ResultCallback;
import com.appnexus.opensdk.instreamvideo.VideoAd;
import com.appnexus.opensdk.instreamvideo.VideoAdLoadListener;
import com.appnexus.opensdk.instreamvideo.VideoAdPlaybackListener;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.views.scroll.ReactScrollView;
import com.facebook.react.views.view.ReactViewGroup;

import javax.annotation.Nullable;

@SuppressLint("ViewConstructor")
class RTCAppNexusVideoBannerView extends ReactViewGroup {
    private static final String TAG = "VIDEO BANNER";
    private ReactScrollView scrollView;
    private static RCTEventEmitter mEventEmitter;
    private ThemedReactContext context;
    private RTCAppNexusVideoBanner banner;
    private VideoAd videoAd;

    public RTCAppNexusVideoBannerView(ThemedReactContext themedReactContext) {
        super(themedReactContext);
        context = themedReactContext;
        setLayerType(LAYER_TYPE_HARDWARE, null);

        mEventEmitter = themedReactContext.getJSModule(RCTEventEmitter.class);
        banner = new RTCAppNexusVideoBanner(themedReactContext.getCurrentActivity());
        addView(banner);
        banner.setIsVisible(Constants.BANNER_NOT_VISIBLE);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed) {
            banner.measure(
                    MeasureSpec.makeMeasureSpec(this.getMeasuredWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(this.getMeasuredHeight(), MeasureSpec.EXACTLY));
            banner.layout(0, 0, this.getMeasuredWidth(), this.getMeasuredHeight());
        }
    }

    private final OnLayoutChangeListener childLayoutListener = new OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if (scrollView == null) {
                scrollView = RTCAppNexusVideoBannerView.fintSpecifyParent(ReactScrollView.class, v.getParent());
                if (scrollView != null) {
                    RTCAppNexusVideoBannerView.this.handleScroll(scrollView);
                } else {
                    final RTCAppNexusVideoBanner banner = (RTCAppNexusVideoBanner) RTCAppNexusVideoBannerView.this.getChildAt(0);
                    onAdVisibleChange(Constants.BANNER_FULLY_VISIBLE);
                    banner.setIsVisible(Constants.BANNER_FULLY_VISIBLE);
                }
            } else {
                RTCAppNexusVideoBannerView.this.handleScroll(scrollView);
            }
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        this.addOnLayoutChangeListener(childLayoutListener);

        if (scrollView == null) {
            scrollView = RTCAppNexusVideoBannerView.fintSpecifyParent(ReactScrollView.class, this.getParent());
            if (scrollView != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    scrollView.setOnScrollChangeListener(new OnScrollChangeListener() {
                        @Override
                        public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                            RTCAppNexusVideoBannerView.this.handleScroll(scrollView);
                        }
                    });
                } else {
                    scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                        @Override
                        public void onScrollChanged() {
                            RTCAppNexusVideoBannerView.this.handleScroll(scrollView);
                        }
                    });
                }
            } else {
                if (banner != null && banner.getIsVisible() != Constants.BANNER_FULLY_VISIBLE) {
                    onAdVisibleChange(Constants.BANNER_FULLY_VISIBLE);
                    banner.setIsVisible(Constants.BANNER_FULLY_VISIBLE);
                }
            }
        }
    }

    private void handleScroll(ScrollView scrollView) {
        Rect scrollBounds = new Rect();
        scrollView.getHitRect(scrollBounds);
        if (RTCAppNexusVideoBannerView.this.getLocalVisibleRect(scrollBounds)) {
            if (banner != null && banner.getIsVisible() != Constants.BANNER_FULLY_VISIBLE) {
                onAdVisibleChange(Constants.BANNER_FULLY_VISIBLE);
                banner.setIsVisible(Constants.BANNER_FULLY_VISIBLE);
            }
        } else {
            if (banner != null && banner.getIsVisible() != Constants.BANNER_NOT_VISIBLE) {
                onAdVisibleChange(Constants.BANNER_NOT_VISIBLE);
                banner.setIsVisible(Constants.BANNER_NOT_VISIBLE);
            }
        }
    }

    protected void createVideoBanner() {
        SDKSettings.init(context, new SDKSettings.InitListener() {
            @Override
            public void onInitFinished() {
                VideoAdLoadListener loadListener = new VideoAdLoadListener() {
                    @Override
                    public void onAdLoaded(VideoAd videoAd) {
                        int id = videoAd.getMemberID();
                        int width = banner.getSizesAd().getArray(0).getInt(0);
                        int height = banner.getSizesAd().getArray(0).getInt(1);
                        String creativeId = videoAd.getAdResponseInfo().getCreativeId();

                        onAdLoadSuccess(id, width, height, creativeId);
                    }

                    @Override
                    public void onAdRequestFailed(VideoAd videoAd, ResultCode errorCode) {
                        int id = videoAd.getMemberID();

                        if (errorCode == null) {
                            onAdLoadFail(id,"Call to loadAd failed");
                        } else {
                            onAdLoadFail(id,"Ad request failed: " + errorCode);
                        }
                    }
                };

                VideoAdPlaybackListener playbackListener = new VideoAdPlaybackListener() {
                    @Override
                    public void onAdPlaying(final VideoAd videoAd) {
                        banner.setIsPlay(true);
                    }

                    @Override
                    public void onQuartile(VideoAd view, Quartile quartile) {}

                    @Override
                    public void onAdCompleted(VideoAd view, PlaybackCompletionState playbackState) {
                        banner.setIsPlay(false);
                        onAdVisibleChange(Constants.BANNER_NOT_VISIBLE);
                    }

                    @Override
                    public void onAdMuted(VideoAd view, boolean isMute) {}

                    @Override
                    public void onAdClicked(VideoAd adView) {}

                    @Override
                    public void onAdClicked(VideoAd videoAd, String clickUrl) {}
                };

                createVideoBannerAd(context, loadListener, playbackListener);
            }
        });
    }

    public void createVideoBannerAd(Context context, VideoAdLoadListener loadListener, VideoAdPlaybackListener playbackListener) {
        videoAd = new VideoAd(context, banner.getPlacementId());
        videoAd.setAdLoadListener(loadListener);
        videoAd.setVideoPlaybackListener(playbackListener);
        setAdKeywords();
        videoAd.loadAd();
    }

    public void setAdKeywords() {
        for (String key : banner.getKeywords().toHashMap().keySet()) {
            videoAd.addCustomKeywords(key, banner.getKeywords().getString(key));
            Log.d("testr", "add keyword key = " + key + ", val = " + banner.getKeywords().getString(key));
        }
    }

    public VideoAd getVideoAd() {
        return videoAd;
    }

    public void onAdLoadSuccess(int id, int width, int height, String creativeId) {
        WritableMap event = Arguments.createMap();
        event.putString("creativeId", creativeId);
        event.putInt("width", width);
        event.putInt("height", height);

        mEventEmitter.receiveEvent(this.getId(), "onAdLoadSuccess", event);
    }

    public void onAdLoadFail(int id, String error) {
        WritableMap event = Arguments.createMap();
        event.putString("error", error);

        mEventEmitter.receiveEvent(this.getId(), "onAdLoadFail", event);
    }

    public void onAdEventChange(int id, int eventType) {
        WritableMap event = Arguments.createMap();
        event.putInt("eventType", eventType);

        mEventEmitter.receiveEvent(this.getId(), "onEventChange", event);
    }

    public void onAdVisibleChange(int visible) {
        WritableMap event = Arguments.createMap();
        event.putInt("visible", visible);

        mEventEmitter.receiveEvent(this.getId(), "onAdVisibleChange", event);
    }

    void loadBannerAd(final RTCAppNexusVideoBannerView bannerView, @Nullable ReadableArray args) {
        if (!banner.getIsLoading()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    createVideoBanner();
                    banner.setIsLoading(true);
                }
            }, 0);
        }
    }

    void viewVideoBannerAd(final RTCAppNexusVideoBannerView bannerView, @Nullable ReadableArray args) {
        if (banner != null && !banner.getIsPlay() && videoAd.isReady()) {
            videoAd.playAd(banner);
        }
    }

    public static <T> T fintSpecifyParent(Class<T> tClass, ViewParent parent) {
        if (parent == null) {
            return null;
        } else {
            if (tClass.isInstance(parent)) {
                return (T) parent;
            } else {
                return fintSpecifyParent(tClass, parent.getParent());
            }
        }
    }
}






