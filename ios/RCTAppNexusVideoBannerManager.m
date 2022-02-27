#import "RCTAppNexusVideoBannerManager.h"
#import "RCTAppNexusVideoBannerView.h"

#import <AppNexusSDK/ANSDKSettings.h>
#import <AppNexusSDK/ANLogManager.h>

#if __has_include(<React/RCTBridge.h>)
#import <React/RCTBridge.h>
#import <React/RCTUIManager.h>
#import <React/RCTEventDispatcher.h>
#else
#import "RCTBridge.h"
#import "RCTUIManager.h"
#import "RCTEventDispatcher.h"
#endif

@implementation RCTAppNexusVideoBannerManager

RCT_EXPORT_MODULE();

- (UIView *)view
{
    return [RCTAppNexusVideoBannerView new];
}

- (instancetype)init
{
    self = [super init];
    return self;
}

RCT_EXPORT_METHOD(removeBanner:(nonnull NSNumber *)reactTag)
{
    [self.bridge.uiManager addUIBlock:^(__unused RCTUIManager *uiManager, NSDictionary<NSNumber *, RCTAppNexusVideoBannerView *> *viewRegistry) {
        RCTAppNexusVideoBannerView *view = viewRegistry[reactTag];
        if (![view isKindOfClass:[RCTAppNexusVideoBannerView class]]) {
            RCTLogError(@"func: removeBanner Invalid view returned from registry, expecting RCTAppNexusVideoBannerView, got: %@", view);
        } else {
            [view removeBanner];
        }
    }];
}

RCT_EXPORT_METHOD(loadAdVideoBanner:(nonnull NSNumber *)reactTag)
{
    [self.bridge.uiManager addUIBlock:^(__unused RCTUIManager *uiManager, NSDictionary<NSNumber *, RCTAppNexusVideoBannerView *> *viewRegistry) {
        RCTAppNexusVideoBannerView *view = viewRegistry[reactTag];
        if (![view isKindOfClass:[RCTAppNexusVideoBannerView class]]) {
            RCTLogError(@"func: loadAdBanner Invalid view returned from registry, expecting RCTAppNexusVideoBannerView, got: %@", view);
        } else {
            [view loadAdVideoBanner];
        }
    }];
}

RCT_EXPORT_METHOD(viewAdVideoBanner:(nonnull NSNumber *)reactTag)
{
    [self.bridge.uiManager addUIBlock:^(__unused RCTUIManager *uiManager, NSDictionary<NSNumber *, RCTAppNexusVideoBannerView *> *viewRegistry) {
        RCTAppNexusVideoBannerView *view = viewRegistry[reactTag];
        if (![view isKindOfClass:[RCTAppNexusVideoBannerView class]]) {
            RCTLogError(@"func: loadAdBanner Invalid view returned from registry, expecting RCTAppNexusVideoBannerView, got: %@", view);
        } else {
            [view viewAdVideoBanner];
        }
    }];
}

RCT_EXPORT_METHOD(forceReloadBanner:(nonnull NSNumber *)reactTag)
{
    [self.bridge.uiManager addUIBlock:^(__unused RCTUIManager *uiManager, NSDictionary<NSNumber *, RCTAppNexusVideoBannerView *> *viewRegistry) {
        RCTAppNexusVideoBannerView *view = viewRegistry[reactTag];
        if (![view isKindOfClass:[RCTAppNexusVideoBannerView class]]) {
            RCTLogError(@"func: forceReloadBanner Invalid view returned from registry, expecting RCTAppNexusVideoBannerView, got: %@", view);
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
RCT_EXPORT_VIEW_PROPERTY(sizes, NSArray)
RCT_EXPORT_VIEW_PROPERTY(keywords, NSDictionary)
RCT_EXPORT_VIEW_PROPERTY(shouldResizeAdToFitContainer, BOOL)
RCT_EXPORT_VIEW_PROPERTY(customUserAgent, NSString *)
RCT_EXPORT_VIEW_PROPERTY(percentVisibility, NSInteger *)

RCT_EXPORT_VIEW_PROPERTY(onAdLoadSuccess, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onAdLoadFail, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onEventChange, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onAdVisibleChange, RCTDirectEventBlock)

@end
