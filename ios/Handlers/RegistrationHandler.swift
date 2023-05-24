class RegistrationHandler {

    private var createPinChallenge: CreatePinChallenge?
    private var browserRegistrationChallenge: BrowserRegistrationChallenge?
    private var customRegistrationChallenge: CustomRegistrationChallenge?
    let createPinEventEmitter = CreatePinEventEmitter()
    let registrationEventEmitter = RegistrationEventEmitter()

    static let cancelCustomRegistrationNotAllowed = "Canceling the custom registration right now is not allowed. Registration is not in progress or pin creation has already started."
    static let cancelBrowserRegistrationNotAllowed = "Canceling the browser registration right now is not allowed. Registration is not in progress or pin creation has already started."

    func handleRedirectURL(_ url: URL, completion: @escaping (Error?) -> Void) {
        guard let browserRegistrationChallenge = self.browserRegistrationChallenge else {
            completion(WrapperError.registrationNotInProgress)
            return
        }
        browserRegistrationChallenge.sender.respond(with: url, to: browserRegistrationChallenge)
        self.browserRegistrationChallenge = nil
        completion(nil)
    }

    func handlePin(_ pin: String?, completion: @escaping (Error?) -> Void) {
        guard let createPinChallenge = self.createPinChallenge else {
            completion(WrapperError.registrationNotInProgress)
            return
        }
        guard let pin = pin else {
            createPinChallenge.sender.cancel(createPinChallenge)
            completion(nil)
            return
        }
        createPinChallenge.sender.respond(with: pin, to: createPinChallenge)
        completion(nil)
    }

    func handleOTPCode(_ code: String? = nil, completion: @escaping (Error?) -> Void) {
        guard let customRegistrationChallenge = self.customRegistrationChallenge else {
            completion(WrapperError.registrationNotInProgress)
            return
        }
        customRegistrationChallenge.sender.respond(with: code, to: customRegistrationChallenge)
        self.customRegistrationChallenge = nil
        completion(nil)
    }

    fileprivate func mapErrorFromPinChallenge(_ challenge: CreatePinChallenge) -> Error? {
        if let error = challenge.error {
            return error
        } else {
            return nil
        }
    }

    func sendCustomRegistrationNotification(_ event: CustomRegistrationNotification, _ data: NSMutableDictionary) {
        BridgeConnector.shared?.toRegistrationConnector.sendCustomRegistrationNotification(event, data)
    }
}

extension RegistrationHandler {
    func setCreatePinChallenge(_ challenge: CreatePinChallenge?) {
        createPinChallenge = challenge
    }

    func signUp(identityProvider: IdentityProvider? = nil, scopes: [String], completion: @escaping (Result<RegistrationResponse, Error>) -> Void) {
        let delegate = RegistrationDelegateImpl(registrationHandler: self, completion: completion)
        SharedUserClient.instance.registerUserWith(identityProvider: identityProvider, scopes: scopes, delegate: delegate)
    }

    func cancelCustomRegistration(completion: @escaping (Error?) -> Void) {
        guard let customRegistrationChallenge = self.customRegistrationChallenge else {
            completion(WrapperError.actionNotAllowed(description: RegistrationHandler.cancelBrowserRegistrationNotAllowed))
            return
        }
        customRegistrationChallenge.sender.cancel(customRegistrationChallenge)
        handleDidFailToRegister()
        completion(nil)
    }

    func cancelBrowserRegistration(completion: @escaping (Error?) -> Void) {
        guard let browserRegistrationChallenge = self.browserRegistrationChallenge else {
            completion(WrapperError.actionNotAllowed(description: RegistrationHandler.cancelCustomRegistrationNotAllowed))
            return
        }
        browserRegistrationChallenge.sender.cancel(browserRegistrationChallenge)
        handleDidFailToRegister()
        completion(nil)
    }
    func cancelPinCreation(completion: @escaping (Error?) -> Void) {
        guard let createPinChallenge = self.createPinChallenge else {
            completion(WrapperError.registrationNotInProgress)
            return
        }
        createPinChallenge.sender.cancel(createPinChallenge)
        completion(nil)
    }

    func handleDidReceivePinRegistrationChallenge(_ challenge: CreatePinChallenge) {
        createPinChallenge = challenge
        if let pinError = mapErrorFromPinChallenge(challenge) {
            createPinEventEmitter.onPinNotAllowed(error: pinError)
        } else {
            guard let userProfile = challenge.userProfile else { return }
            createPinEventEmitter.onPinOpen(profileId: userProfile.profileId, pinLength: challenge.pinLength)
        }
    }

    func handleDidReceiveBrowserRegistrationChallenge(_ challenge: BrowserRegistrationChallenge) {
        browserRegistrationChallenge = challenge
        registrationEventEmitter.onSendUrl(challenge.url)
    }

    func handleDidReceiveCustomRegistrationChallenge(_ challenge: CustomRegistrationChallenge) {
        customRegistrationChallenge = challenge
    }

    func handleDidFailToRegister() {
        createPinChallenge = nil
        customRegistrationChallenge = nil
        browserRegistrationChallenge = nil
        createPinEventEmitter.onPinClose()
    }

    func handleDidRegisterUser() {
        createPinChallenge = nil
        customRegistrationChallenge = nil
        browserRegistrationChallenge = nil
        createPinEventEmitter.onPinClose()
    }
}

class RegistrationDelegateImpl: RegistrationDelegate {
    private let completion: ((Result<RegistrationResponse, Error>) -> Void)
    private let registrationHandler: RegistrationHandler

    init(registrationHandler: RegistrationHandler, completion: @escaping (Result<RegistrationResponse, Error>) -> Void) {
        self.completion = completion
        self.registrationHandler = registrationHandler
    }

    func userClient(_ userClient: UserClient, didReceiveCreatePinChallenge challenge: CreatePinChallenge) {
        registrationHandler.handleDidReceivePinRegistrationChallenge(challenge)
    }

    func userClient(_ userClient: UserClient, didReceiveBrowserRegistrationChallenge challenge: BrowserRegistrationChallenge) {
        registrationHandler.handleDidReceiveBrowserRegistrationChallenge(challenge)

    }

    func userClient(_ userClient: UserClient, didReceiveCustomRegistrationInitChallenge challenge: CustomRegistrationChallenge) {
        registrationHandler.handleDidReceiveCustomRegistrationChallenge(challenge)

        let result = NSMutableDictionary()
        result.setValue(challenge.identityProvider.identifier, forKey: "identityProviderId")

        registrationHandler.sendCustomRegistrationNotification(CustomRegistrationNotification.initRegistration, result)
    }

    func userClient(_ userClient: UserClient, didReceiveCustomRegistrationFinishChallenge challenge: CustomRegistrationChallenge) {
        registrationHandler.handleDidReceiveCustomRegistrationChallenge(challenge)
        let result = NSMutableDictionary()
        result.setValue(challenge.identityProvider.identifier, forKey: "identityProviderId")

        if let info = challenge.info {
            let customInfo = NSMutableDictionary()

            customInfo.setValue(info.data, forKey: "data")
            customInfo.setValue(info.status, forKey: "status")
            result.setValue(customInfo, forKey: "customInfo")
        }

        registrationHandler.sendCustomRegistrationNotification(CustomRegistrationNotification.finishRegistration, result)
    }

    func userClient(_ userClient: UserClient, didRegisterUser profile: UserProfile, with identityProvider: IdentityProvider, info: CustomInfo?) {
        registrationHandler.handleDidRegisterUser()
        completion(.success(
            RegistrationResponse(userProfile: profile,
                                   customInfo: info)))
    }

    func userClient(_ userClient: UserClient, didFailToRegisterUserWith identityProvider: IdentityProvider, error: Error) {
        registrationHandler.handleDidFailToRegister()
        completion(.failure(error))
    }
}
