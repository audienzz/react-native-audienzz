package com.reactnativeaudienzz;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

public class RCTAppNexusLibraryModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public RCTAppNexusLibraryModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RCTAppNexusLibrary";
    }
}