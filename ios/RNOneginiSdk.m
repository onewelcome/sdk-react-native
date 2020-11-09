#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>


@interface RCT_EXTERN_MODULE(RNOneginiSdk, RCTEventEmitter)
RCT_EXTERN_METHOD(supportedEvents)
RCT_EXTERN_METHOD(startClient:(RCTResponseSenderBlock *)callback)
RCT_EXTERN_METHOD(getRedirectUri:(RCTResponseSenderBlock *)callback)
RCT_EXTERN_METHOD(registerUser:(NSString *)identityProviderId callback:(RCTResponseSenderBlock *)callback)
RCT_EXTERN_METHOD(handleRegistrationCallback:(NSString *)url)
RCT_EXTERN_METHOD(cancelRegistration)
RCT_EXTERN_METHOD(submitPinAction:(NSString *)action isCreatePinFlow:(nonnull NSNumber *)isCreatePinFlow pin:(NSString *)pin)
@end
