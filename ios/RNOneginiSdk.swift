import Foundation
import React
import OneginiSDKiOS
import LocalAuthentication


protocol ConnectorToRNBridgeProtocol: NSObject {
  func sendBridgeEvent(eventName: OneWelcomeBridgeEvents, data: Any!) -> Void
}

// Pin notification actions for RN Bridge
enum OneWelcomeBridgeEvents : String {
    case pinNotification = "ONEWELCOME_PIN_NOTIFICATION"
    case fingerprintNotification = "ONEWELCOME_FINGERPRINT_NOTIFICATION"
    case customRegistrationNotification = "ONEWELCOME_CUSTOM_REGISTRATION_NOTIFICATION"
    case authWithOtpNotification = "ONEWELCOME_MOBILE_AUTH_OTP_NOTIFICATION"
}

@objc(RNOneginiSdk)
class RNOneginiSdk: RCTEventEmitter, ConnectorToRNBridgeProtocol {
    var bridgeConnector: BridgeConnector
    private var userClient: ONGUserClient {
        return ONGClient.sharedInstance().userClient
    }

    override init() {
        self.bridgeConnector = BridgeConnector()
        super.init()
        self.bridgeConnector.bridge = self
    }

    override func supportedEvents() -> [String]! {
        return [OneWelcomeBridgeEvents.pinNotification.rawValue, OneWelcomeBridgeEvents.fingerprintNotification.rawValue, OneWelcomeBridgeEvents.customRegistrationNotification.rawValue, OneWelcomeBridgeEvents.authWithOtpNotification.rawValue]
    }

    func sendBridgeEvent(eventName: OneWelcomeBridgeEvents, data: Any!) -> Void {
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
        let redirectUri = ONGClient.sharedInstance().configModel.redirectURL

        resolve([ "redirectUri" : redirectUri!])
    }

    @objc
    func getAccessToken(_ resolve: RCTPromiseResolveBlock, rejecter reject:RCTPromiseRejectBlock) -> Void {
        let accessToken = userClient.accessToken

        resolve(accessToken ?? nil)
    }

    @objc
    func getUserProfiles(_ resolve: RCTPromiseResolveBlock, rejecter reject:RCTPromiseRejectBlock) -> Void {
        let profiles = userClient.userProfiles()
        let result: NSMutableArray = []

        for profile in profiles {
            result.add(["profileId": profile.value(forKey: "profileId")])
        }

        resolve(result)
    }

    @objc
    func getAuthenticatedUserProfile(_ resolve: RCTPromiseResolveBlock, rejecter reject:RCTPromiseRejectBlock) -> Void {
        guard let authenticatedProfile = userClient.authenticatedUserProfile() else {
            let error = NSError(domain: ONGGenericErrorDomain, code: ONGGenericError.serverNotReachable.rawValue, userInfo: [NSLocalizedDescriptionKey : "No authenticated user profiles found."])
            reject("\(error.code)", error.localizedDescription, error)
            return
        }

        resolve(["profileId": authenticatedProfile.value(forKey: "profileId")])
    }

    @objc
    func getIdentityProviders(_ resolve: RCTPromiseResolveBlock, rejecter reject:RCTPromiseRejectBlock) -> Void {
        let profiles = userClient.identityProviders()
        let result: NSMutableArray  = []

        for profile in profiles {
            result.add(["id": profile.value(forKey: "identifier"), "name": profile.value(forKey: "name")])
        }

        resolve(result)
    }

    @objc
    func registerUser(_ identityProviderId: String?,
                      scopes: [String],
                      resolver resolve: @escaping RCTPromiseResolveBlock,
                      rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        var provider: ONGIdentityProvider? = nil

        if identityProviderId != nil {
            provider = userClient.identityProviders().first(where: { $0.value(forKey: "identifier") as? String == identityProviderId })!
        }

        bridgeConnector.toRegistrationConnector.registrationHandler.signUp(identityProvider: provider, scopes: scopes) {
          (_, userProfile, error) -> Void in

            if let error = error {
                reject("\(error.code)", error.localizedDescription, error)
            } else {
                resolve(["profileId" : userProfile?.profileId!])
            }

        }
    }

    @objc
    func submitCustomRegistrationAction(_ action: String, identityProviderId: String, token: String?) -> Void {
        bridgeConnector.toRegistrationConnector.handleCustomRegistrationAction(action, identityProviderId, token)
    }

    @objc
    func deregisterUser(_ profileId: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile = userClient.userProfiles().first(where: { $0.value(forKey: "profileId") as! String == profileId })!

        userClient.deregisterUser(profile) {
            (result: Bool, error) -> Void in

            if let error = error {
                reject("\(error.code)", error.localizedDescription, error)
              } else {
                resolve(true)
              }
        }
    }

    @objc
    func handleRegistrationCallback(_ url: String) -> Void {
        bridgeConnector.toRegistrationConnector.registrationHandler.processRedirectURL(url: URL(string: url)!)
    }

    @objc
    func cancelRegistration() -> Void {
        bridgeConnector.toRegistrationConnector.registrationHandler.cancelRegistration()
    }

    @objc
    func submitPinAction(_ flow: String, action: String, pin: String) -> Void {
        switch flow {
        case PinFlow.Create.rawValue:
            bridgeConnector.toRegistrationConnector.registrationHandler.handlePinAction(pin, action: action)
        case PinFlow.Authentication.rawValue:
            bridgeConnector.toLoginHandler.handlePinAction(pin, action: action)
            return
        default:
            return
        }
    }

    @objc
    func changePin(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        bridgeConnector.toChangePinHandler.onChangePinCalled() {
            (_, error) -> Void in

            if let error = error {
                reject("\(error.code)", error.localizedDescription, error)
            } else {
                resolve(true)
            }
        }
    }

    @objc
    func authenticateUser(_ profileId: String, authenticatorId authenticator: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile = userClient.userProfiles().first(where: { $0.value(forKey: "profileId") as! String == profileId })!
        let authenticator = userClient.preferredAuthenticator
        
        bridgeConnector.toLoginHandler.authenticateUser(profile, authenticator: authenticator) {
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
        userClient.logoutUser { _, error in
            if let error = error {
                reject("\(error.code)", error.localizedDescription, error)
              } else {
                resolve(true)
              }
        }
    }

    @objc
    func startSingleSignOn(_ url: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let _url = URL(string: url)

        bridgeConnector.toAppToWebHandler.signInAppToWeb(targetURL: _url, completion: { (result, error) in
            if let error = error {
                reject("\(error.code)", error.localizedDescription, error)
            } else {
                resolve(result)
            }
        })
    }

    @objc
    func authenticateUserImplicitly(_ profileId: String,
                                    scopes: [String],
                                    resolver resolve: @escaping RCTPromiseResolveBlock,
                                    rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile = userClient.userProfiles().first(where: { $0.value(forKey: "profileId") as! String == profileId })!

        bridgeConnector.toResourceHandler.authenticateImplicitly(profile, scopes: scopes) {
            (success, error) -> Void in
            if let error = error {
                reject("\(error.code)", error.localizedDescription, error)
              } else {
                resolve(true)
              }
        }
    }

    @objc
    func authenticateDeviceForResource(_ scopes: [String],
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        bridgeConnector.toResourceHandler.authenticateDevice(scopes) {
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
            (data: String?, error) -> Void in

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
    func handleMobileAuthWithOtp(_ otpCode: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        bridgeConnector.toMobileAuthConnector.mobileAuthHandler.handleOTPMobileAuth(otpCode) {
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
        bridgeConnector.toMobileAuthConnector.mobileAuthHandler.handleMobileAuthConfirmation(accepted: true)
        resolve(true)
    }

    @objc
    func denyMobileAuthConfirmation(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        bridgeConnector.toMobileAuthConnector.mobileAuthHandler.handleMobileAuthConfirmation(accepted: false)
        resolve(true)
    }

    // Authenticators management
    @objc
    func getAllAuthenticators(_ profileId: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile = userClient.userProfiles().first(where: { $0.value(forKey: "profileId") as! String == profileId })!

        let allAuthenticators: Array<ONGAuthenticator> = bridgeConnector.toAuthenticatorsHandler.getAuthenticatorsListForUserProfile(profile)

        let result: NSMutableArray = []

        for authenticator in allAuthenticators {
            result.add(["id": authenticator.identifier, "name": authenticator.name, "type": authenticator.type, "isRegistered": authenticator.isRegistered, "isPreferred": authenticator.isPreferred])
        }

        resolve(result)
    }

    @objc
    func getRegisteredAuthenticators(_ profileId: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile = userClient.userProfiles().first(where: { $0.value(forKey: "profileId") as! String == profileId })!

        let registeredAuthenticators: Array<ONGAuthenticator> = bridgeConnector.toAuthenticatorsHandler.getAuthenticatorsListForUserProfile(profile).filter {$0.isRegistered == true}

        let result: NSMutableArray = []

        for authenticator in registeredAuthenticators {
            result.add(["id": authenticator.identifier, "name": authenticator.name, "type": authenticator.type, "isRegistered": authenticator.isRegistered, "isPreferred": authenticator.isPreferred])
        }

        resolve(result)
    }

    @objc
    func setPreferredAuthenticator(_ profileId: String, authenticatorId: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile = userClient.userProfiles().first(where: { $0.profileId == profileId })!

        bridgeConnector.toAuthenticatorsHandler.setPreferredAuthenticator(profile, authenticatorId) {
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
    func registerFingerprintAuthenticator(_ profileId: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile = userClient.userProfiles().first(where: { $0.profileId == profileId})!

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
    func deregisterFingerprintAuthenticator(_ profileId: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile = userClient.userProfiles().first(where: { $0.profileId == profileId })!

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
    func isFingerprintAuthenticatorRegistered(_ profileId: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile = userClient.userProfiles().first(where: { $0.profileId == profileId })!
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
