#if __has_include(<React/RCTComponent.h>)
#import <React/RCTView.h>
#else
#import "RCTView.h"
#endif

#import <AppNexusSDK/ANBannerAdView.h>
#import <AppNexusSDK/ANVideoAdPlayer.h>

@class RCTBridge;
@class RCTAppNexusBannerView;

@interface RCTAppNexusBannerView : RCTView<ANBannerAdViewDelegate>

@property (nonatomic) NSString * _Nonnull placementId;
@property (nonatomic) BOOL *lazyLoad;
@property (nonatomic) NSArray *sizes;
@property (nonatomic) double autoRefreshInterval;
@property (nonatomic) NSDictionary *keywords;
@property (nonatomic) BOOL shouldResizeAdToFitContainer;
@property (nonatomic) BOOL allowVideo;
@property (nonatomic) NSNumber *reactTag;
@property (nonatomic) NSString *bannerVisible;
@property (nonatomic) NSString *customUserAgent;
@property (nonatomic) NSInteger * _Nullable percentVisibility;

@property (nonatomic, readonly, weak) RCTBridge *bridge;

@property (nonatomic, copy) RCTDirectEventBlock onAdLoadSuccess;
@property (nonatomic, copy) RCTDirectEventBlock onAdLazyLoadSuccess;
@property (nonatomic, copy) RCTDirectEventBlock onAdLoadFail;
@property (nonatomic, copy) RCTDirectEventBlock onEventChange;
@property (nonatomic, copy) RCTDirectEventBlock onAdVisibleChange;

- (void)removeBanner;
- (void)loadAdBanner;
- (void)lazyLoadAdBanner;
- (void)viewLazyAdBanner;
- (void)forceReloadBanner;
- (void)setVisibleBanner:(CGRect)clipRect relativeToView:(UIView *)clipView;
- (void)createAdBanner:(BOOL)enableLazyLoad;

- (instancetype)initWithFrame:(CGRect)frame
                       bridge:(RCTBridge *)bridge
                     reactTag:(NSNumber *)reactTag;

@end
