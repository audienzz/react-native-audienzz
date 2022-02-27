#import "RCTAppNexusBannerManager.h"
#import "RCTAppNexusBannerView.h"

#import <AppNexusSDK/ANSDKSettings.h>

#if __has_include(<React/RCTBridge.h>)
#import <React/RCTBridge.h>
#import <React/RCTUIManager.h>
#import <React/RCTEventDispatcher.h>
#else
#import "RCTBridge.h"
#import "RCTUIManager.h"
#import "RCTEventDispatcher.h"
#endif

@implementation RCTAppNexusBannerManager

RCT_EXPORT_MODULE();

- (UIView *)view
{
    return [RCTAppNexusBannerView new];
}

- (instancetype)init
{
    self = [super init];
    return self;
}

RCT_EXPORT_METHOD(removeBanner:(nonnull NSNumber *)reactTag)
{
    [self.bridge.uiManager addUIBlock:^(__unused RCTUIManager *uiManager, NSDictionary<NSNumber *, RCTAppNexusBannerView *> *viewRegistry) {
        RCTAppNexusBannerView *view = viewRegistry[reactTag];
        if (![view isKindOfClass:[RCTAppNexusBannerView class]]) {
            RCTLogError(@"func: removeBanner Invalid view returned from registry, expecting RCTAppNexusBannerView, got: %@", view);
        } else {
            [view removeBanner];
        }
    }];
}

RCT_EXPORT_METHOD(loadAdBanner:(nonnull NSNumber *)reactTag)
{
    [self.bridge.uiManager addUIBlock:^(__unused RCTUIManager *uiManager, NSDictionary<NSNumber *, RCTAppNexusBannerView *> *viewRegistry) {
        RCTAppNexusBannerView *view = viewRegistry[reactTag];
        if (![view isKindOfClass:[RCTAppNexusBannerView class]]) {
            RCTLogError(@"func: loadAdBanner Invalid view returned from registry, expecting RCTAppNexusBannerView, got: %@", view);
        } else {
            [view loadAdBanner];
        }
    }];
}

RCT_EXPORT_METHOD(lazyLoadAdBanner:(nonnull NSNumber *)reactTag)
{
    [self.bridge.uiManager addUIBlock:^(__unused RCTUIManager *uiManager, NSDictionary<NSNumber *, RCTAppNexusBannerView *> *viewRegistry) {
        RCTAppNexusBannerView *view = viewRegistry[reactTag];
        if (![view isKindOfClass:[RCTAppNexusBannerView class]]) {
            RCTLogError(@"func: loadAdBanner Invalid view returned from registry, expecting RCTAppNexusBannerView, got: %@", view);
        } else {
            [view lazyLoadAdBanner];
        }
    }];
}

RCT_EXPORT_METHOD(viewLazyAdBanner:(nonnull NSNumber *)reactTag)
{
    [self.bridge.uiManager addUIBlock:^(__unused RCTUIManager *uiManager, NSDictionary<NSNumber *, RCTAppNexusBannerView *> *viewRegistry) {
        RCTAppNexusBannerView *view = viewRegistry[reactTag];
        if (![view isKindOfClass:[RCTAppNexusBannerView class]]) {
            RCTLogError(@"func: loadAdBanner Invalid view returned from registry, expecting RCTAppNexusBannerView, got: %@", view);
        } else {
            [view viewLazyAdBanner];
        }
    }];
}

RCT_EXPORT_METHOD(forceReloadBanner:(nonnull NSNumber *)reactTag)
{
    [self.bridge.uiManager addUIBlock:^(__unused RCTUIManager *uiManager, NSDictionary<NSNumber *, RCTAppNexusBannerView *> *viewRegistry) {
        RCTAppNexusBannerView *view = viewRegistry[reactTag];
        if (![view isKindOfClass:[RCTAppNexusBannerView class]]) {
            RCTLogError(@"func: forceReloadBanner Invalid view returned from registry, expecting RCTAppNexusBannerView, got: %@", view);
        } else {
            [view forceReloadBanner];
        }
    }];
}

+ (BOOL)requiresMainQueueSetup
{
    return NO;
}

RCT_EXPORT_VIEW_PROPERTY(placementId, NSString *)
RCT_EXPORT_VIEW_PROPERTY(lazyLoad, BOOL *)
RCT_EXPORT_VIEW_PROPERTY(sizes, NSArray)
RCT_EXPORT_VIEW_PROPERTY(autoRefreshInterval, double)
RCT_EXPORT_VIEW_PROPERTY(keywords, NSDictionary)
RCT_EXPORT_VIEW_PROPERTY(shouldResizeAdToFitContainer, BOOL)
RCT_EXPORT_VIEW_PROPERTY(allowVideo, BOOL)
RCT_EXPORT_VIEW_PROPERTY(customUserAgent, NSString *)
RCT_EXPORT_VIEW_PROPERTY(percentVisibility, NSInteger *)

RCT_EXPORT_VIEW_PROPERTY(onAdLoadSuccess, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onAdLazyLoadSuccess, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onAdLoadFail, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onEventChange, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onAdVisibleChange, RCTDirectEventBlock)

@end
