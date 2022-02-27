const VIDEO_BANNER_EVENT_TYPE_RAW = {
  RAW_ANInstreamVideoEventAdWasClicked: -1,
  RAW_ANInstreamVideoEventAdClose: 0,
  RAW_ANInstreamVideoEventDidPresent: 1,
  RAW_ANInstreamVideoEventAdWillDisappear: 2,
  RAW_ANInstreamVideoEventAdWillAppear: 3,
  RAW_ANInstreamVideoEventStartPlaying: 4,
  RAW_ANInstreamVideoEventMuted: 51,
  RAW_ANInstreamVideoEventUnMuted: 52,
  RAW_ANInstreamVideoEventWillLeaveApp: 6,
};

const VIDEO_BANNER_EVENT_TYPE = {
  ANIVEventAdWasClicked: 'adWasClicked',
  ANIVEventAdClose: 'adClosed',
  ANIVEventDidPresent: 'adPresented',
  ANIVEventAdWillDisappear: 'adWillDisappear',
  ANIVEventAdWillAppear: 'adWillAppear',
  ANIVEventStartPlaying: 'videoStartPlaying',
  ANIVEventMuted: 'videoMuted',
  ANIVEventUnMuted: 'videoUnMuted',
  ANIVEventWillLeaveApp: 'adWillLeaveApp',
};

const VIDEO_BANNER_PLAYBACK_STATE_RAW = {
  RAW_ANInstreamVideoPlaybackStateError: -1,
  RAW_ANInstreamVideoPlaybackStateCompleted: 0,
  RAW_ANInstreamVideoPlaybackStateSkipped: 1,
};

const VIDEO_BANNER_PLAYBACK_STATE = {
  ANVPlaybackStateError: 'finishedWithError',
  ANVPlaybackStateCompleted: 'videoCompleted',
  ANVPlaybackStateSkipped: 'videoSkipped',
};

const BANNER_EVENT_TYPE_RAW = {
  RAW_BannerEventAdWasClicked: -1,
  RAW_BannerEventAdClose: 0,
  RAW_BannerEventDidPresent: 1,
  RAW_BannerEventAdWillDisappear: 2,
  RAW_BannerEventAdWillAppear: 3,
  RAW_BannerEventWillLeaveApp: 4,
};

const BANNER_EVENT_TYPE = {
  BannerEventAdWasClicked: 'adWasClicked',
  BannerEventAdClose: 'adClosed',
  BannerEventDidPresent: 'adPresented',
  BannerEventAdWillDisappear: 'adWillDisappear',
  BannerEventAdWillAppear: 'adWillAppear',
  BannerEventWillLeaveApp: 'adWillLeaveApp',
};

const BANNER_STATE_TYPE = {
  BANNER_NOT_VISIBLE: 0,
  BANNER_PARTIALLY_VISIBLE: 1,
  BANNER_PERCENT_VISIBLE: 2,
  BANNER_FULLY_VISIBLE: 3,
};

const MIN_RN_VERSION_STATE_LISTENER_REMOVE = 65;

export {
  VIDEO_BANNER_EVENT_TYPE_RAW,
  VIDEO_BANNER_EVENT_TYPE,
  VIDEO_BANNER_PLAYBACK_STATE_RAW,
  VIDEO_BANNER_PLAYBACK_STATE,
  BANNER_EVENT_TYPE_RAW,
  BANNER_EVENT_TYPE,
  BANNER_STATE_TYPE,
  MIN_RN_VERSION_STATE_LISTENER_REMOVE,
};
