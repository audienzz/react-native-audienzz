#import "RCTAppNexusUtils.h"
#if !(TARGET_OS_TV)
#import <WebKit/WebKit.h>
#endif

@implementation RCTAppNexusUtils
{
    #if !(TARGET_OS_TV)
        WKWebView *webView;
    #endif
}

+ (void)getUserAgent:(void(^)(NSString *userAgent)) completion
{
    __weak RCTAppNexusUtils *weakSelf = self;
    dispatch_async(dispatch_get_main_queue(), ^
    {
        __strong RCTAppNexusUtils *strongSelf = weakSelf;
        if (strongSelf) {
            __block WKWebView *webView = [[WKWebView alloc] init];

            [webView evaluateJavaScript:@"window.navigator.userAgent;" completionHandler:^(id _Nullable result, NSError * _Nullable error) {
                if (error) {
                }else{
                    completion([NSString stringWithFormat:@"%@", result]);
                }
                webView = nil;
            }];
        }
    });
}

@end
