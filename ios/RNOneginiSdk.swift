// swiftlint:disable type_body_length file_length
import Foundation
import React
import OneginiSDKiOS
import LocalAuthentication

protocol ConnectorToRNBridgeProtocol: NSObject {
  func sendBridgeEvent(eventName: OneWelcomeBridgeEvents, data: Any)
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

    private lazy var sharedClient = ClientBuilder().build()

    private var userClientONG: ONGUserClient {
        // We use this computed property to make sure the SDK is built before we use it. This can be removed once we fully switch to the swift api.
        _ = sharedClient
        return ONGUserClient.sharedInstance()
    }

    private var deviceClientONG: ONGDeviceClient {
        // We use this computed property to make sure the SDK is built before we use it. This can be removed once we fully switch to the swift api.
        _ = sharedClient
        return ONGDeviceClient.sharedInstance()
    }

    private var deviceClient: DeviceClient {
        return sharedClient.deviceClient
    }

    private var userClient: UserClient {
        return sharedClient.userClient
    }

    override init() {
        self.bridgeConnector = BridgeConnector()
        super.init()
        self.bridgeConnector.bridge = self

    }

    override func supportedEvents() -> [String] {
        return [OneWelcomeBridgeEvents.pinAuthNotification.rawValue,
                OneWelcomeBridgeEvents.pinCreateNotification.rawValue,
                OneWelcomeBridgeEvents.fingerprintNotification.rawValue,
                OneWelcomeBridgeEvents.customRegistrationNotification.rawValue,
                OneWelcomeBridgeEvents.authWithOtpNotification.rawValue,
                OneWelcomeBridgeEvents.registrationNotification.rawValue]
    }

    func sendBridgeEvent(eventName: OneWelcomeBridgeEvents, data: Any) {
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

    func makeCompletionHandler(resolver resolve: @escaping RCTPromiseResolveBlock,
                               rejecter reject: @escaping RCTPromiseRejectBlock,
                               resolveWithNil: Bool
    ) -> (_ result: Any?, _ error: Error?) -> Void {
        return { (_ result: Any?, _ error: Error?) in
            if let error = error {
                self.rejectWithError(reject, error)
            } else {
                if resolveWithNil {
                    resolve(nil)
                } else {
                    resolve(result)
                }
            }
        }
    }

    func makeCompletionHandler(resolver resolve: @escaping RCTPromiseResolveBlock,
                               rejecter reject: @escaping RCTPromiseRejectBlock
    ) -> (_ error: Error?) -> Void {
        return { (_ error: Error?) in
            if let error = error {
                self.rejectWithError(reject, error)
            } else {
                resolve(nil)
            }
        }
    }

    @objc
    func startClient(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
        let completion = makeCompletionHandler(resolver: resolve, rejecter: reject)
        sharedClient.start(completion: completion)
    }

    @objc
    func getRedirectUri(_ resolve: RCTPromiseResolveBlock, rejecter reject: RCTPromiseRejectBlock) {
        let redirectUri = sharedClient.configModel.redirectURL
        resolve(redirectUri)
    }

    @objc
    func getAccessToken(_ resolve: RCTPromiseResolveBlock, rejecter reject: RCTPromiseRejectBlock) {
        let accessToken = userClientONG.accessToken
        guard let accessToken = accessToken else {
            rejectWithError(reject, WrapperError.noProfileAuthenticated)
            return
        }
        resolve(accessToken)
    }

    @objc
    func getUserProfiles(_ resolve: RCTPromiseResolveBlock, rejecter reject: RCTPromiseRejectBlock) {
        let profiles = userClientONG.userProfiles()
        let result: NSMutableArray = []

        for profile in profiles {
            result.add(["id": profile.value(forKey: "profileId")])
        }

        resolve(result)
    }

    @objc
    func getAuthenticatedUserProfile(_ resolve: RCTPromiseResolveBlock, rejecter reject: RCTPromiseRejectBlock) {
        guard let authenticatedProfile = userClientONG.authenticatedUserProfile() else {
            rejectWithError(reject, WrapperError.noProfileAuthenticated)
            return
        }
        resolve(["id": authenticatedProfile.profileId])
    }

    @objc
    func getIdentityProviders(_ resolve: RCTPromiseResolveBlock, rejecter reject: RCTPromiseRejectBlock) {
        let profiles = userClientONG.identityProviders()
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
                      rejecter reject: @escaping RCTPromiseRejectBlock) {
        var provider: IdentityProvider?
        if identityProviderId != nil {
            provider = userClient.identityProviders.first(where: { $0.identifier == identityProviderId })
        }
        bridgeConnector.toRegistrationConnector.registrationHandler.signUp(identityProvider: provider,
                                                                           scopes: scopes) { (userProfile, customInfo, error) -> Void in
            let result = mapToAuthData(profile: userProfile, info: customInfo)
            self.resolveResultOrRejectError(resolve, reject, result, error)
        }
    }

    @objc
    func submitCustomRegistrationAction(_ identityProviderId: String, token: String?,
                                        resolver resolve: @escaping RCTPromiseResolveBlock,
                                        rejecter reject: @escaping RCTPromiseRejectBlock) {
        let completion = makeCompletionHandler(resolver: resolve, rejecter: reject)
        bridgeConnector.toRegistrationConnector.registrationHandler.handleOTPCode(token, completion: completion)
    }

    @objc
    func deregisterUser(_ profileId: String,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) {
        let profile = userClientONG.userProfiles().first(where: { $0.profileId == profileId })
        guard let profile = profile else {
            rejectWithError(reject, WrapperError.profileDoesNotExist)
            return
        }
        let completion = makeCompletionHandler(resolver: resolve, rejecter: reject, resolveWithNil: true)
        userClientONG.deregisterUser(profile, completion: completion)
    }

    @objc
    func handleRegistrationCallback(_ url: String,
                                    resolver resolve: @escaping RCTPromiseResolveBlock,
                                    rejecter reject: @escaping RCTPromiseRejectBlock) {
        let urlOBject = URL(string: url)
        guard let urlOBject = urlOBject else {
            rejectWithError(reject, WrapperError.malformedUrl)
            return
        }
        let completion = makeCompletionHandler(resolver: resolve, rejecter: reject)
        bridgeConnector.toRegistrationConnector.registrationHandler.handleRedirectURL(urlOBject, completion: completion)
    }

    @objc
    func cancelBrowserRegistration(_ resolve: @escaping RCTPromiseResolveBlock,
                                   rejecter reject: @escaping RCTPromiseRejectBlock) {
        let completion = makeCompletionHandler(resolver: resolve, rejecter: reject)
        bridgeConnector.toRegistrationConnector.registrationHandler.cancelBrowserRegistration(completion: completion)
    }

    @objc
    func cancelCustomRegistration(_ message: String, resolver resolve: @escaping RCTPromiseResolveBlock,
                                  rejecter reject: @escaping RCTPromiseRejectBlock) {
        let completion = makeCompletionHandler(resolver: resolve, rejecter: reject)
        bridgeConnector.toRegistrationConnector.registrationHandler.cancelCustomRegistration(completion: completion)
    }

    @objc
    func cancelPinAuthentication(_ resolve: @escaping RCTPromiseResolveBlock,
                                 rejecter reject: @escaping RCTPromiseRejectBlock) {
        let completion = makeCompletionHandler(resolver: resolve, rejecter: reject)
        bridgeConnector.toLoginHandler.cancelPinAuthentication(completion: completion)
    }

    @objc
    func cancelPinCreation(_ resolve: @escaping RCTPromiseResolveBlock,
                           rejecter reject: @escaping RCTPromiseRejectBlock) {
        let completion = makeCompletionHandler(resolver: resolve, rejecter: reject)
        bridgeConnector.toRegistrationConnector.registrationHandler.cancelPinCreation(completion: completion)
    }

    @objc
    func submitPin(_ flow: String, pin: String,
                   resolver resolve: @escaping RCTPromiseResolveBlock,
                   rejecter reject: @escaping RCTPromiseRejectBlock) {
        let completion = makeCompletionHandler(resolver: resolve, rejecter: reject)
        switch flow {
        case PinFlow.create.rawValue:
            bridgeConnector.toRegistrationConnector.registrationHandler.handlePin(pin, completion: completion)
        case PinFlow.authentication.rawValue:
            bridgeConnector.toLoginHandler.handlePin(pin, completion: completion)
        default:
            let error = WrapperError.parametersNotCorrect(description: "Incorrect pinflow supplied: \(flow)")
            reject(String(error.code), error.localizedDescription, error)
        }
    }

    @objc
    func changePin(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
        let completion = makeCompletionHandler(resolver: resolve, rejecter: reject)
        bridgeConnector.toChangePinHandler.onChangePinCalled(completion: completion)
    }

    @objc
    func authenticateUser(_ profileId: String, authenticatorId: String,
                          resolver resolve: @escaping RCTPromiseResolveBlock,
                          rejecter reject: @escaping RCTPromiseRejectBlock) {
        let profile = userClient.userProfiles.first(where: { $0.profileId == profileId })
        guard let profile = profile else {
            rejectWithError(reject, WrapperError.profileDoesNotExist)
            return
        }
        let authenticators = userClient.authenticators(.all, for: profile)
        let authenticator = authenticators.first(where: {$0.identifier == authenticatorId}) ?? userClient.preferredAuthenticator
        bridgeConnector.toLoginHandler.authenticateUser(profile, authenticator: authenticator) { (userProfile, result) -> Void in
            switch result {
            case .success(let response):
                resolve(mapToAuthData(profile: userProfile, info: response))
            case .failure(let error):
                self.rejectWithError(reject, error)
            }
        }
    }

    @objc
    func logout(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
        let completion = makeCompletionHandler(resolver: resolve, rejecter: reject, resolveWithNil: true)
        userClientONG.logoutUser(completion)
    }

    @objc
    func startSingleSignOn(_ url: String,
                           resolver resolve: @escaping RCTPromiseResolveBlock,
                           rejecter reject: @escaping RCTPromiseRejectBlock) {
        let completion = makeCompletionHandler(resolver: resolve, rejecter: reject, resolveWithNil: false)
        bridgeConnector.toAppToWebHandler.signInAppToWeb(targetURL: URL(string: url), completion: completion)
    }

    @objc
    func authenticateUserImplicitly(_ profileId: String,
                                    scopes: [String],
                                    resolver resolve: @escaping RCTPromiseResolveBlock,
                                    rejecter reject: @escaping RCTPromiseRejectBlock) {
        let profile = userClientONG.userProfiles().first(where: { $0.profileId == profileId })
        guard let profile = profile else {
            rejectWithError(reject, WrapperError.profileDoesNotExist)
            return
        }
        let completion = makeCompletionHandler(resolver: resolve, rejecter: reject, resolveWithNil: true)
        userClientONG.implicitlyAuthenticateUser(profile, scopes: scopes, completion: completion)
    }

    @objc
    func authenticateDeviceForResource(_ scopes: [String],
                                       resolver resolve: @escaping RCTPromiseResolveBlock,
                                       rejecter reject: @escaping RCTPromiseRejectBlock) {
        // Due to a bug in the swift api we need to use the obj-c api, after that is fixed, we can use commented code below.
//        let completion = makeCompletionHandler(resolver: resolve, rejecter: reject)
//        deviceClient.authenticateDevice(with: scopes, completion: completion)
        let completion = makeCompletionHandler(resolver: resolve, rejecter: reject, resolveWithNil: true)
        deviceClientONG.authenticateDevice(scopes, completion: completion)
    }

    @objc
    func resourceRequest(_ type: (String), details: (NSDictionary),
                         resolver resolve: @escaping RCTPromiseResolveBlock,
                         rejecter reject: @escaping RCTPromiseRejectBlock) {

        let resourceType = ResourceRequestType(rawValue: type)
        guard let resourceType = resourceType else {
            rejectWithError(reject, WrapperError.parametersNotCorrect(description: "Supplied request type is not supported"))
            return
        }
        guard let details = details as? [String: Any?] else {
            rejectWithError(reject, WrapperError.parametersNotCorrect(description: "Details must be an object only containing keys that are Strings"))
            return
        }

        bridgeConnector.toResourceHandler.resourceRequest(resourceType, details) { result in
            switch result {
            case .success(let response):
                resolve(response.asDictionary)
            case .failure(let error):
                self.rejectWithError(reject, error)
            }
        }
    }

    @objc
    func getResourceBaseUrl(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
        resolve(SharedClient.instance.configModel.resourceBaseURL)
    }

    @objc
    func enrollMobileAuthentication(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
        let completion = makeCompletionHandler(resolver: resolve, rejecter: reject)
        bridgeConnector.toMobileAuthConnector.mobileAuthHandler.enrollForMobileAuth(completion)
    }

    @objc
    func handleMobileAuthWithOtp(_ otpCode: String,
                                 resolver resolve: @escaping RCTPromiseResolveBlock,
                                 rejecter reject: @escaping RCTPromiseRejectBlock) {
        let completion = makeCompletionHandler(resolver: resolve, rejecter: reject)
        bridgeConnector.toMobileAuthConnector.mobileAuthHandler.handleOTPMobileAuth(otpCode, completion)
    }

    @objc
    func acceptMobileAuthConfirmation(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
        let completion = makeCompletionHandler(resolver: resolve, rejecter: reject)
        bridgeConnector.toMobileAuthConnector.mobileAuthHandler.handleMobileAuthConfirmation(accepted: true, completion: completion)
    }

    @objc
    func denyMobileAuthConfirmation(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
        let completion = makeCompletionHandler(resolver: resolve, rejecter: reject)
        bridgeConnector.toMobileAuthConnector.mobileAuthHandler.handleMobileAuthConfirmation(accepted: false, completion: completion)
    }

    // Authenticators management
    @objc
    func getAllAuthenticators(_ profileId: String,
                              resolver resolve: @escaping RCTPromiseResolveBlock,
                              rejecter reject: @escaping RCTPromiseRejectBlock) {

        let profile = userClient.userProfiles.first(where: { $0.profileId == profileId })
        guard let profile = profile else {
            rejectWithError(reject, WrapperError.profileDoesNotExist)
            return
        }

        let allAuthenticators: [Authenticator] = bridgeConnector.toAuthenticatorsHandler.getAuthenticatorsListForUserProfile(profile)

        let result: NSMutableArray = []

        for authenticator in allAuthenticators {
            result.add(["id": authenticator.identifier, "name": authenticator.name, "type": authenticator.type, "isRegistered": authenticator.isRegistered, "isPreferred": authenticator.isPreferred])
        }

        resolve(result)
    }

    @objc
    func getRegisteredAuthenticators(_ profileId: String,
                                     resolver resolve: @escaping RCTPromiseResolveBlock,
                                     rejecter reject: @escaping RCTPromiseRejectBlock) {
        let profile = userClient.userProfiles.first(where: { $0.profileId == profileId })
        guard let profile = profile else {
            rejectWithError(reject, WrapperError.profileDoesNotExist)
            return
        }

        let registeredAuthenticators: [Authenticator] = bridgeConnector.toAuthenticatorsHandler.getAuthenticatorsListForUserProfile(profile).filter {$0.isRegistered == true}

        let result: NSMutableArray = []

        for authenticator in registeredAuthenticators {
            result.add(["id": authenticator.identifier, "name": authenticator.name, "type": authenticator.type, "isRegistered": authenticator.isRegistered, "isPreferred": authenticator.isPreferred])
        }

        resolve(result)
    }

    @objc
    func setPreferredAuthenticator(_ authenticatorId: String,
                                   resolver resolve: @escaping RCTPromiseResolveBlock,
                                   rejecter reject: @escaping RCTPromiseRejectBlock) {
        guard let profile = userClient.authenticatedUserProfile else {
            rejectWithError(reject, WrapperError.noProfileAuthenticated)
            return
        }
        let completion = makeCompletionHandler(resolver: resolve, rejecter: reject)
        bridgeConnector.toAuthenticatorsHandler.setPreferredAuthenticator(profile, authenticatorId, completion: completion)
    }

    @objc
    func validatePinWithPolicy(_ pin: String,
                               resolver resolve: @escaping RCTPromiseResolveBlock,
                               rejecter reject: @escaping RCTPromiseRejectBlock) {
        let completion = makeCompletionHandler(resolver: resolve, rejecter: reject, resolveWithNil: true)
        userClientONG.validatePin(withPolicy: pin, completion: completion)
    }

    @objc
    func registerAuthenticator(_ authenticatorId: String,
                               resolver resolve: @escaping RCTPromiseResolveBlock,
                               rejecter reject: @escaping RCTPromiseRejectBlock) {
        let profile = userClient.authenticatedUserProfile
        guard let profile = profile else {
            rejectWithError(reject, WrapperError.profileDoesNotExist)
            return
        }
        let completion = makeCompletionHandler(resolver: resolve, rejecter: reject)
        bridgeConnector.toAuthenticatorsHandler.registerAuthenticator(profile, authenticatorId, completion: completion)
    }

    @objc
    func deregisterAuthenticator(_ authenticatorId: String,
                                 resolver resolve: @escaping RCTPromiseResolveBlock,
                                 rejecter reject: @escaping RCTPromiseRejectBlock) {
        let profile = userClient.authenticatedUserProfile
        guard let profile = profile else {
            reject(String(WrapperError.noProfileAuthenticated.code), WrapperError.noProfileAuthenticated.localizedDescription, WrapperError.noProfileAuthenticated)
            return
        }
        let completion = makeCompletionHandler(resolver: resolve, rejecter: reject)
        bridgeConnector.toAuthenticatorsHandler.deregisterAuthenticator(profile, authenticatorId, completion: completion)
    }
}
