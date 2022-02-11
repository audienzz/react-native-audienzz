import React, { useRef, useState, useEffect } from 'react';
import {
  requireNativeComponent,
  ActivityIndicator,
  AppState,
  LayoutAnimation,
  Dimensions,
  Platform,
} from 'react-native';
import {
  bannerEventChangeAction,
  forceReloadBanner,
  lazyLoadAdBanner,
  viewLazyAdBanner,
} from './utils';
import {
  BANNER_STATE_TYPE,
  MIN_RN_VERSION_STATE_LISTENER_REMOVE,
} from '../Constants';

interface INativeEvent {
  nativeEvent: {
    width?: number;
    height?: number;
    error?: string;
    eventType?: string;
    visible?: number;
  };
}

interface bannerStyles {
  width: number;
  height: number;
}

export type AppNexusBannerProps = {
  placementId: string;
  sizes: number[][];
  autoRefreshInterval?: number;
  keywords?: object;
  onAdLoadSuccess?: () => void;
  onAdLazyLoadSuccess?: () => void;
  onAdLoadFail?: (event: string | undefined) => void;
  onEventChange?: (event: string | undefined) => void;
  allowVideo?: boolean;
  reloadOnAppStateChangeIfFailed?: boolean;
  onAdVisibleChange?: (event: number | undefined) => void;
  customUserAgent?: string | undefined;
};

export const AppNexusBanner: React.FC<AppNexusBannerProps> = ({
  placementId,
  sizes = [[Dimensions.get('window').width, 1]],
  autoRefreshInterval,
  keywords,
  onAdLoadSuccess,
  onAdLazyLoadSuccess,
  onAdLoadFail,
  onEventChange,
  allowVideo,
  reloadOnAppStateChangeIfFailed,
  onAdVisibleChange,
  customUserAgent,
  ...props
}) => {
  let bannerRef = useRef(null);
  const [adRequestProcessed, setAdRequestProcessed] = useState<boolean>(false);
  const [adLoaded, setAdLoaded] = useState<boolean>(false);
  const [bannerVisible, setBannerVisible] = useState<number>(
    BANNER_STATE_TYPE.BANNER_NOT_VISIBLE
  );
  const [width, setWidth] = useState<number>(0);
  const [height, setHeight] = useState<number>(0);
  const [bannerStyles, setBannerStyles] = useState<bannerStyles | null>({
    width,
    height,
  });

  /**
   * This means the app became active again
   */
  useEffect(() => {
    const { reactNativeVersion } = Platform.constants;
    const _handleAppStateChange = (nextAppState: string) => {
      if (nextAppState === 'active') {
        if (
          bannerVisible !== BANNER_STATE_TYPE.BANNER_NOT_VISIBLE &&
          !adLoaded &&
          adRequestProcessed &&
          reloadOnAppStateChangeIfFailed
        ) {
          setAdRequestProcessed(false);
          forceReloadBanner(bannerRef);
        }
      }
    };

    let changeListener: any;
    if (reactNativeVersion.minor >= MIN_RN_VERSION_STATE_LISTENER_REMOVE) {
      changeListener = AppState.addEventListener(
        'change',
        _handleAppStateChange
      );
    } else {
      AppState.addEventListener('change', _handleAppStateChange);
    }

    return () => {
      if (
        reactNativeVersion.minor >= MIN_RN_VERSION_STATE_LISTENER_REMOVE &&
        changeListener
      ) {
        changeListener.remove();
      } else {
        AppState.removeEventListener('change', _handleAppStateChange);
      }
    };
  }, [
    adRequestProcessed,
    bannerVisible,
    adLoaded,
    reloadOnAppStateChangeIfFailed,
  ]);

  /**
   * Initial preload banner
   */
  useEffect(() => {
    lazyLoadAdBanner(bannerRef);
  }, []);

  /**
   * Displaying the area for displaying the banner
   */
  useEffect(() => {
    const timeout = setTimeout(() => {
      LayoutAnimation.configureNext(LayoutAnimation.Presets.easeInEaseOut);
      setBannerStyles({ width, height });
    }, 300);

    return () => {
      if (timeout) {
        clearTimeout(timeout);
      }
    };
  }, [width, height]);

  /**
   * Banner appearance handler in the viewport
   * @param isVisible
   */
  const onViewportChangedHandler = (isVisible: number | undefined) => {
    if (isVisible === BANNER_STATE_TYPE.BANNER_FULLY_VISIBLE && adLoaded) {
      viewLazyAdBanner(bannerRef);
    } else if (
      (isVisible === BANNER_STATE_TYPE.BANNER_PARTIALLY_VISIBLE ||
        isVisible === BANNER_STATE_TYPE.BANNER_FULLY_VISIBLE) &&
      !adLoaded
    ) {
      setWidth(
        sizes[0] && sizes[0][0] ? sizes[0][0] : Dimensions.get('window').width
      );
      setHeight(1);
    }
  };

  /**
   * The banner was loaded successfully, we are updating the data
   * @param event
   */
  const onAdLoadSuccessHandler = (event: INativeEvent) => {
    setAdLoaded(true);

    if (
      event.nativeEvent &&
      event.nativeEvent.height &&
      event.nativeEvent.width
    ) {
      setWidth(event.nativeEvent.width);
      setHeight(event.nativeEvent.height);
      setAdRequestProcessed(true);
    }

    if (onAdLoadSuccess) {
      onAdLoadSuccess();
    }
  };

  /**
   * The banner was lazy loaded successfully, we are updating the data
   * @param event
   */
  const onAdLazyLoadSuccessHandler = (event: INativeEvent) => {
    setAdLoaded(true);

    if (
      event.nativeEvent &&
      event.nativeEvent.height &&
      event.nativeEvent.width
    ) {
      setWidth(event.nativeEvent.width);
      setHeight(event.nativeEvent.height);
      setAdRequestProcessed(true);
    }

    if (onAdLazyLoadSuccess) {
      onAdLazyLoadSuccess();
    }
  };

  /**
   * Banner not loaded, hide the block
   * @param event
   */
  const onAdLoadFailHandler = (event: INativeEvent) => {
    if (bannerVisible !== BANNER_STATE_TYPE.BANNER_NOT_VISIBLE) {
      setWidth(
        sizes[0] && sizes[0][0] ? sizes[0][0] : Dimensions.get('window').width
      );
      setHeight(1);
    }

    setAdLoaded(false);
    setAdRequestProcessed(true);
    setBannerVisible(BANNER_STATE_TYPE.BANNER_NOT_VISIBLE);

    if (onAdLoadFail) {
      onAdLoadFail(event.nativeEvent.error);
    }

    if (onAdVisibleChange) {
      onAdVisibleChange(BANNER_STATE_TYPE.BANNER_NOT_VISIBLE);
    }
  };

  /**
   * Banner event handler
   * @param event
   */
  const onEventChangeHandler = (event: INativeEvent) => {
    const eventType: string = bannerEventChangeAction(
      Number(event.nativeEvent.eventType)
    );
    onEventChange && onEventChange(eventType);
  };

  /**
   * Banner visibility handler
   * @param event
   */
  const onAdVisibleChangeHandler = (event: INativeEvent) => {
    const visible: number = event.nativeEvent.visible
      ? event.nativeEvent.visible
      : BANNER_STATE_TYPE.BANNER_NOT_VISIBLE;

    setBannerVisible(visible);
    onViewportChangedHandler(visible);
    onAdVisibleChange && onAdVisibleChange(visible);
  };

  /**
   * Displaying the component
   */
  return (
    <>
      {!adRequestProcessed && <ActivityIndicator size="large" />}
      <RCTAppNexusBanner
        {...props}
        allowVideo={allowVideo}
        placementId={placementId}
        sizes={sizes}
        autoRefreshInterval={autoRefreshInterval}
        keywords={keywords}
        ref={bannerRef}
        // @ts-ignore
        onAdLoadSuccess={onAdLoadSuccessHandler}
        // @ts-ignore
        onAdLazyLoadSuccess={onAdLazyLoadSuccessHandler}
        // @ts-ignore
        onAdLoadFail={onAdLoadFailHandler}
        style={bannerStyles}
        // @ts-ignore
        onEventChange={onEventChangeHandler}
        // @ts-ignore
        onAdVisibleChange={onAdVisibleChangeHandler}
        customUserAgent={customUserAgent}
      />
    </>
  );
};

export default AppNexusBanner;

const RCTAppNexusBanner =
  requireNativeComponent<AppNexusBannerProps>('RCTAppNexusBanner');
