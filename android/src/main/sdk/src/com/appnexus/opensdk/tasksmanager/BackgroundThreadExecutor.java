/*
 *    Copyright 2020 APPNEXUS INC
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

package com.appnexus.opensdk.tasksmanager;

import android.os.Handler;
import android.os.HandlerThread;

class BackgroundThreadExecutor implements CancellableExecutor {

    private Handler h;
    private boolean running = false;
    private final int HANDLER_COUNT = 3;
    private int index = 0;

    BackgroundThreadExecutor() {
        HandlerThread backgroundThread = new HandlerThread("BackgroundThread");
        backgroundThread.start();
        h = new Handler(backgroundThread.getLooper());
        running = true;
    }


    @Override
    public void execute(Runnable runnable) {
        if (running) {
            h.post(runnable);
        }
    }

    @Override
    public boolean cancel(Runnable runnable) {
        if (running) {
            h.removeCallbacks(runnable);
            return true;
        }
        return false;
    }

    public void shutdown() {
        if (running) {
            h.getLooper().quit();
            h = null;
            running = false;
        }
    }

    public void startThread() {
        if (!running) {
            HandlerThread backgroundThread = new HandlerThread("BackgroundThread");
            backgroundThread.start();
            h = new Handler(backgroundThread.getLooper());
            running = true;
        }
    }
}
