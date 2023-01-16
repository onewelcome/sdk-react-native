import Foundation
import React
import OneginiSDKiOS
import LocalAuthentication


protocol ConnectorToRNBridgeProtocol: NSObject {
  func sendBridgeEvent(eventName: OneWelcomeBridgeEvents, data: Any) -> Void
}

// Pin notification actions for RN Bridge
enum OneWelcomeBridgeEvents: String {
    case pinCreateNotification = "ONEWELCOME_PIN_CREATE_NOTIFICATION"
    case pinAuthNotification = "ONEWELCOME_PIN_AUTHENTICATION_NOTIFICATION"
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
        return [OneWelcomeBridgeEvents.pinAuthNotification.rawValue,OneWelcomeBridgeEvents.pinCreateNotification.rawValue, OneWelcomeBridgeEvents.fingerprintNotification.rawValue, OneWelcomeBridgeEvents.customRegistrationNotification.rawValue, OneWelcomeBridgeEvents.authWithOtpNotification.rawValue, OneWelcomeBridgeEvents.registrationNotification.rawValue]
    }

    func sendBridgeEvent(eventName: OneWelcomeBridgeEvents, data: Any) -> Void {
      self.sendEvent(withName: eventName.rawValue, body: data)
    }

    @objc
    override static func requiresMainQueueSetup() -> Bool {
        return true
    }
    
    func rejectWithError(_ reject: RCTPromiseRejectBlock, _ error: Error) {
        reject(String(error.code), error.localizedDescription, error)
    }
    
    func resolveResultOrRejectError(_ resolve: RCTPromiseResolveBlock, _ reject: RCTPromiseRejectBlock, _ result: Any?, _ error: Error?) {
        if let error = error {
            rejectWithError(reject, error)
        } else {
            resolve(result)
        }
    }
    
    func makeCompletionClosure(resolver resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock, resolveWithNil: Bool) -> (_ result: Any?, _ error: Error?) -> Void {
        return { (_ result: Any?, _ error: Error?) in
            if let error = error {
                self.rejectWithError(reject, error)
            } else {
                if (resolveWithNil) {
                    resolve(nil)
                } else {
                    resolve(result)
                }
            }
        }
    }
    
    func makeCompletionClosure(resolver resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> (_ error: Error?) -> Void {
        return { (_ error: Error?) in
            if let error = error {
                self.rejectWithError(reject, error)
            } else {
                resolve(nil)
            }
        }
    }

    @objc
    func startClient(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let completion = makeCompletionClosure(resolver: resolve, rejecter: reject, resolveWithNil: false)
        oneginiSDKStartup(completion: completion)
    }

    @objc
    func getRedirectUri(_ resolve: RCTPromiseResolveBlock, rejecter reject:RCTPromiseRejectBlock) -> Void {
        let redirectUri = ONGClient.sharedInstance().configModel.redirectURL

        resolve(["redirectUri": redirectUri])
    }

    @objc
    func getAccessToken(_ resolve: RCTPromiseResolveBlock, rejecter reject:RCTPromiseRejectBlock) -> Void {
        let accessToken = userClient.accessToken
        guard let accessToken = accessToken else {
            return rejectWithError(reject, WrapperError.noProfileAuthenticated)
        }
        resolve(accessToken)
    }

    @objc
    func getUserProfiles(_ resolve: RCTPromiseResolveBlock, rejecter reject:RCTPromiseRejectBlock) -> Void {
        let profiles = userClient.userProfiles()
        let result: NSMutableArray = []

        for profile in profiles {
            result.add(["id": profile.value(forKey: "profileId")])
        }

        resolve(result)
    }

    @objc
    func getAuthenticatedUserProfile(_ resolve: RCTPromiseResolveBlock, rejecter reject:RCTPromiseRejectBlock) -> Void {
        guard let authenticatedProfile = userClient.authenticatedUserProfile() else {
            return rejectWithError(reject, WrapperError.noProfileAuthenticated)
        }
        resolve(["id": authenticatedProfile.profileId])
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
            self.resolveResultOrRejectError(resolve, reject, ["id": userProfile?.profileId], error)
        }
    }

    @objc
    func submitCustomRegistrationAction(_ identityProviderId: String, token: String?,
                                        resolver resolve: @escaping RCTPromiseResolveBlock,
                                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let completion = makeCompletionClosure(resolver: resolve, rejecter: reject)
        bridgeConnector.toRegistrationConnector.registrationHandler.handleOTPCode(token, completion: completion)
    }

    
    @objc
    func deregisterUser(_ profileId: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile = userClient.userProfiles().first(where: { $0.profileId == profileId })
        guard let profile = profile else {
            return rejectWithError(reject, WrapperError.profileDoesNotExist)
        }
        let completion = makeCompletionClosure(resolver: resolve, rejecter: reject, resolveWithNil: true)
        userClient.deregisterUser(profile, completion: completion)
    }

    @objc
    func handleRegistrationCallback(_ url: String,
                                    resolver resolve: @escaping RCTPromiseResolveBlock,
                                    rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let urlOBject = URL(string: url)
        guard let urlOBject = urlOBject else {
            return rejectWithError(reject, WrapperError.malformedUrl)
        }
        let completion = makeCompletionClosure(resolver: resolve, rejecter: reject)
        bridgeConnector.toRegistrationConnector.registrationHandler.handleRedirectURL(urlOBject, completion: completion)
    }

    @objc
    func cancelBrowserRegistration(_ resolve: @escaping RCTPromiseResolveBlock,
                            rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let completion = makeCompletionClosure(resolver: resolve, rejecter: reject)
        bridgeConnector.toRegistrationConnector.registrationHandler.cancelBrowserRegistration(completion: completion)
    }
    
    @objc
    func cancelCustomRegistration(_ message: String, resolver resolve: @escaping RCTPromiseResolveBlock,
                            rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let completion = makeCompletionClosure(resolver: resolve, rejecter: reject)
        bridgeConnector.toRegistrationConnector.registrationHandler.cancelCustomRegistration(completion: completion)
    }

    @objc
    func cancelPinAuthentication(_ resolve: @escaping RCTPromiseResolveBlock,
                            rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let completion = makeCompletionClosure(resolver: resolve, rejecter: reject)
        bridgeConnector.toLoginHandler.cancelPinAuthentication(completion: completion)
    }

    @objc
    func cancelPinCreation(_ resolve: @escaping RCTPromiseResolveBlock,
                            rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let completion = makeCompletionClosure(resolver: resolve, rejecter: reject)
        bridgeConnector.toRegistrationConnector.registrationHandler.cancelPinCreation(completion: completion)
    }
    
    @objc
    func submitPin(_ flow: String, pin: String,
                         resolver resolve: @escaping RCTPromiseResolveBlock,
                         rejecter reject: @escaping RCTPromiseRejectBlock) {
        let completion = makeCompletionClosure(resolver: resolve, rejecter: reject)
        switch flow {
        case PinFlow.create.rawValue:
            return bridgeConnector.toRegistrationConnector.registrationHandler.handlePin(pin, completion: completion)
        case PinFlow.authentication.rawValue:
            return bridgeConnector.toLoginHandler.handlePin(pin, completion: completion)
        default:
            reject(String(WrapperError.parametersNotCorrect.code), "Incorrect pinflow supplied: \(flow)", WrapperError.parametersNotCorrect)
        }
    }

    @objc
    func changePin(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let completion = makeCompletionClosure(resolver: resolve, rejecter: reject)
        bridgeConnector.toChangePinHandler.onChangePinCalled(completion: completion)
    }

    @objc
    func authenticateUser(_ profileId: String, authenticatorId: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile = userClient.userProfiles().first(where: { $0.profileId == profileId })
        guard let profile = profile else {
            return rejectWithError(reject, WrapperError.profileDoesNotExist)
        }
        let authenticators = userClient.allAuthenticators(forUser: profile)
        let authenticator = authenticators.first(where: {$0.identifier == authenticatorId}) ?? userClient.preferredAuthenticator
        bridgeConnector.toLoginHandler.authenticateUser(profile, authenticator: authenticator) {
            (userProfile, error) -> Void in
            self.resolveResultOrRejectError(resolve, reject, ["userProfile": ["id": userProfile.profileId]], error)
        }
    }

    @objc
    func logout(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let completion = makeCompletionClosure(resolver: resolve, rejecter: reject, resolveWithNil: true)
        userClient.logoutUser(completion)
    }

    @objc
    func startSingleSignOn(_ url: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let _url = URL(string: url)
        let completion = makeCompletionClosure(resolver: resolve, rejecter: reject, resolveWithNil: true)
        bridgeConnector.toAppToWebHandler.signInAppToWeb(targetURL: _url, completion: completion)
    }

    @objc
    func authenticateUserImplicitly(_ profileId: String,
                                    scopes: [String],
                                    resolver resolve: @escaping RCTPromiseResolveBlock,
                                    rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile = userClient.userProfiles().first(where: { $0.profileId == profileId })
        guard let profile = profile else {
            return rejectWithError(reject, WrapperError.profileDoesNotExist)
        }
        let completion = makeCompletionClosure(resolver: resolve, rejecter: reject, resolveWithNil: true)
        bridgeConnector.toResourceHandler.authenticateImplicitly(profile, scopes: scopes, completion)
    }

    @objc
    func authenticateDeviceForResource(_ scopes: [String],
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let completion = makeCompletionClosure(resolver: resolve, rejecter: reject, resolveWithNil: true)
        bridgeConnector.toResourceHandler.authenticateDevice(scopes, completion)
    }

    @objc
    func resourceRequest(_ type: (String), details: (NSDictionary),
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {

        let type = ResourceRequestType(rawValue: type) ?? .Anonymous
        
        let completion = makeCompletionClosure(resolver: resolve, rejecter: reject, resolveWithNil: false)
        bridgeConnector.toResourceHandler.resourceRequest(type, details, completion)
    }

    @objc
    func enrollMobileAuthentication(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let completion = makeCompletionClosure(resolver: resolve, rejecter: reject)
        bridgeConnector.toMobileAuthConnector.mobileAuthHandler.enrollForMobileAuth(completion)
    }

    @objc
    func handleMobileAuthWithOtp(_ otpCode: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let completion = makeCompletionClosure(resolver: resolve, rejecter: reject)
        bridgeConnector.toMobileAuthConnector.mobileAuthHandler.handleOTPMobileAuth(otpCode, completion)
    }

    @objc
    func acceptMobileAuthConfirmation(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let completion = makeCompletionClosure(resolver: resolve, rejecter: reject)
        bridgeConnector.toMobileAuthConnector.mobileAuthHandler.handleMobileAuthConfirmation(accepted: true, completion: completion)
    }

    @objc
    func denyMobileAuthConfirmation(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let completion = makeCompletionClosure(resolver: resolve, rejecter: reject)
        bridgeConnector.toMobileAuthConnector.mobileAuthHandler.handleMobileAuthConfirmation(accepted: false, completion: completion)
    }

    // Authenticators management
    @objc
    func getAllAuthenticators(_ profileId: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile = userClient.userProfiles().first(where: { $0.profileId == profileId })
        guard let profile = profile else {
            return rejectWithError(reject, WrapperError.profileDoesNotExist)
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
            return rejectWithError(reject, WrapperError.profileDoesNotExist)
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
            return rejectWithError(reject, WrapperError.profileDoesNotExist)
        }
        let completion = makeCompletionClosure(resolver: resolve, rejecter: reject)
        bridgeConnector.toAuthenticatorsHandler.setPreferredAuthenticator(profile, authenticatorId, completion)
    }

    @objc
    func validatePinWithPolicy(_ pin: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let completion = makeCompletionClosure(resolver: resolve, rejecter: reject, resolveWithNil: true)
        userClient.validatePin(withPolicy: pin, completion: completion)
    }

    // Biometric
    // @todo rename methods
    @objc
    func registerFingerprintAuthenticator(_ profileId: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile = userClient.userProfiles().first(where: { $0.profileId == profileId})
        guard let profile = profile else {
            return rejectWithError(reject, WrapperError.profileDoesNotExist)
        }
        let completion = makeCompletionClosure(resolver: resolve, rejecter: reject, resolveWithNil: true)
        bridgeConnector.toAuthenticatorsHandler.registerAuthenticator(profile, ONGAuthenticatorType.biometric, completion)
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
