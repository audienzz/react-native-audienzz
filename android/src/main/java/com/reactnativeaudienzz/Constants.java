package com.reactnativeaudienzz;

public class Constants {

    //Banner Events
    public static final int BANNER_EVENT_AD_WAS_CLICKED = -1;
    public static final int BANNER_EVENT_AD_CLOSE = 0;
    public static final int BANNER_EVENT_AD_PRESENT = 1;
    //Banner Commands
    public static final int COMMAND_LOAD_AD  = 1;
    public static final int COMMAND_LAZY_LOAD_AD = 2;
    public static final int COMMAND_VISIBLE_AD = 3;
    public static final int FORCE_LOAD_AD = 11;

    // Banner Visible
    public static final int BANNER_NOT_VISIBLE = 0;
    public static final int BANNER_PARTIALLY_VISIBLE = 1;
    public static final int BANNER_FULLY_VISIBLE = 2;

    //Video banner State
    public static final int VIDEO_PLAYBACK_STATE_ERROR = -1;
    public static final int VIDEO_PLAYBACK_STATE_COMPLETED = 0;
    public static final int VIDEO_PLAYBACK_STATE_SKIPPED = 1;
    //Video banner Events
    public static final int VIDEO_EVENT_MUTED = 51;
    public static final int VIDEO_EVENT_UN_MUTED = 52;
    public static final int VIDEO_EVENT_START_PLAYING = 4;
    //Video banner Commands
    public static final int COMMAND_START_PLAYING = 1;
    public static final int COMMAND_RELOAD_VIDEO  = 2;
    public static final int COMMAND_STOP_PLAYING  = 3;
    public static final int COMMAND_LOAD_VIDEO_AD  = 4;
    public static final int COMMAND_PAUSE_AD = 5;
    public static final int COMMAND_RESUME_AD = 6;
}
