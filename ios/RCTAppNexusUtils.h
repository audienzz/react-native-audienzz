#import <sys/utsname.h>
#import <AppNexusSDK/ANVideoAdPlayer.h>

@interface RCTAppNexusUtils : NSObject

+ (void)getUserAgent:(void(^)(NSString *userAgent)) completion;

@end
