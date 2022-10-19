import Foundation
import React
import OneginiSDKiOS
import LocalAuthentication


protocol ConnectorToRNBridgeProtocol: NSObject {
  func sendBridgeEvent(eventName: OneWelcomeBridgeEvents, data: Any) -> Void
}

// Pin notification actions for RN Bridge
enum OneWelcomeBridgeEvents: String {
    case pinNotification = "ONEWELCOME_PIN_NOTIFICATION"
    case fingerprintNotification = "ONEWELCOME_FINGERPRINT_NOTIFICATION"
    case customRegistrationNotification = "ONEWELCOME_CUSTOM_REGISTRATION_NOTIFICATION"
    case authWithOtpNotification = "ONEWELCOME_MOBILE_AUTH_OTP_NOTIFICATION"
    case registrationNotification = "ONEWELCOME_REGISTRATION_NOTIFICATION"
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

    override func supportedEvents() -> [String] {
        return [OneWelcomeBridgeEvents.pinNotification.rawValue, OneWelcomeBridgeEvents.fingerprintNotification.rawValue, OneWelcomeBridgeEvents.customRegistrationNotification.rawValue, OneWelcomeBridgeEvents.authWithOtpNotification.rawValue,
            OneWelcomeBridgeEvents.registrationNotification.rawValue,
        ]
    }

    func sendBridgeEvent(eventName: OneWelcomeBridgeEvents, data: Any) -> Void {
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
                resolve(nil)
            }
        }
    }

    @objc
    func getRedirectUri(_ resolve: RCTPromiseResolveBlock, rejecter reject:RCTPromiseRejectBlock) -> Void {
        let redirectUri = ONGClient.sharedInstance().configModel.redirectURL

        resolve([ "redirectUri" : redirectUri])
    }

    @objc
    func getAccessToken(_ resolve: RCTPromiseResolveBlock, rejecter reject:RCTPromiseRejectBlock) -> Void {
        let accessToken = userClient.accessToken
        guard let accessToken = accessToken else {
            reject(String(WrapperError.noProfileAuthenticated.code), WrapperError.noProfileAuthenticated.localizedDescription, WrapperError.noProfileAuthenticated)
            return
        }
        resolve(accessToken)
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
        //TODO: Fix this error, clearly this errorcode is wrong
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
            provider = userClient.identityProviders().first(where: { $0.value(forKey: "identifier") as? String == identityProviderId })
        }

        bridgeConnector.toRegistrationConnector.registrationHandler.signUp(identityProvider: provider, scopes: scopes) {
          (_, userProfile, error) -> Void in

            if let error = error {
                reject("\(error.code)", error.localizedDescription, error)
            } else {
                resolve(["profileId" : userProfile?.profileId])
            }

        }
    }

    @objc
    func submitCustomRegistrationAction(_ action: String, identityProviderId: String, token: String?,
                                        resolver resolve: @escaping RCTPromiseResolveBlock,
                                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        switch action {
            case CustomRegistrationAction.provide.rawValue:
                bridgeConnector.toRegistrationConnector.registrationHandler.processOTPCode(code: token)
                break
            case CustomRegistrationAction.cancel.rawValue:
                bridgeConnector.toRegistrationConnector.registrationHandler.cancelCustomRegistration()
                break
            default:
                reject(String(WrapperError.parametersNotCorrect.code), "Incorrect customAction supplied: \(action)", WrapperError.parametersNotCorrect)
                break
        }
        bridgeConnector.toRegistrationConnector.handleCustomRegistrationAction(action, identityProviderId, token)
        resolve(nil)
    }

    
    @objc
    func deregisterUser(_ profileId: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile = userClient.userProfiles().first(where: { $0.profileId == profileId })
        guard let profile = profile else {
            reject(String(WrapperError.profileDoesNotExist.code), WrapperError.profileDoesNotExist.localizedDescription, WrapperError.profileDoesNotExist)
            return
        }
        userClient.deregisterUser(profile) {
            (result: Bool, error) -> Void in

            if let error = error {
                reject("\(error.code)", error.localizedDescription, error)
              } else {
                resolve(nil)
              }
        }
    }

    @objc
    func handleRegistrationCallback(_ url: String,
                                    resolver resolve: @escaping RCTPromiseResolveBlock,
                                    rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let urlOBject = URL(string: url)
        guard let urlOBject = urlOBject else {
            reject(String(WrapperError.malformedUrl.code), WrapperError.malformedUrl.description, WrapperError.malformedUrl)
            return
        }

        bridgeConnector.toRegistrationConnector.registrationHandler.processRedirectURL(url: urlOBject)
        resolve(nil)
    }

    @objc
    func cancelRegistration(_ resolve: @escaping RCTPromiseResolveBlock,
                            rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        bridgeConnector.toRegistrationConnector.registrationHandler.cancelRegistration()
        resolve(nil)
    }
    
    func mapStringToPinAction(action: String) -> PinAction? {
        switch action {
        case PinAction.provide.rawValue:
            return .provide
        case PinAction.cancel.rawValue:
            return .cancel
        default:
            return nil
        }
    }
    
    @objc
    func submitPinAction(_ flow: String, action: String, pin: String,
                         resolver resolve: @escaping RCTPromiseResolveBlock,
                         rejecter reject: @escaping RCTPromiseRejectBlock) {
        if let pinAction = mapStringToPinAction(action: action) {
            switch flow {
            case PinFlow.create.rawValue:
                bridgeConnector.toRegistrationConnector.registrationHandler.handlePinAction(pin, action: pinAction)
            case PinFlow.authentication.rawValue:
                bridgeConnector.toLoginHandler.handlePinAction(pin, action: pinAction)
            default:
                reject(String(WrapperError.parametersNotCorrect.code), "Incorrect pinflow supplied: \(flow)", WrapperError.parametersNotCorrect)
                break
            }
        } else {
            reject(String(WrapperError.parametersNotCorrect.code), "Incorrect action supplied: \(action)", WrapperError.parametersNotCorrect)
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
    func authenticateUser(_ profileId: String, authenticatorId: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile = userClient.userProfiles().first(where: { $0.profileId == profileId })
        guard let profile = profile else {
            reject(String(WrapperError.profileDoesNotExist.code), WrapperError.profileDoesNotExist.localizedDescription, WrapperError.profileDoesNotExist)
            return
        }
        let authenticators = userClient.allAuthenticators(forUser: profile)
        let authenticator = authenticators.first(where: {$0.identifier == authenticatorId}) ?? userClient.preferredAuthenticator
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
        let profile = userClient.userProfiles().first(where: { $0.profileId == profileId })
        guard let profile = profile else {
            reject(String(WrapperError.profileDoesNotExist.code), WrapperError.profileDoesNotExist.localizedDescription, WrapperError.profileDoesNotExist)
            return
        }

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
        let profile = userClient.userProfiles().first(where: { $0.profileId == profileId })
        guard let profile = profile else {
            reject(String(WrapperError.profileDoesNotExist.code), WrapperError.profileDoesNotExist.localizedDescription, WrapperError.profileDoesNotExist)
            return
        }
        
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
        let profile = userClient.userProfiles().first(where: { $0.profileId == profileId })
        guard let profile = profile else {
            reject(String(WrapperError.profileDoesNotExist.code), WrapperError.profileDoesNotExist.localizedDescription, WrapperError.profileDoesNotExist)
            return
        }

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
        let profile = userClient.userProfiles().first(where: { $0.profileId == profileId })
        guard let profile = profile else {
            reject(String(WrapperError.profileDoesNotExist.code), WrapperError.profileDoesNotExist.localizedDescription, WrapperError.profileDoesNotExist)
            return
        }

        bridgeConnector.toAuthenticatorsHandler.setPreferredAuthenticator(profile, authenticatorId) {
            (_ , error) -> Void in

            if let error = error {
                reject("\(error.code)", error.localizedDescription, error)
              } else {
                resolve(nil)
              }
        }
    }

    @objc
    func validatePinWithPolicy(_ pin: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        userClient.validatePin(withPolicy: pin) { (_ , error) -> Void in

            if let error = error {
                reject("\(error.code)", error.localizedDescription, error)
            } else {
                resolve(nil)
            }
        }
    }

    // Biometric
    // @todo rename methods
    @objc
    func registerFingerprintAuthenticator(_ profileId: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile = userClient.userProfiles().first(where: { $0.profileId == profileId})
        guard let profile = profile else {
            reject(String(WrapperError.profileDoesNotExist.code), WrapperError.profileDoesNotExist.localizedDescription, WrapperError.profileDoesNotExist)
            return
        }
        bridgeConnector.toAuthenticatorsHandler.registerAuthenticator(profile, ONGAuthenticatorType.biometric) {
            (_ , error) -> Void in

            if let error = error {
                reject("\(error.code)", error.localizedDescription, error)
              } else {
                resolve(nil)
              }
        }
    }

    @objc
    func deregisterFingerprintAuthenticator(_ profileId: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile = userClient.userProfiles().first(where: { $0.profileId == profileId })
        guard let profile = profile else {
            reject(String(WrapperError.profileDoesNotExist.code), WrapperError.profileDoesNotExist.localizedDescription, WrapperError.profileDoesNotExist)
            return
        }

        bridgeConnector.toAuthenticatorsHandler.deregisterAuthenticator(profile, ONGAuthenticatorType.biometric) {
            (_ , error) -> Void in

            if let error = error {
                reject("\(error.code)", error.localizedDescription, error)
              } else {
                resolve(nil)
              }
        }
    }

    @objc
    func isFingerprintAuthenticatorRegistered(_ profileId: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile = userClient.userProfiles().first(where: { $0.profileId == profileId })
        guard let profile = profile else {
            reject(String(WrapperError.profileDoesNotExist.code), WrapperError.profileDoesNotExist.localizedDescription, WrapperError.profileDoesNotExist)
            return
        }
        let isAuthenticatorRegistered = bridgeConnector.toAuthenticatorsHandler.isAuthenticatorRegistered(ONGAuthenticatorType.biometric, profile)

        resolve(isAuthenticatorRegistered)
    }

    // Service methods
    private func oneginiSDKStartup(completion: @escaping (Bool, Error?) -> Void) {
        ONGClientBuilder().build()
        ONGClient.sharedInstance().start { result, error in
            if let error = error {
                completion(result, error)
            } else {
                completion(result, nil)
            }
        }
    }
}
