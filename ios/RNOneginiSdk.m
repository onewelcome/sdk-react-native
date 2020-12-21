#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>


@interface RCT_EXTERN_MODULE(RNOneginiSdk, RCTEventEmitter)
RCT_EXTERN_METHOD(supportedEvents)
RCT_EXTERN_METHOD(startClient:(RCTResponseSenderBlock *)callback)
RCT_EXTERN_METHOD(getRedirectUri:(RCTResponseSenderBlock *)callback)
RCT_EXTERN_METHOD(getUserProfiles:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(getIdentityProviders:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(registerUser:(NSString *)identityProviderId callback:(RCTResponseSenderBlock *)callback)
RCT_EXTERN_METHOD(deregisterUser:(NSString *)profileId resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(handleRegistrationCallback:(NSString *)url)
RCT_EXTERN_METHOD(cancelRegistration)
RCT_EXTERN_METHOD(submitPinAction:(NSString *)flow action:(NSString *)action pin:(NSString *)pin)
RCT_EXTERN_METHOD(changePin:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(authenticateUser:(NSString *)profileId resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(logout:(RCTResponseSenderBlock *)callback)
@end
