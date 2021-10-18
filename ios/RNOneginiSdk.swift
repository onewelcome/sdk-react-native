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
                reject("\(error.code)", error.localizedDescription, error)
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
    func getAuthenticatedUserProfile(_ resolve: RCTPromiseResolveBlock, rejecter reject:RCTPromiseRejectBlock) -> Void {
        guard let authenticatedProfile = ONGClient.sharedInstance().userClient.authenticatedUserProfile() else {
            let error = NSError(domain: ONGGenericErrorDomain, code: ONGGenericError.serverNotReachable.rawValue, userInfo: [NSLocalizedDescriptionKey : "No authenticated user profiles found."])
            reject("\(error.code)", error.localizedDescription, error);
            return;
        }

        resolve(["profileId": authenticatedProfile.value(forKey: "profileId")])
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

            if let error = error {
                reject("\(error.code)", error.localizedDescription, error)
            } else {
                resolve(["profileId" : userProfile?.profileId!])
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
                reject("\(error.code)", error.localizedDescription, error)
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
                reject("\(error.code)", error.localizedDescription, error)
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
                reject("\(error.code)", error.localizedDescription, error)
              } else {
                resolve(true)
              }
        }
    }

    @objc
    func logout(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        ONGClient.sharedInstance().userClient.logoutUser { _, error in
            if let error = error {
                reject("\(error.code)", error.localizedDescription, error)
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
                reject("\(error.code)", error.localizedDescription, error)
            } else {
                resolve(result)
            }
        })
    }

    @objc
    func authenticateUserImplicitly(_ profileId: (NSString),
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile: ONGUserProfile = ONGClient.sharedInstance().userClient.userProfiles().first(where: { $0.value(forKey: "profileId") as! NSObject == profileId })!;

        bridgeConnector.toResourceHandler.authenticateImplicitly(profile) {
            (success, error) -> Void in
            if let error = error {
                reject("\(error.code)", error.localizedDescription, error)
              } else {
                resolve(true)
              }
        }
    }

    @objc
    func authenticateDevice(_ resourcePath: (NSString),
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        bridgeConnector.toResourceHandler.authenticateDevice(resourcePath) {
            (success, error) -> Void in
            if let error = error {
                reject("\(error.code)", error.localizedDescription, error)
              } else {
                resolve(true)
              }
        }
    }

    @objc
    func resourceRequest(_ type: (String), details: (NSDictionary),
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {

        let type = ResourceRequestType(rawValue: type) ?? .Anonymous

        bridgeConnector.toResourceHandler.resourceRequest(type, details) {
            (data: [String: Any]?, error) -> Void in

            if let error = error {
                reject("\(error.code)", error.localizedDescription, error)
              } else {
                resolve(data)
              }
        }
    }

    @objc
    func enrollMobileAuthentication(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        bridgeConnector.toMobileAuthConnector.mobileAuthHandler.enrollForMobileAuth { _, error in
            if let error = error {
                reject("\(error.code)", error.localizedDescription, error)
              } else {
                resolve(true)
              }
        }
    }

    @objc
    func handleMobileAuthWithOtp(_ otpCode: (NSString),
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        bridgeConnector.toMobileAuthConnector.mobileAuthHandler.handleOTPMobileAuth(otpCode as String) {
            (_ , error) -> Void in

            if let error = error {
                reject("\(error.code)", error.localizedDescription, error)
              } else {
                resolve(true)
              }
        }
    }

    @objc
    func acceptMobileAuthConfirmation(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        bridgeConnector.toMobileAuthConnector.mobileAuthHandler.handleMobileAuthConfirmation(cancelled: false)
        resolve(true)
    }

    @objc
    func denyMobileAuthConfirmation(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        bridgeConnector.toMobileAuthConnector.mobileAuthHandler.handleMobileAuthConfirmation(cancelled: true)
        resolve(true)
    }

    // Authenticators management
    @objc
    func getAllAuthenticators(_ profileId: (NSString),
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile: ONGUserProfile = ONGClient.sharedInstance().userClient.userProfiles().first(where: { $0.value(forKey: "profileId") as! NSObject == profileId })!;

        let allAuthenticators: Array<ONGAuthenticator> = bridgeConnector.toAuthenticatorsHandler.getAuthenticatorsListForUserProfile(profile)

        var result: NSMutableArray  = []

        for authenticator in allAuthenticators {
            result.add(["id": authenticator.identifier, "name": authenticator.name, "type": authenticator.type, "isRegistered": authenticator.isRegistered, "isPreferred": authenticator.isPreferred])
        }

        resolve(result)
    }

    @objc
    func getRegisteredAuthenticators(_ profileId: (NSString),
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile: ONGUserProfile = ONGClient.sharedInstance().userClient.userProfiles().first(where: { $0.value(forKey: "profileId") as! NSObject == profileId })!;

        let registeredAuthenticators: Array<ONGAuthenticator> = bridgeConnector.toAuthenticatorsHandler.getAuthenticatorsListForUserProfile(profile).filter {$0.isRegistered == true}

        var result: NSMutableArray  = []

        for authenticator in registeredAuthenticators {
            result.add(["id": authenticator.identifier, "name": authenticator.name, "type": authenticator.type, "isRegistered": authenticator.isRegistered, "isPreferred": authenticator.isPreferred])
        }

        resolve(result)
    }

    @objc
    func setPreferredAuthenticator(_ profileId: (NSString), authenticatorId: (NSString),
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile: ONGUserProfile = ONGClient.sharedInstance().userClient.userProfiles().first(where: { $0.profileId as! NSObject == profileId })!;

        bridgeConnector.toAuthenticatorsHandler.setPreferredAuthenticator(profile, authenticatorId as String) {
            (_ , error) -> Void in

            if let error = error {
                reject("\(error.code)", error.localizedDescription, error)
              } else {
                resolve(true)
              }
        }
    }


    // Biometric
    // @todo rename methods
    @objc
    func registerFingerprintAuthenticator(_ profileId: (NSString),
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile: ONGUserProfile = ONGClient.sharedInstance().userClient.userProfiles().first(where: { $0.profileId as! NSObject == profileId })!;

        bridgeConnector.toAuthenticatorsHandler.registerAuthenticator(profile, ONGAuthenticatorType.biometric) {
            (_ , error) -> Void in

            if let error = error {
                reject("\(error.code)", error.localizedDescription, error)
              } else {
                resolve(true)
              }
        }
    }

    @objc
    func deregisterFingerprintAuthenticator(_ profileId: (NSString),
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile: ONGUserProfile = ONGClient.sharedInstance().userClient.userProfiles().first(where: { $0.profileId as! NSObject == profileId })!;

        bridgeConnector.toAuthenticatorsHandler.deregisterAuthenticator(profile, ONGAuthenticatorType.biometric) {
            (_ , error) -> Void in

            if let error = error {
                reject("\(error.code)", error.localizedDescription, error)
              } else {
                resolve(true)
              }
        }
    }

    @objc
    func isFingerprintAuthenticatorRegistered(_ profileId: (NSString),
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile: ONGUserProfile = ONGClient.sharedInstance().userClient.userProfiles().first(where: { $0.value(forKey: "profileId") as! NSObject == profileId })!;
        let isAuthenticatorRegistered = bridgeConnector.toAuthenticatorsHandler.isAuthenticatorRegistered(ONGAuthenticatorType.biometric, profile)

        resolve(isAuthenticatorRegistered)
    }

    // Service methods
    private func oneginiSDKStartup(completion: @escaping (Bool, NSError?) -> Void) {
        ONGClientBuilder().build()
        ONGClient.sharedInstance().start { result, error in
            if let error = error {
                completion(result, error as NSError)
            } else {
                completion(result, nil)
            }
        }
    }
}
