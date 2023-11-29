/*
 *    Copyright 2013 APPNEXUS INC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.appnexus.opensdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaRouter;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Hex;
import com.appnexus.opensdk.utils.StringUtil;
import com.appnexus.opensdk.utils.ViewUtil;
import com.appnexus.opensdk.utils.W3CEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.MEDIA_ROUTER_SERVICE;
import static android.media.MediaRouter.CALLBACK_FLAG_UNFILTERED_EVENTS;
import static android.media.MediaRouter.ROUTE_TYPE_USER;

@SuppressLint("InlinedApi")
class MRAIDImplementation {
    private MediaRouter mediaRouter;
    private MediaRouter.Callback callback;

    protected static enum MRAID_INIT_STATE {
        STARTING_DEFAULT,
        STARTING_EXPANDED
    }

    protected final static String[] MRAID_INIT_STATE_STRINGS = {"default", "expanded"};

    protected final AdWebView owner;
    private boolean readyFired = false;
    boolean expanded = false;
    boolean resized = false;
    int default_width, default_height;
    int default_gravity;
    private int screenWidth, screenHeight;
    boolean supportsPictureAPI = false;
    boolean supportsCalendar = false;
    private Activity fullscreenActivity;
    private ViewGroup defaultContainer;
    boolean isViewable;
    private String exposureVal = "";
    private int[] position = new int[4];
    private int lastRotation;
    protected boolean isMRAIDTwoPartExpanded = false;

    public MRAIDImplementation(AdWebView owner) {
        this.owner = owner;
    }

    void webViewFinishedLoading(AdWebView view, String startingState) {
        // Fire the ready event only once
        if (!readyFired) {
            String adType = owner.adView.isBanner() ? "inline" : "interstitial";
            isMRAIDTwoPartExpanded =
                    startingState.equals(
                            MRAID_INIT_STATE_STRINGS[MRAID_INIT_STATE.STARTING_EXPANDED.ordinal()]);
            owner.isFullScreen = isMRAIDTwoPartExpanded;

            view.injectJavaScript("javascript:window.mraid.util.setPlacementType('"
                    + adType + "')");

            if (!isMRAIDTwoPartExpanded) {
                setSupportsValues(view);
                setScreenSize();
                setMaxSize();
                setDefaultPosition();
                setCurrentAppOrientation();
            }

            owner.checkPosition(); //set CURRENT position, in addition to default
            view.injectJavaScript("javascript:window.mraid.util.stateChangeEvent('" + startingState + "')");
            view.injectJavaScript("javascript:window.mraid.util.readyEvent();");

            // Store width and height for close()
            owner.adView.post(new Runnable() {
                @Override
                public void run() {
                    if (owner != null && owner.adView != null) {
                        ViewGroup.LayoutParams layoutParams = owner.adView.getLayoutParams();
                        if (layoutParams != null) {
                            default_width = layoutParams.width;
                            default_height = layoutParams.height;
                        }
                    }
                }
            });

            if (owner.adView.getMediaType() == MediaType.BANNER) {
                default_gravity = ((FrameLayout.LayoutParams) owner.getLayoutParams()).gravity;
            }

            readyFired = true;
            onViewableChange(owner.isViewable());
        }
    }

    private void setDefaultPosition() {

        Activity a = (Activity) owner.getContextFromMutableContext();

        int[] location = new int[2];
        owner.getLocationOnScreen(location);

        // current position is relative to max size, so subtract the status bar from y
        int contentViewTop = a.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        location[1] -= contentViewTop;

        owner.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        int width = owner.getMeasuredWidth();
        int height = owner.getMeasuredHeight();
        int[] size = {width, height};
        ViewUtil.convertFromPixelsToDP(a, size);

        owner.injectJavaScript(String.format("javascript:window.mraid.util.setDefaultPosition(%d, %d, %d, %d)",
                location[0], location[1], size[0], size[1]));
    }

    private void setCurrentAppOrientation(){
        String orientationString = "none"; //portrait,landscape,none
        boolean orientationLocked = false; // always false for Android, since orientation is locked at activity level vs app level in iOS.

        Activity a = (Activity) owner.getContextFromMutableContext();
        if(a!=null) {
            int orientation = a.getResources().getConfiguration().orientation;
            orientationString = (orientation == Configuration.ORIENTATION_PORTRAIT) ? "portrait" : "landscape";
        }

        owner.injectJavaScript(String.format("javascript:window.mraid.util.setCurrentAppOrientation(\'%s\', %s)",
                orientationString, String.valueOf(orientationLocked)));
    }

    private void setSupports(AdWebView view, String feature, boolean value) {
        view.injectJavaScript(String.format("javascript:window.mraid.util.setSupports(\'%s\', %s)", feature, String.valueOf(value)));
    }

    @SuppressLint("NewApi")
    private void setSupportsValues(AdWebView view) {
        PackageManager pm = owner.getContext().getPackageManager();

        //SMS
        if (XandrAd.hasSMSIntent(pm)) {
            setSupports(view, "sms", true);
        }

        //Tel
        if (XandrAd.hasTelIntent(pm)) {
            setSupports(view, "tel", true);
        }

        //Calendar
        if (XandrAd.hasCalendarIntent(pm)) {
            setSupports(view, "calendar", true);
            supportsCalendar = true;
        } else if (XandrAd.hasCalendarEventIntent(pm)) {
            setSupports(view, "calendar", true);
            supportsCalendar = true;
            W3CEvent.useMIME = true;
        }


        //Store Picture only if on API 11 or above
        if (pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, owner.getContext().getPackageName()) == PackageManager.PERMISSION_GRANTED) {
            setSupports(view, "storePicture", true);
            supportsPictureAPI = true;
        }

        //Video should always work inline.
        setSupports(view, "inlineVideo", true);
    }

    void onViewableChange(boolean viewable) {
        if (!readyFired) return;
        isViewable = viewable;
        owner.injectJavaScript("javascript:window.mraid.util.setIsViewable(" + viewable + ")");
    }

    void onExposureChange() {
        if (!readyFired) return;
        owner.injectJavaScript(String.format("javascript:window.mraid.util.exposureChangeEvent(%s)", exposureVal));
    }

    void onAudioVolumeChange(String volumePercentage) {
        if (!readyFired) return;
        owner.injectJavaScript(String.format("javascript:window.mraid.util.audioVolumeChangeEvent(%s)", volumePercentage));
    }


    // parameters are view properties in pixels
    void setCurrentPosition(int left, int top, int width, int height) {
        //Set current position whether or not ready has been fired in order
        //to set the position prior to first ready fire

        if ((position[0] == left) && (position[1] == top)
                && (position[2] == width) && (position[3] == height)) return;
        position[0] = left;
        position[1] = top;
        position[2] = width;
        position[3] = height;

        Activity a = (Activity) owner.getContextFromMutableContext();
        // current position is relative to max size, so subtract the status bar from y
        int contentViewTop = a.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        top -= contentViewTop;

        int[] properties = {left, top, width, height};

        // convert properties to DP
        ViewUtil.convertFromPixelsToDP(a, properties);
        left = properties[0];
        top = properties[1];
        width = properties[2];
        height = properties[3];
        owner.injectJavaScript(String.format("javascript:window.mraid.util.setCurrentPosition(%d, %d, %d, %d)",
                left, top, width, height));
        owner.injectJavaScript(String.format("javascript:window.mraid.util.sizeChangeEvent(%d, %d)",
                width, height));
    }

    void close() {
        if (expanded || resized || isMRAIDTwoPartExpanded) {
            AdView.LayoutParams lp = new AdView.LayoutParams(
                    owner.getLayoutParams());
            lp.height = default_height;
            lp.width = default_width;
            if (owner.adView.getMediaType() == MediaType.BANNER) {
                lp.gravity = default_gravity;
            } else {
                lp.gravity = Gravity.CENTER;
            }
            owner.setLayoutParams(lp);
            owner.close();
            owner.injectJavaScript("javascript:window.mraid.util.stateChangeEvent('default');");

            //Avoid calling onAdCollapsed if this is the TwoPartView closing
            if (!owner.adView.isInterstitial() && !isMRAIDTwoPartExpanded) {
                owner.adView.getAdDispatcher().onAdCollapsed();
            }

            // Allow orientation changes
            Activity a = ((Activity) this.owner.getContextFromMutableContext());
            if (a != null)
                a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            expanded = false;
            resized = false;
            expandedWebView = null;
        } else if (owner.adView.isInterstitial()) {
            owner.adView.getAdDispatcher().onAdCollapsed();
            Activity containerActivity = ((Activity) this.owner.getContextFromMutableContext());
            if (containerActivity != null && !containerActivity.isFinishing()) {
                containerActivity.finish();
            }
        } else {
            // state must be default
            owner.hide();
        }
    }

    void setUseCustomClose(ArrayList<Pair<String, String>> parameters) {
        owner.setMRAIDUseCustomClose(Boolean.parseBoolean(parameters.get(0).second));
    }

    private MRAIDTwoPartExpandWebView expandedWebView = null;

    void expand(ArrayList<Pair<String, String>> parameters) {
        // Default value is fullscreen.
        int width = -1;
        int height = -1;
        boolean useCustomClose = false;
        String uri = null;
        boolean allowOrientationChange = true;
        AdActivity.OrientationEnum forceOrientation = AdActivity.OrientationEnum.none;
        for (Pair<String, String> bnvp : parameters) {
            switch (bnvp.first) {
                case "w":
                    try {
                        width = Integer.parseInt(bnvp.second);
                    } catch (NumberFormatException e) {
                        // Do nothing
                    }
                    break;
                case "h":
                    try {
                        height = Integer.parseInt(bnvp.second);
                    } catch (NumberFormatException e) {
                        // Do nothing
                    }
                    break;
                case "useCustomClose":
                    useCustomClose = Boolean.parseBoolean(bnvp.second);
                    break;
                case "url":
                    uri = Uri.decode(bnvp.second);
                    break;
                case "allow_orientation_change":
                    allowOrientationChange = Boolean.parseBoolean(bnvp.second);
                    break;
                case "force_orientation":
                    forceOrientation = parseForceOrientation(bnvp.second);
                    break;
                default:
                    Clog.e(Clog.mraidLogTag, "expand Invalid parameter::" + bnvp.first);
                    break;
            }
        }

        //A new webview is used for two-part creatives
        if (!StringUtil.isEmpty(uri)) {
            //We must pass a reference to the current MRAIDImplementation in order for the
            //second webview to forward the close event back

            //"This" mraid implementation is *not* reused by the second webview
            try {
                expandedWebView = new MRAIDTwoPartExpandWebView(this.owner.adView, this);
                expandedWebView.loadUrlWithMRAID(uri);

                final boolean allowOrientationChangeInner = allowOrientationChange;
                final AdActivity.OrientationEnum forceOrientationInner = forceOrientation;
                this.owner.adView.mraidFullscreenExpand(expandedWebView.getMRAIDImplementation(),
                        useCustomClose, new AdWebView.MRAIDFullscreenListener() {
                            @Override
                            public void onCreateCompleted() {
                                // lock orientation if necessary
                                if (getFullscreenActivity() != null) {
                                    expandedWebView.lockOrientationFromExpand(getFullscreenActivity(),
                                            allowOrientationChangeInner, forceOrientationInner);
                                    AdView.mraidFullscreenListener = null; // only listen once
                                }
                            }
                        });
            } catch (Exception e) {
                // Catches PackageManager$NameNotFoundException for webview
                Clog.e(Clog.baseLogTag, "Exception initializing the redirect webview: " + e.getMessage());
            }
        } else {
            owner.expand(width, height, useCustomClose, this, allowOrientationChange, forceOrientation);
        }

        // Fire the stateChange to MRAID
        this.owner.injectJavaScript("javascript:window.mraid.util.stateChangeEvent('expanded');");
        expanded = true;

        // Fire the AdListener event
        if (!this.owner.adView.isInterstitial()) {
            this.owner.adView.getAdDispatcher().onAdExpanded();
        }
    }

    void dispatch_mraid_call(String url, boolean userInteracted) {
        // Remove the fake protocol
        url = url.replaceFirst("mraid://", "");

        // Separate the function from the parameters
        String[] qMarkSplit = url.split("\\?");
        String func = qMarkSplit[0].replaceAll("/", "");
        String params;
        ArrayList<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
        if (qMarkSplit.length > 1) {
            params = url.substring(url.indexOf("?") + 1);

            for (String s : params.split("&")) {
                String[] pair = s.split("=");
                if (pair.length < 2) {
                    continue;
                }

                if (StringUtil.isEmpty(pair[1]) || "undefined".equals(pair[1])) {
                    continue;
                }

                parameters.add(new Pair(pair[0], pair[1]));
            }
        }

        if (func.equals("expand")) {
            if (userInteracted) {
                expand(parameters);
            } else {
                Clog.w(Clog.mraidLogTag, Clog.getString(R.string.no_user_interaction, url));
            }
        } else if (func.equals("close")) {
            close();
        } else if (func.equals("resize")) {
            if (userInteracted) {
                resize(parameters);
            } else {
                Clog.w(Clog.mraidLogTag, Clog.getString(R.string.no_user_interaction, url));
            }
        } else if (func.equals("setOrientationProperties")) {
            setOrientationProperties(parameters);
        } else if (supportsCalendar && func.equals("createCalendarEvent")) {
            if (userInteracted) {
                createCalendarEvent(parameters);
            } else {
                Clog.w(Clog.mraidLogTag, Clog.getString(R.string.no_user_interaction, url));
            }
        } else if (func.equals("playVideo")) {
            if (userInteracted) {
                playVideo(parameters);
            } else {
                Clog.w(Clog.mraidLogTag, Clog.getString(R.string.no_user_interaction, url));
            }
        } else if (supportsPictureAPI && func.equals("storePicture")) {
            if (userInteracted) {
                storePicture(parameters);
            } else {
                Clog.w(Clog.mraidLogTag, Clog.getString(R.string.no_user_interaction, url));
            }
        } else if (func.equals("open")) {
            if (userInteracted) {
                open(parameters);
            } else {
                Clog.w(Clog.mraidLogTag, Clog.getString(R.string.no_user_interaction, url));
            }
        } else if (func.equals("setUseCustomClose")) {
            setUseCustomClose(parameters);
        } else if (func.equals("audioVolumeChange")) {
            if (mediaRouter == null) {
                startAudioVolumeListener();
            }
        } else {
            if (func.equals("enable")) {
                // suppress error for enable command
                return;
            }
            Clog.d(Clog.mraidLogTag, Clog.getString(R.string.unsupported_mraid, func));
        }
    }

    private void open(ArrayList<Pair<String, String>> parameters) {
        String uri = null;
        for (Pair<String, String> bnvp : parameters) {
            if (bnvp.first.equals("uri")) {
                uri = Uri.decode(bnvp.second);
            }
        }

        if (!StringUtil.isEmpty(uri)) {
            this.owner.handleClickUrl(uri);
        }
    }

    private void storePicture(ArrayList<Pair<String, String>> parameters) {
        String uri = null;
        for (Pair<String, String> bnvp : parameters) {
            if (bnvp.first.equals("uri")) {
                uri = bnvp.second;
            }
        }
        if (uri == null) {
            Clog.d(Clog.mraidLogTag, Clog.getString(R.string.store_picture_error));
            return;
        }

        final String uri_final = Uri.decode(uri);

        AlertDialog.Builder builder = new AlertDialog.Builder(ViewUtil.getTopContext(owner));
        builder.setTitle(R.string.store_picture_title);
        builder.setMessage(R.string.store_picture_message);
        builder.setPositiveButton(R.string.store_picture_accept, new DialogInterface.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Check URI scheme
                if (uri_final.startsWith("data:")) {
                    //Remove 'data:(//?)' and save
                    String ext = ".png";
                    boolean isBase64 = false;
                    //First, find file type:
                    if (uri_final.contains("image/gif")) {
                        ext = ".gif";
                    } else if (uri_final.contains("image/jpeg") || uri_final.contains("image/pjpeg")) {
                        ext = ".jpg";
                    } else if (uri_final.contains("image/png")) {
                        ext = ".png";
                    } else if (uri_final.contains("image/tiff")) {
                        ext = ".tif";
                    } else if (uri_final.contains("image/svg+xml")) {
                        ext = ".svg";
                    }
                    if (uri_final.contains("base64")) {
                        isBase64 = true;
                    }
                    File out = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ext);
                    FileOutputStream outstream = null;
                    try {
                        byte[] out_array;
                        outstream = new FileOutputStream(out);
                        if (out.canWrite()) {
                            if (!isBase64) {
                                out_array = Hex.hexStringToByteArray(uri_final.substring(uri_final.lastIndexOf(",") + 1, uri_final.length()));
                            } else {
                                out_array = Base64.decode(uri_final.substring(uri_final.lastIndexOf(",") + 1, uri_final.length()), Base64.DEFAULT);
                            }

                            outstream.write(out_array);
                        }
                    } catch (FileNotFoundException e) {
                        Clog.d(Clog.mraidLogTag, Clog.getString(R.string.store_picture_error));
                    } catch (IOException e) {
                        Clog.d(Clog.mraidLogTag, Clog.getString(R.string.store_picture_error));
                    } catch (IllegalArgumentException e) {
                        Clog.d(Clog.mraidLogTag, Clog.getString(R.string.store_picture_error));
                    } finally {
                        if (outstream != null) {
                            try {
                                outstream.close();
                            } catch (IOException e) {
                                Clog.d(Clog.mraidLogTag, Clog.getString(R.string.store_picture_error));
                            }
                        }
                    }

                } else {
                    //Use the download manager
                    final DownloadManager dm = (DownloadManager) owner.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request r = new DownloadManager.Request(Uri.parse(uri_final));

                    //Check if we're writing to internal or external
                    PackageManager pm = owner.getContext().getPackageManager();
                    if (pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, owner.getContext().getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                        r.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, uri_final.split("/")[uri_final.split("/").length - 1]);
                        try {
                            r.allowScanningByMediaScanner();
                            r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            dm.enqueue(r);
                        } catch (IllegalStateException ex) {
                            Clog.d(Clog.mraidLogTag, Clog.getString(R.string.store_picture_error));
                        }
                    } else {
                        Clog.d(Clog.mraidLogTag, Clog.getString(R.string.store_picture_error));
                    }
                }

                owner.fireAdClicked();
            }
        });

        builder.setNegativeButton(R.string.store_picture_decline, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Nothing needs to be done
            }
        });

        AlertDialog d = builder.create();
        d.show();


    }

    private void playVideo(ArrayList<Pair<String, String>> parameters) {
        String uri = null;
        for (Pair<String, String> bnvp : parameters) {
            if (bnvp.first.equals("uri")) {
                uri = bnvp.second;
            }
        }
        if (uri == null) {
            Clog.d(Clog.mraidLogTag, Clog.getString(R.string.play_vide_no_uri));
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            i.setDataAndType(Uri.parse(URLDecoder.decode(uri, "UTF-8")), "video/mp4");
        } catch (UnsupportedEncodingException e) {
            Clog.d(Clog.mraidLogTag, Clog.getString(R.string.unsupported_encoding));
            return;
        }
        try {
            owner.getContext().startActivity(i);
            owner.fireAdClicked();
        } catch (ActivityNotFoundException e) {
            return;
        }
    }

    private void createCalendarEvent(ArrayList<Pair<String, String>> parameters) {
        W3CEvent event = null;
        try {
            if (parameters != null && parameters.size() > 0) {
                event = W3CEvent.createFromJSON(URLDecoder.decode(parameters.get(0).second, "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            return;
        }


        if (event != null) {
            try {
                Intent i = event.getInsertIntent();
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                owner.getContext().startActivity(i);
                owner.fireAdClicked();
                Clog.d(Clog.mraidLogTag, Clog.getString(R.string.create_calendar_event));
            } catch (ActivityNotFoundException e) {

            }
        }

    }

    private AdActivity.OrientationEnum parseForceOrientation(String value) {
        AdActivity.OrientationEnum orientation = AdActivity.OrientationEnum.none;
        if (value.equals("landscape")) {
            orientation = AdActivity.OrientationEnum.landscape;
        } else if (value.equals("portrait")) {
            orientation = AdActivity.OrientationEnum.portrait;
        } // default is none anyway, so no need to check
        return orientation;
    }

    @SuppressWarnings({"UnusedDeclaration", "UnusedAssignment"})
    private void setOrientationProperties(ArrayList<Pair<String, String>> parameters) {
        boolean allow_orientation_change = true;
        AdActivity.OrientationEnum orientation = AdActivity.OrientationEnum.none;

        for (Pair<String, String> bnvp : parameters) {
            if (bnvp.first.equals("allow_orientation_change")) {
                allow_orientation_change = Boolean.parseBoolean(bnvp.second);
            } else if (bnvp.first.equals("force_orientation")) {
                orientation = parseForceOrientation(bnvp.second);
            }
        }

        // orientationProperties only affects expanded state
        if (expanded || owner.adView.isInterstitial()) {
            Activity containerActivity = owner.isFullScreen
                    ? getFullscreenActivity() : (Activity) owner.getContextFromMutableContext();
            if (allow_orientation_change) {
                AdActivity.unlockOrientation(containerActivity);
            } else {
                int androidOrientation = Configuration.ORIENTATION_UNDEFINED;
                switch (orientation) {
                    case landscape:
                        androidOrientation = Configuration.ORIENTATION_LANDSCAPE;
                        break;
                    case portrait:
                        androidOrientation = Configuration.ORIENTATION_PORTRAIT;
                        break;
                    case none:
                    default:
                        break;
                }
                AdActivity.lockToConfigOrientation(containerActivity, androidOrientation);
            }
        }
    }

    public enum CUSTOM_CLOSE_POSITION {
        top_left,
        top_right,
        center,
        bottom_left,
        bottom_right,
        top_center,
        bottom_center
    }

    private void resize(ArrayList<Pair<String, String>> parameters) {
        int w = -1;
        int h = -1;
        int offset_x = 0;
        int offset_y = 0;
        String custom_close_position = "top-right";
        boolean allow_offscreen = true;
        for (Pair<String, String> bnvp : parameters) {
            try {
                switch (bnvp.first) {
                    case "w":
                        w = Integer.parseInt(bnvp.second);
                        break;
                    case "h":
                        h = Integer.parseInt(bnvp.second);
                        break;
                    case "offset_x":
                        offset_x = Integer.parseInt(bnvp.second);
                        break;
                    case "offset_y":
                        offset_y = Integer.parseInt(bnvp.second);
                        break;
                    case "custom_close_position":
                        custom_close_position = bnvp.second;
                        break;
                    case "allow_offscreen":
                        allow_offscreen = Boolean.parseBoolean(bnvp.second);
                        break;
                    default:
                        Clog.e(Clog.mraidLogTag, "resize Invalid parameter::" + bnvp.first);
                        break;
                }
            } catch (NumberFormatException e) {
                Clog.d(Clog.mraidLogTag, Clog.getString(R.string.number_format));
                return;
            }
        }
        //If the resized ad is larger than the screen, reject with great prejudice
        if (w > screenWidth && h > screenHeight) {
            this.owner.injectJavaScript("javascript:mraid.util.errorEvent('Resize called with resizeProperties larger than the screen.', 'mraid.resize()')");
            return;
        }

        CUSTOM_CLOSE_POSITION cp_enum = CUSTOM_CLOSE_POSITION.top_right;
        try {
            cp_enum = CUSTOM_CLOSE_POSITION.valueOf(custom_close_position.replace('-', '_'));
        } catch (IllegalArgumentException e) {
        } //Default case is used


        Clog.d(Clog.mraidLogTag, Clog.getString(R.string.resize, w, h, offset_x, offset_y, custom_close_position, allow_offscreen));
        this.owner.resize(w, h, offset_x, offset_y, cp_enum, allow_offscreen);

        owner.fireAdClicked();

        this.owner
                .injectJavaScript("javascript:window.mraid.util.stateChangeEvent('resized');");
        resized = true;

    }

    private void setMaxSize() {
        if (owner.getContextFromMutableContext() instanceof Activity) {
            Activity a = ((Activity) owner.getContextFromMutableContext());

            int[] screenSize = ViewUtil.getScreenSizeAsPixels(a);
            int maxWidth = screenSize[0];
            int maxHeight = screenSize[1];

            // subtract status bar from total screen size
            int contentViewTop = a.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
            maxHeight -= contentViewTop;

            //Convert to DPs
            final float scale = a.getResources().getDisplayMetrics().density;
            maxHeight = (int) ((maxHeight / scale) + 0.5f);
            maxWidth = (int) ((maxWidth / scale) + 0.5f);

            owner.injectJavaScript("javascript:window.mraid.util.setMaxSize(" + maxWidth + ", " + maxHeight + ")");
        }
    }

    private void setScreenSize() {
        if (owner.getContextFromMutableContext() instanceof Activity) {
            int[] screenSize = ViewUtil.getScreenSizeAsDP(((Activity) owner.getContextFromMutableContext()));
            screenWidth = screenSize[0];
            screenHeight = screenSize[1];

            owner.injectJavaScript("javascript:window.mraid.util.setScreenSize(" + screenWidth + ", " + screenHeight + ")");
        }
    }

    void fireViewableChangeEvent() {
        boolean isCurrentlyViewable = owner.isViewable();
        if (isViewable != isCurrentlyViewable) {
            this.onViewableChange(isCurrentlyViewable);
            if (mediaRouter != null) {
                fireAudioVolumeChangeEvent(getAudioVolumePercentage());
            }
        }
    }

    //@NOTE  occlusionRectangles - Not Supported.
    void fireExposureChangeEvent(double exposedPercentage, Rect visibleRect) {
        String newExposureVal = "";
        if (visibleRect != null) {
            Activity a = (Activity) owner.getContextFromMutableContext();
            int[] size = {visibleRect.left, visibleRect.top, visibleRect.width(), visibleRect.height()};
            ViewUtil.convertFromPixelsToDP(a, size);
            newExposureVal = String.format(Locale.ROOT,"{\"exposedPercentage\":%.1f,\"visibleRectangle\":{\"x\":%d,\"y\":%d,\"width\":%d,\"height\":%d},\"occlusionRectangles\":null}", exposedPercentage, size[0], size[1], size[2], size[3]);
        } else {
            newExposureVal = String.format(Locale.ROOT,"{\"exposedPercentage\":%.1f,\"visibleRectangle\":null,\"occlusionRectangles\":null}", exposedPercentage);
        }

        if (!exposureVal.equals(newExposureVal)) {
            exposureVal = newExposureVal;
            this.onExposureChange();
        }
    }

    //Fire AudioVolumeChange event whenever there is change in audio volume of device
    void fireAudioVolumeChangeEvent(Double volumePercentage) {
        if (isViewable) {
            String newAudioVolumeVal = "";
            if (volumePercentage == null) {
                newAudioVolumeVal = String.format(Locale.ROOT, "{\"volumePercentage\":null}");
            } else {
                newAudioVolumeVal = String.format(Locale.ROOT, "{\"volumePercentage\":%.1f}", volumePercentage);
            }
            this.onAudioVolumeChange(newAudioVolumeVal);
        }
    }

    void onOrientationChanged(int orientation) {
        if (lastRotation != orientation) {
            lastRotation = orientation;
            setMaxSize();
            setScreenSize();
        }
    }

    Activity getFullscreenActivity() {
        return fullscreenActivity;
    }

    void setFullscreenActivity(Activity fullscreenActivity) {
        this.fullscreenActivity = fullscreenActivity;
    }

    ViewGroup getDefaultContainer() {
        return defaultContainer;
    }

    void setDefaultContainer(ViewGroup defaultContainer) {
        this.defaultContainer = defaultContainer;
    }

    //Initialise AudioVolumeContentObserver to get notified whenever audio volume changes take place
    private void startAudioVolumeListener() {
        mediaRouter = (MediaRouter) owner.getContext().getSystemService(MEDIA_ROUTER_SERVICE);
        setupCallback();
        mediaRouter.addCallback(ROUTE_TYPE_USER, callback, CALLBACK_FLAG_UNFILTERED_EVENTS);
        fireAudioVolumeChangeEvent(getAudioVolumePercentage());
    }

    private void setupCallback() {
        callback = new MediaRouter.Callback() {

            @Override
            public void onRouteSelected(MediaRouter router, int type, MediaRouter.RouteInfo info) {
                Clog.d("onRouteSelected", info.getName().toString());
                // This is called when the MediaRoute is changed from Phone or Headphones to Bluetooth or vice-versa
                fireAudioVolumeChangeEvent(getAudioVolumePercentage());
            }

            @Override
            public void onRouteUnselected(MediaRouter router, int type, MediaRouter.RouteInfo info) {
                Clog.d("onRouteUnselected", info.getName().toString());
            }

            @Override
            public void onRouteAdded(MediaRouter router, MediaRouter.RouteInfo info) {
                Clog.d("onRouteAdded", info.getName().toString());
            }

            @Override
            public void onRouteRemoved(MediaRouter router, MediaRouter.RouteInfo info) {
                Clog.d("onRouteRemoved", info.getName().toString());
            }

            @Override
            public void onRouteChanged(MediaRouter router, MediaRouter.RouteInfo info) {
                Clog.d("onRouteChanged", info.getName().toString());
                // This is called when the MediaRoute is changed from Phone to Headphone or vice-versa
                fireAudioVolumeChangeEvent(getAudioVolumePercentage());
            }

            @Override
            public void onRouteGrouped(MediaRouter router, MediaRouter.RouteInfo info, MediaRouter.RouteGroup group, int index) {
                Clog.d("onRouteUngrouped", info.getName().toString());
            }

            @Override
            public void onRouteUngrouped(MediaRouter router, MediaRouter.RouteInfo info, MediaRouter.RouteGroup group) {
                Clog.d("onRouteUngrouped", info.getName().toString());

            }

            @Override
            public void onRouteVolumeChanged(MediaRouter router, MediaRouter.RouteInfo info) {
                Clog.d("onRouteVolumeChanged", router.getDefaultRoute().getCategory().getName().toString());
                Clog.d("onRouteVolumeChanged", info.getName() + ", " + info.getVolume() + ", " + info.getSupportedTypes());
                fireAudioVolumeChangeEvent(getAudioVolumePercentage());
            }
        };

    }

    //Deallocate AudioVolumeContentObserver when memory cleanup takes place
    private void stopAudioVolumeListener() {
        if (mediaRouter != null && callback != null) {
            mediaRouter.removeCallback(callback);
            mediaRouter = null;
            callback = null;
        }
    }

    public void destroy() {
        stopAudioVolumeListener();
    }

    // Helper method to fetch volume level of device
    private Double getAudioVolumePercentage() {
        AudioManager audioManager = (AudioManager)owner.getContext().getSystemService(Context.AUDIO_SERVICE);
        if(audioManager == null){
            return null;
        }
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return (100.0 * currentVolume) / maxVolume;
    }
}
