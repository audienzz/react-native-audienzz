#import <sys/utsname.h>
#import "ANVideoAdPlayer.h

@interface RCTAppNexusUtils : NSObject

+ (void)getUserAgent:(void(^)(NSString *userAgent)) completion;

@end
