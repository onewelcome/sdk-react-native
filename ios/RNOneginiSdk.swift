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
    
    func resolveWithResultOrError(_ resolve: RCTPromiseResolveBlock, _ reject: RCTPromiseRejectBlock, _ result: Any?, _ error: Error?) {
        if let error = error {
            self.rejectWithError(reject, error)
        } else {
            resolve(result)
        }
    }
    
    func resolveWithResultOrErrorClosure(_ resolve: @escaping RCTPromiseResolveBlock, _ reject: @escaping RCTPromiseRejectBlock) -> (_ result: Any, _ error: Error?) -> Void {
        return { (_ result: Any?, _ error: Error?) in
            self.resolveWithResultOrError(resolve, reject, result, error)
        }
    }

    @objc
    func startClient(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        self.oneginiSDKStartup(completion: resolveWithResultOrErrorClosure(resolve, reject))
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
            return self.rejectWithError(reject, WrapperError.noProfileAuthenticated)
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
            return self.rejectWithError(reject, WrapperError.noProfileAuthenticated)
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
            self.resolveWithResultOrError(resolve, reject, ["id": userProfile?.profileId], error)
        }
    }

    @objc
    func submitCustomRegistrationAction(_ identityProviderId: String, token: String?,
                                        resolver resolve: @escaping RCTPromiseResolveBlock,
                                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        do {
            try bridgeConnector.toRegistrationConnector.registrationHandler.processOTPCode(token)
            resolve(nil)
        } catch {
            self.rejectWithError(reject, error)
        }
    }

    
    @objc
    func deregisterUser(_ profileId: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile = userClient.userProfiles().first(where: { $0.profileId == profileId })
        guard let profile = profile else {
            return self.rejectWithError(reject, WrapperError.profileDoesNotExist)
        }
        userClient.deregisterUser(profile) {
            (result: Bool, error) -> Void in
            self.resolveWithResultOrError(resolve, reject, nil, error)
        }
    }

    @objc
    func handleRegistrationCallback(_ url: String,
                                    resolver resolve: @escaping RCTPromiseResolveBlock,
                                    rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let urlOBject = URL(string: url)
        guard let urlOBject = urlOBject else {
            return self.rejectWithError(reject, WrapperError.malformedUrl)
        }
        do {
            try bridgeConnector.toRegistrationConnector.registrationHandler.processRedirectURL(urlOBject)
            resolve(nil)
        } catch {
            self.rejectWithError(reject, error)
        }
    }

    @objc
    func cancelBrowserRegistration(_ resolve: @escaping RCTPromiseResolveBlock,
                            rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        do {
            try bridgeConnector.toRegistrationConnector.registrationHandler.cancelBrowserRegistration()
            resolve(nil)
        } catch {
            self.rejectWithError(reject, error)
        }
    }

    @objc
    func cancelCustomRegistration(_ message: String, resolver resolve: @escaping RCTPromiseResolveBlock,
                            rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        do {
            // Message here is not used, as we can not give a reason for canceling on iOS, only on Android
            try bridgeConnector.toRegistrationConnector.registrationHandler.cancelCustomRegistration()
            resolve(nil)
        } catch {
            self.rejectWithError(reject, error)
        }
    }

    @objc
    func cancelPinAuthentication(_ resolve: @escaping RCTPromiseResolveBlock,
                            rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        do {
            try bridgeConnector.toLoginHandler.cancelPinAuthentication()
            resolve(nil)
        } catch {
            self.rejectWithError(reject, error)
        }
    }

    @objc
    func cancelPinCreation(_ resolve: @escaping RCTPromiseResolveBlock,
                            rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        do {
            try bridgeConnector.toRegistrationConnector.registrationHandler.cancelPinCreation()
            resolve(nil)
        } catch {
            self.rejectWithError(reject, error)
        }
    }
    
    @objc
    func submitPin(_ flow: String, pin: String,
                         resolver resolve: @escaping RCTPromiseResolveBlock,
                         rejecter reject: @escaping RCTPromiseRejectBlock) {
        switch flow {
        case PinFlow.create.rawValue:
            do {
                try bridgeConnector.toRegistrationConnector.registrationHandler.handlePin(pin)
                resolve(nil)
            } catch {
                self.rejectWithError(reject, error)
            }
        case PinFlow.authentication.rawValue:
            do {
                try bridgeConnector.toLoginHandler.handlePin(pin)
                resolve(nil)
            } catch {
                self.rejectWithError(reject, error)
            }
        default:
            reject(String(WrapperError.parametersNotCorrect.code), "Incorrect pinflow supplied: \(flow)", WrapperError.parametersNotCorrect)
        }
    }

    @objc
    func changePin(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        bridgeConnector.toChangePinHandler.onChangePinCalled() {
            (_, error) -> Void in
            self.resolveWithResultOrError(resolve, reject, nil, error)
        }
    }

    @objc
    func authenticateUser(_ profileId: String, authenticatorId: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile = userClient.userProfiles().first(where: { $0.profileId == profileId })
        guard let profile = profile else {
            return self.rejectWithError(reject, WrapperError.profileDoesNotExist)
        }
        let authenticators = userClient.allAuthenticators(forUser: profile)
        let authenticator = authenticators.first(where: {$0.identifier == authenticatorId}) ?? userClient.preferredAuthenticator
        bridgeConnector.toLoginHandler.authenticateUser(profile, authenticator: authenticator) {
            (userProfile, error) -> Void in
            self.resolveWithResultOrError(resolve, reject, ["userProfile": ["id": userProfile.profileId]], error)
        }
    }

    @objc
    func logout(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        userClient.logoutUser { _, error in
            self.resolveWithResultOrError(resolve, reject, nil, error)
        }
    }

    @objc
    func startSingleSignOn(_ url: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let _url = URL(string: url)

        bridgeConnector.toAppToWebHandler.signInAppToWeb(targetURL: _url, completion: { (result, error) in
            self.resolveWithResultOrError(resolve, reject, nil, error)
        })
    }

    @objc
    func authenticateUserImplicitly(_ profileId: String,
                                    scopes: [String],
                                    resolver resolve: @escaping RCTPromiseResolveBlock,
                                    rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile = userClient.userProfiles().first(where: { $0.profileId == profileId })
        guard let profile = profile else {
            return self.rejectWithError(reject, WrapperError.profileDoesNotExist)
        }

        bridgeConnector.toResourceHandler.authenticateImplicitly(profile, scopes: scopes) {
            (success, error) -> Void in
            self.resolveWithResultOrError(resolve, reject, nil, error)
        }
    }

    @objc
    func authenticateDeviceForResource(_ scopes: [String],
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        bridgeConnector.toResourceHandler.authenticateDevice(scopes) {
            (success, error) -> Void in
            self.resolveWithResultOrError(resolve, reject, nil, error)
        }
    }

    @objc
    func resourceRequest(_ type: (String), details: (NSDictionary),
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {

        let type = ResourceRequestType(rawValue: type) ?? .Anonymous

        bridgeConnector.toResourceHandler.resourceRequest(type, details) {
            (data: String?, error) -> Void in
            self.resolveWithResultOrError(resolve, reject, data, error)
        }
    }

    @objc
    func enrollMobileAuthentication(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        bridgeConnector.toMobileAuthConnector.mobileAuthHandler.enrollForMobileAuth { _, error in
            self.resolveWithResultOrError(resolve, reject, nil, error)
        }
    }

    @objc
    func handleMobileAuthWithOtp(_ otpCode: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        bridgeConnector.toMobileAuthConnector.mobileAuthHandler.handleOTPMobileAuth(otpCode) {
            (_ , error) -> Void in
            self.resolveWithResultOrError(resolve, reject, nil, error)
        }
    }

    @objc
    func acceptMobileAuthConfirmation(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        if (bridgeConnector.toMobileAuthConnector.mobileAuthHandler.handleMobileAuthConfirmation(accepted: true)) {
            resolve(nil)
        } else {
            self.rejectWithError(reject, WrapperError.mobileAuthNotInProgress)
        }
    }

    @objc
    func denyMobileAuthConfirmation(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        if (bridgeConnector.toMobileAuthConnector.mobileAuthHandler.handleMobileAuthConfirmation(accepted: false)) {
            resolve(nil)
        } else {
            self.rejectWithError(reject, WrapperError.mobileAuthNotInProgress)
        }
    }

    // Authenticators management
    @objc
    func getAllAuthenticators(_ profileId: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile = userClient.userProfiles().first(where: { $0.profileId == profileId })
        guard let profile = profile else {
            return self.rejectWithError(reject, WrapperError.profileDoesNotExist)
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
            return self.rejectWithError(reject, WrapperError.profileDoesNotExist)
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
            return self.rejectWithError(reject, WrapperError.profileDoesNotExist)
        }

        bridgeConnector.toAuthenticatorsHandler.setPreferredAuthenticator(profile, authenticatorId) {
            (_ , error) -> Void in
            self.resolveWithResultOrError(resolve, reject, nil, error)
        }
    }

    @objc
    func validatePinWithPolicy(_ pin: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        userClient.validatePin(withPolicy: pin) { (_ , error) -> Void in
            self.resolveWithResultOrError(resolve, reject, nil, error)
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
            return self.rejectWithError(reject, WrapperError.profileDoesNotExist)
        }
        bridgeConnector.toAuthenticatorsHandler.registerAuthenticator(profile, ONGAuthenticatorType.biometric) {
            (_ , error) -> Void in
            self.resolveWithResultOrError(resolve, reject, nil, error)
        }
    }

    @objc
    func deregisterFingerprintAuthenticator(_ profileId: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile = userClient.userProfiles().first(where: { $0.profileId == profileId })
        guard let profile = profile else {
            return self.rejectWithError(reject, WrapperError.profileDoesNotExist)
        }

        bridgeConnector.toAuthenticatorsHandler.deregisterAuthenticator(profile, ONGAuthenticatorType.biometric) {
            (_ , error) -> Void in
            self.resolveWithResultOrError(resolve, reject, nil, error)
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
