import Foundation
import React
import OneginiSDKiOS


protocol ConnectorToRNBridgeProtocol: NSObject {
  func sendBridgeEvent(eventName: OneginiBridgeEvents, data: Any!) -> Void
}

// Pin notification actions for RN Bridge
enum OneginiBridgeEvents : String {
    case pinNotification = "ONEGINI_PIN_NOTIFICATION"
    case fingerprintNotification = "ONEGINI_FINGERPRINT_NOTIFICATION"
    case customRegistrationNotification = "ONEGINI_CUSTOM_REGISTRATION_NOTIFICATION"
    case authWithOtpNotification = "ONEGINI_MOBILE_AUTH_OTP_NOTIFICATION"
}

@objc(RNOneginiSdk)
class RNOneginiSdk: RCTEventEmitter, ConnectorToRNBridgeProtocol {

  var bridgeConnector: BridgeConnector

    override init() {
        self.bridgeConnector = BridgeConnector()
        super.init()
        self.bridgeConnector.bridge = self
    }

    override func supportedEvents() -> [String]! {
        return [OneginiBridgeEvents.pinNotification.rawValue, OneginiBridgeEvents.fingerprintNotification.rawValue, OneginiBridgeEvents.customRegistrationNotification.rawValue, OneginiBridgeEvents.authWithOtpNotification.rawValue]
    }
    
    func sendBridgeEvent(eventName: OneginiBridgeEvents, data: Any!) -> Void {
      self.sendEvent(withName: eventName.rawValue, body: data)
    }

    @objc
    override static func requiresMainQueueSetup() -> Bool {
        return true
    }

    @objc
    func startClient(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        self.oneginiSDKStartup { _, error in
            if let error = error {
                reject(nil, error.errorDescription, nil)
            } else {
                resolve(true)
            }
        }
    }

    @objc
    func getRedirectUri(_ resolve: RCTPromiseResolveBlock, rejecter reject:RCTPromiseRejectBlock) -> Void {
        let redirectUri = ONGClient.sharedInstance().configModel.redirectURL;

        resolve([ "redirectUri" : redirectUri!])
    }
    
    @objc
    func getAccessToken(_ resolve: RCTPromiseResolveBlock, rejecter reject:RCTPromiseRejectBlock) -> Void {
        let accessToken = ONGClient.sharedInstance().userClient.accessToken;

        resolve(accessToken ?? nil)
    }

    @objc
    func getUserProfiles(_ resolve: RCTPromiseResolveBlock, rejecter reject:RCTPromiseRejectBlock) -> Void {
        let profiles = ONGClient.sharedInstance().userClient.userProfiles()
        var result: NSMutableArray  = []

        for profile in profiles {
            result.add(["profileId": profile.value(forKey: "profileId")])
        }

        resolve(result)
    }

    @objc
    func getIdentityProviders(_ resolve: RCTPromiseResolveBlock, rejecter reject:RCTPromiseRejectBlock) -> Void {
        let profiles = ONGClient.sharedInstance().userClient.identityProviders()
        var result: NSMutableArray  = []

        for profile in profiles {
            result.add(["id": profile.value(forKey: "identifier"), "name": profile.value(forKey: "name")])
        }

        resolve(result)
    }

    @objc
    func registerUser(_ identityProviderId: (NSString)?,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        var provider: ONGIdentityProvider? = nil

        if(identityProviderId != nil) {
            provider =         ONGClient.sharedInstance().userClient.identityProviders().first(where: { $0.value(forKey: "identifier") as? NSString == identityProviderId })!;
        }

        bridgeConnector.toRegistrationConnector.registrationHandler.signUp(identityProvider: provider) {
          (_, userProfile, error) -> Void in

            if let userProfile = userProfile {
                resolve(["profileId" : userProfile.profileId!])
            } else {
                reject(nil, error?.errorDescription ?? "Unexpected Error.", nil)
            }

        }
    }

    @objc
    func submitCustomRegistrationAction(_ action: (NSString), identityProviderId: (NSString), token: (NSString)?) -> Void {
        bridgeConnector.toRegistrationConnector.handleCustomRegistrationAction(action, identityProviderId, token)
    }

    @objc
    func deregisterUser(_ profileId: (NSString),
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile: ONGUserProfile = ONGClient.sharedInstance().userClient.userProfiles().first(where: { $0.value(forKey: "profileId") as! NSObject == profileId })!;

        ONGClient.sharedInstance().userClient.deregisterUser(profile) {
            (result: Bool, error) -> Void in

            if let error = error {
                let mappedError = ErrorMapper().mapError(error);
                reject(nil, mappedError.errorDescription, nil)
              } else {
                resolve(true)
              }
        }
    }

    @objc
    func handleRegistrationCallback(_ url: (NSString)) -> Void {
        bridgeConnector.toRegistrationConnector.registrationHandler.processRedirectURL(url: URL(string: url as String)!)
    }

    @objc
    func cancelRegistration() -> Void {
        bridgeConnector.toRegistrationConnector.registrationHandler.cancelRegistration()
    }

    @objc
    func submitPinAction(_ flow: (NSString), action: (NSString), pin: (NSString)) -> Void {
        bridgeConnector.toPinHandlerConnector.handlePinAction(flow, action, pin)
    }

    @objc
    func changePin(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        bridgeConnector.toPinHandlerConnector.pinHandler.onChangePinCalled() {
            (_, error) -> Void in

            if let error = error {
                reject(nil, error.errorDescription, nil)
            } else {
                resolve(true)
            }
        }
    }

    @objc
    func authenticateUser(_ profileId: (NSString),
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile: ONGUserProfile = ONGClient.sharedInstance().userClient.userProfiles().first(where: { $0.value(forKey: "profileId") as! NSObject == profileId })!;

        bridgeConnector.toLoginHandler.authenticateUser(profile) {
            (userProfile, error) -> Void in

            if let error = error {
                reject(nil, error.errorDescription, nil)
              } else {
                resolve(true)
              }
        }
    }

    @objc
    func logout(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        ONGClient.sharedInstance().userClient.logoutUser { _, error in
            if let error = error {
                let mappedError = ErrorMapper().mapError(error);
                reject(nil, mappedError.errorDescription, nil)
              } else {
                resolve(true)
              }
        }
    }

    @objc
    func startSingleSignOn(_ url: (NSString),
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let _url = URL(string: url as String)

        bridgeConnector.toAppToWebHandler.signInAppToWeb(targetURL: _url, completion: { (result, error) in
            if let error = error {
                reject(nil, error.errorDescription, nil)
            } else {
                resolve(result)
            }
        })
    }
    
    @objc
    func getImplicitDataResource(_ profileId: (NSString),
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile: ONGUserProfile = ONGClient.sharedInstance().userClient.userProfiles().first(where: { $0.value(forKey: "profileId") as! NSObject == profileId })!;

        bridgeConnector.toResourceFetchHandler.getImplicitData(profile) {
            (result: String?, error) -> Void in

            if let error = error {
                reject(nil, error.errorDescription, nil)
              } else {
                resolve(result)
              }
        }
    }
    
    @objc
    func getAppDetailsResource(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        
        bridgeConnector.toResourceFetchHandler.getAppDetails() {
            (result: ApplicationDetails?, error) -> Void in

            if let error = error {
                reject(nil, error.errorDescription, nil)
              } else {
                resolve(["applicationIdentifier": result?.applicationIdentifier, "applicationVersion": result?.applicationVersion,
                         "applicationPlatform": result?.applicationPlatform,])
              }
        }
    }
    
    @objc
    func getDeviceListResource(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        
        bridgeConnector.toResourceFetchHandler.getDeviceList() {
            (fetchResult: Devices?, error) -> Void in

            if let error = error {
                reject(nil, error.errorDescription, nil)
              } else {
                var result: NSMutableArray  = []

                for device in fetchResult?.devices ?? [] {
                    result.add(["id" : device.id, "name" : device.name, "application" : device.application, "platform" : device.platform])
                }
                resolve(["devices" : result])
              }
        }
    }
    
    private func oneginiSDKStartup(completion: @escaping (Bool, SdkError?) -> Void) {
        ONGClientBuilder().build()
        ONGClient.sharedInstance().start { result, error in
            if let error = error {
                let mappedError = ErrorMapper().mapError(error)
                completion(result, mappedError)
            } else {
                completion(result, nil)
            }
        }
    }
}
