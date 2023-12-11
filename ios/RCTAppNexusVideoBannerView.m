#import "RCTAppNexusVideoBannerView.h"
#import <AppNexusSDK/ANSDKSettings.h>
#import "RCTAppNexusUtils.h"
#import <AppNexusSDK/XandrAd.h>

#if __has_include(<React/RCTBridgeModule.h>)
#import <React/RCTBridgeModule.h>
#import <React/UIView+React.h>
#import <React/RCTLog.h>
#else
#import "RCTBridgeModule.h"
#import "UIView+React.h"
#import "RCTLog.h"
#endif

typedef NS_ENUM(NSInteger, ANInstreamVideoEventType)
{
    BannerEventAdWasClicked            = -1,
    BannerEventAdClose                 = 0,
    BannerEventDidPresent              = 1,
    BannerEventAdWillDisappear         = 2,
    BannerEventAdWillAppear            = 3,
    BannerEventWillLeaveApp            = 4,

    BANNER_NOT_VISIBLE                 = 0,
    BANNER_PARTIALLY_VISIBLE           = 1,
    BANNER_PERCENT_VISIBLE             = 2,
    BANNER_FULLY_VISIBLE               = 3
};

@implementation RCTAppNexusVideoBannerView
{
    BOOL _isLoaded;
    NSInteger _isVisible;
    BOOL _isSendNotification;
}

- (instancetype)initWithFrame:(CGRect)frame
                       bridge:(RCTBridge *)bridge
                     reactTag:(NSNumber *)reactTag
{
  if ((self = [super initWithFrame:frame])) {
    _bridge = bridge;
    _reactTag = reactTag;

    _isLoaded = NO;
    _isVisible = 0;
    _isSendNotification = NO;
  }

  return self;
}

- (void)dealloc
{
    [self removeBanner];
}

- (void)layoutSubviews {
    super.removeClippedSubviews = YES;
    [super layoutSubviews];
}

- (void)removeBanner
{
    [self.bannerView removeAd];
    [self.bannerView removeFromSuperview];
}

- (void)didSetProps:(NSArray<NSString *> *)changedProps {}

- (void)forceReloadBanner
{
    if (self.bannerView != nil) {
        [self.bannerView loadAdWithDelegate:self];
    } else {
        _isLoaded = NO;
        [self loadAdVideoBanner];
    }
}

- (void)loadAdVideoBanner
{
    NSLog(@"Zoftify - loadAdBanner");

    if (_customUserAgent != nil) {
        ANSDKSettings *settings = [ANSDKSettings sharedInstance];
        settings.customUserAgent = _customUserAgent;

        [self createAdVideoBanner];
    } else {
        [RCTAppNexusUtils getUserAgent:^(NSString *userAgent) {
            ANSDKSettings *settings = [ANSDKSettings sharedInstance];
            settings.customUserAgent = userAgent;

            [self createAdVideoBanner];
        }];
    }
}

- (void)viewAdVideoBanner
{
    [self.bannerView playAdWithContainer:self withDelegate:self];
}

- (void)createAdVideoBanner {
    [[XandrAd sharedInstance] initWithMemberID: [_placementId intValue] preCacheRequestObjects:YES completionHandler:^(BOOL success){
                                  if(success){
                                    NSLog(@"Completion is called with status success ");
                                  }else{
                                    NSLog(@"Completion is called with status failed ");
                                  }
                              }];
    if (_isLoaded){
        return;
    }

    super.backgroundColor = [UIColor clearColor];

    ANVideoPlayerSettings *_playerSettings = [ANVideoPlayerSettings sharedInstance];
    _playerSettings.initalAudio = (ANInitialAudioSetting)SoundOff;

    self.bannerView = [[ANInstreamVideoAd alloc] initWithPlacementId:_placementId];
    [self.bannerView loadAdWithDelegate:self];
    self.bannerView.clickThroughAction = ANClickThroughActionOpenDeviceBrowser;
}

- (void)react_updateClippedSubviewsWithClipRect:(CGRect)clipRect relativeToView:(UIView *)clipView
{
    if (!_isLoaded) {
        return;
    }

    clipRect = [clipView convertRect:clipRect toView:self];
    clipRect = CGRectIntersection(clipRect, self.bounds);
    clipView = self;

    if (!CGSizeEqualToSize(CGRectIntersection(clipRect, self.frame).size, CGSizeZero)) {
        if (!CGRectContainsRect(clipRect, self.frame)) {
            [self calculationVisibilityVertical:clipRect];
        } else {
            if (_isVisible != BANNER_FULLY_VISIBLE) {
                _isVisible = BANNER_FULLY_VISIBLE;

                if (_isSendNotification == NO) {
                    _isSendNotification = YES;
                    _onAdVisibleChange(@{@"visible": @(BANNER_PERCENT_VISIBLE)});
                }

                _onAdVisibleChange(@{@"visible": @(BANNER_FULLY_VISIBLE)});
            }
        }
    } else {
        if (_isVisible != BANNER_NOT_VISIBLE) {
            _isSendNotification = NO;
            _isVisible = BANNER_NOT_VISIBLE;
            _onAdVisibleChange(@{@"visible": @(BANNER_NOT_VISIBLE)});
        }
    }
}

- (void) calculationVisibilityVertical:(CGRect)clipRect {
    int percents = 0;

    if (self.frame.size.height == clipRect.size.height) {
        if (_isVisible != BANNER_FULLY_VISIBLE) {
            _isVisible = BANNER_FULLY_VISIBLE;

            if (_isSendNotification == NO) {
                _isSendNotification = YES;
                _onAdVisibleChange(@{@"visible": @(BANNER_PERCENT_VISIBLE)});
            }

            _onAdVisibleChange(@{@"visible": @(BANNER_FULLY_VISIBLE)});
        }

        return;
    }

    if (clipRect.origin.y > 0 && clipRect.size.height < self.frame.size.height) {
        percents = (self.frame.size.height - clipRect.origin.y) * 100 / self.frame.size.height;
        if (percents > 0) {
            [self onPercentVisibility:percents];
        }
    } else if (clipRect.origin.y == 0 && clipRect.size.height > 0) {
        percents = 100 - ((self.frame.size.height - clipRect.size.height) * 100 / self.frame.size.height);
        if (percents > 0) {
            [self onPercentVisibility:percents];
        }
    }
}

- (void) onPercentVisibility:(NSInteger)percents {
    if (_isVisible != BANNER_PARTIALLY_VISIBLE) {
        _isVisible = BANNER_PARTIALLY_VISIBLE;
        _onAdVisibleChange(@{@"visible": @(BANNER_PARTIALLY_VISIBLE)});
    }

    if (percents > (NSInteger)_percentVisibility && _isSendNotification == NO && _isVisible == BANNER_PARTIALLY_VISIBLE) {
        _isSendNotification = YES;
        _onAdVisibleChange(@{@"visible": @(BANNER_PERCENT_VISIBLE)});
    }
}

- (void)ad:(id)ad requestFailedWithError:(NSError *)error
{
    _isLoaded = NO;

    if (_onAdLoadFail) {
        if(error){
            _onAdLoadFail(@{@"error": [error localizedDescription]});
        } else {
            _onAdLoadFail(@{@"error": @"Call to loadAd failed"});
        }
    }
}

- (NSArray *)toCGSizes:(NSArray *)sizes
{
    NSMutableArray *adSizes = [NSMutableArray array];

    for (int i = 0; i < [sizes count]; i++) {
        CGSize size = CGSizeMake([sizes[i][0] doubleValue], [sizes[i][1] doubleValue]);
        [adSizes addObject:[NSValue valueWithCGSize:size]];
    }

    return adSizes;
}

- (void)addKeywords:(NSDictionary *)keywords toBanner:(ANInstreamVideoAd *)_bannerView
{
    NSArray *keywordKeys = [keywords allKeys];
    for (NSString *key in keywordKeys) {
        [self.bannerView addCustomKeywordWithKey:key value:keywords[key]];
    }
}

#pragma mark - ANInstreamVideoAdDelegate.

//----------------------------- -o-
- (void) adDidReceiveAd: (id<ANAdProtocol>)ad
{
    _isLoaded = YES;
    [self setNeedsLayout];
    CGSize size = CGSizeMake([_sizes[0][0] doubleValue], [_sizes[0][1] doubleValue]);

    if (_onAdLoadSuccess) {
        _onAdLoadSuccess(@{
                           @"width": [NSNumber numberWithUnsignedInt:size.width],
                           @"height": [NSNumber numberWithUnsignedInt:size.height],
                           });
    }
}

//----------------------------- -o-
- (void) adCompletedFirstQuartile:(id<ANAdProtocol>)ad
{
    NSLog(@"adCompletedFirstQuartile");
}

//----------------------------- -o-
- (void) adCompletedMidQuartile:(id<ANAdProtocol>)ad
{
    NSLog(@"adCompletedMidQuartile");
}

-(void) adPlayStarted:(id<ANAdProtocol>)ad
{
    NSLog(@"adPlayStarted");
}

//----------------------------- -o-
- (void) adCompletedThirdQuartile:(id<ANAdProtocol>)ad
{
    NSLog(@"adCompletedThirdQuartile");
}

//----------------------------- -o-
- (void) adWasClicked: (id<ANAdProtocol>)ad
{
    NSLog(@"adWasClicked");
}

//----------------------------- -o-
-(void) adMute: (id<ANAdProtocol>)ad withStatus: (BOOL)muteStatus
{
    NSLog(@"adMute");
}

-(void) adDidComplete:(id<ANAdProtocol>)ad withState:(ANInstreamVideoPlaybackStateType)state
{
    NSLog(@"adDidComplete");

    _isLoaded = NO;

    if (_onAdLoadFail) {
        _onAdLoadFail(@{@"message": @"Ad Did Complete"});
    }
}

@end
