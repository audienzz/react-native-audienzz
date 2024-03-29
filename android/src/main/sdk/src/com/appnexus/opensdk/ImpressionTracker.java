/*
 *    Copyright 2014 APPNEXUS INC
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

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.Settings.ImpressionType;
import com.appnexus.opensdk.viewability.ANOmidAdSession;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

class ImpressionTracker {
    private ArrayList<String> urls;
    private VisibilityDetector visibilityDetector;
    private boolean fired = false;
    private Context context;
    private ImpressionListener listener;
    private ANOmidAdSession anOmidAdSession;
    private ImpressionTrackerListener impressionTrackerListener;
    private WeakReference<View> viewWeakReference;
    private boolean isFired = false;
    private int countOfImpressionTrackersFired = 0;

    static ImpressionTracker create(WeakReference<View> viewWeakReference, ArrayList<String> urls, VisibilityDetector visibilityDetector, Context context, ANOmidAdSession anOmidAdSession, ImpressionType impressionType, ImpressionTrackerListener impressionTrackerListener) {
        if (visibilityDetector == null) {
            return null;
        } else {
            ImpressionTracker impressionTracker = new ImpressionTracker(viewWeakReference, urls, visibilityDetector, context, anOmidAdSession, impressionType, impressionTrackerListener);
            return impressionTracker;
        }
    }

    private ImpressionTracker(WeakReference<View> viewWeakReference, ArrayList<String> urls, VisibilityDetector visibilityDetector, Context context, ANOmidAdSession anOmidAdSession, ImpressionType impressionType, ImpressionTrackerListener impressionTrackerListener) {
        this.viewWeakReference = viewWeakReference;
        this.urls = urls;
        this.visibilityDetector = visibilityDetector;
        this.listener = new ImpressionListener();
        this.context = context;
        this.anOmidAdSession = anOmidAdSession;
        this.impressionTrackerListener = impressionTrackerListener;
        View view = viewWeakReference.get();
        if (view != null) {
            if (impressionType == Settings.ImpressionType.BEGIN_TO_RENDER) {
                listener.onVisibilityChanged(true);
            } else {
                view.setTag(R.string.native_view_tag, listener);
                visibilityDetector.addVisibilityListener(viewWeakReference.get());
            }
        }
    }

    private void fire() {
        // check if impression has already fired
        if (!fired) {
            SharedNetworkManager nm = SharedNetworkManager.getInstance(context);
            if (nm.isConnected(context)) {
                for (final String url : urls) {
                    @SuppressLint("StaticFieldLeak") HTTPGet asyncTask = new HTTPGet() {
                        @Override
                        protected void onPostExecute(HTTPResponse response) {
                            Clog.d(Clog.nativeLogTag, "Impression tracked.");
                            countOfImpressionTrackersFired++;
                            isFired = countOfImpressionTrackersFired == urls.size();
                            Clog.i("Impression Tracker", "Number of Impression trackers fired: " + countOfImpressionTrackersFired);
                            if (impressionTrackerListener != null && isFired) {
                                impressionTrackerListener.onImpressionTrackerFired();
                            }
                        }

                        @Override
                        protected String getUrl() {
                            return url;
                        }
                    };
                    asyncTask.execute();
                }
            } else {
                for (final String url : urls) {
                    nm.addURL(url, context, new ImpressionTrackerListener() {
                        @Override
                        public void onImpressionTrackerFired() {
                            if (impressionTrackerListener != null) {
                                impressionTrackerListener.onImpressionTrackerFired();
                            }
                        }
                    });
                    if (impressionTrackerListener != null) {
                        impressionTrackerListener.onImpressionTrackerFired();
                    }
                }
            }
            if (anOmidAdSession != null) {
                anOmidAdSession.fireImpression();
            }
            visibilityDetector.destroy(viewWeakReference.get());
            listener = null;
            fired = true;
        }
    }

    class ImpressionListener implements VisibilityDetector.VisibilityListener {

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                ImpressionTracker.this.fire();
                Clog.d(Clog.baseLogTag, "FIRING Impression Tracker");
            }
        }
    }

}
