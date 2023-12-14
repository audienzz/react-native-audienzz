#if __has_include(<React/RCTComponent.h>)
#import <React/RCTView.h>
#else
#import "RCTView.h"
#endif

#import <AppNexusSDK/ANInstreamVideoAd.h>
#import <AppNexusSDK/ANVideoPlayerSettings.h>
#import "ANVideoAdPlayer.h"

@class RCTBridge;
@class RCTAppNexusVideoBannerView;

@interface RCTAppNexusVideoBannerView : RCTView<ANInstreamVideoAdLoadDelegate, ANInstreamVideoAdPlayDelegate>

@property (nonatomic) NSString * _Nonnull placementId;
@property (nonatomic) NSArray *sizes;
@property (nonatomic) NSDictionary *keywords;
@property (nonatomic) BOOL shouldResizeAdToFitContainer;
@property (nonatomic) BOOL openDeviceBrowser;
@property (nonatomic) NSNumber *reactTag;
@property (nonatomic) NSString *customUserAgent;
@property (nonatomic) NSInteger * _Nullable percentVisibility;

@property (strong, nonatomic)  ANInstreamVideoAd  *bannerView;
@property (strong, nonatomic)  AVPlayer *videoContentPlayer;
@property (nonatomic)  ANVideoPlayerSettings *playerSettings;

@property (nonatomic, readonly, weak) RCTBridge *bridge;

@property (nonatomic, copy) RCTDirectEventBlock onAdLoadSuccess;
@property (nonatomic, copy) RCTDirectEventBlock onAdLoadFail;
@property (nonatomic, copy) RCTDirectEventBlock onEventChange;
@property (nonatomic, copy) RCTDirectEventBlock onAdVisibleChange;

- (void)removeBanner;
- (void)loadAdVideoBanner;
- (void)viewAdVideoBanner;
- (void)forceReloadBanner;
- (void)setVisibleBanner:(CGRect)clipRect relativeToView:(UIView *)clipView;
- (void)createAdVideoBanner;

- (instancetype)initWithFrame:(CGRect)frame
                       bridge:(RCTBridge *)bridge
                     reactTag:(NSNumber *)reactTag;

@end
