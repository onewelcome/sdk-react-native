class RegistrationHandler: NSObject {
    private var createPinChallenge: ONGCreatePinChallenge?
    private var browserRegistrationChallenge: ONGBrowserRegistrationChallenge?
    private var customRegistrationChallenge: ONGCustomRegistrationChallenge?
    private var signUpCompletion: ((Bool, ONGUserProfile?, Error?) -> Void)?
    private let createPinEventEmitter = CreatePinEventEmitter()
    private let registrationEventEmitter = RegistrationEventEmitter()
    static let cancelCustomRegistrationNotAllowed = "Canceling the custom registration right now is not allowed. Registration is not in progress or pin creation has already started."
    static let cancelBrowserRegistrationNotAllowed = "Canceling the browser registration right now is not allowed. Registration is not in progress or pin creation has already started."

    func handleRedirectURL(_ url: URL, completion: @escaping (Error?) -> Void) {
        guard let browserRegistrationChallenge = self.browserRegistrationChallenge else {
            completion(WrapperError.registrationNotInProgress)
            return
        }
        browserRegistrationChallenge.sender.respond(with: url, challenge: browserRegistrationChallenge)
        self.browserRegistrationChallenge = nil
        completion(nil)
    }

    func handlePin(_ pin: String?, completion: @escaping (Error?) -> Void) {
        guard let createPinChallenge = self.createPinChallenge else {
            completion(WrapperError.registrationNotInProgress)
            return
        }
        guard let pin = pin else{
            createPinChallenge.sender.cancel(createPinChallenge)
            completion(nil)
            return
        }
        createPinChallenge.sender.respond(withCreatedPin: pin, challenge: createPinChallenge)
        completion(nil)
    }

    func handleOTPCode(_ code: String? = nil, completion: @escaping (Error?) -> Void) {
        guard let customRegistrationChallenge = self.customRegistrationChallenge else {
            completion(WrapperError.registrationNotInProgress)
            return
        }
        customRegistrationChallenge.sender.respond(withData: code, challenge: customRegistrationChallenge)
        self.customRegistrationChallenge = nil
        completion(nil)
    }

    fileprivate func mapErrorFromPinChallenge(_ challenge: ONGCreatePinChallenge) -> Error? {
        if let error = challenge.error {
            return error
        } else {
            return nil
        }
    }

    private func sendCustomRegistrationNotification(_ event: CustomRegistrationNotification,_ data: NSMutableDictionary) {
        BridgeConnector.shared?.toRegistrationConnector.sendCustomRegistrationNotification(event, data)
    }
}

extension RegistrationHandler {
    func setCreatePinChallenge(_ challenge: ONGCreatePinChallenge?) {
        createPinChallenge = challenge
    }
    
    func signUp(identityProvider: ONGIdentityProvider? = nil, scopes: [String], completion: @escaping (Bool, ONGUserProfile?, Error?) -> Void) {
        signUpCompletion = completion
        ONGUserClient.sharedInstance().registerUser(with: identityProvider, scopes: scopes, delegate: self)
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
    
    func handleDidReceivePinRegistrationChallenge(_ challenge: ONGCreatePinChallenge) {
        createPinChallenge = challenge
        if let pinError = mapErrorFromPinChallenge(challenge) {
            createPinEventEmitter.onPinNotAllowed(error: pinError)
        } else {
            createPinEventEmitter.onPinOpen(profileId: challenge.userProfile.profileId, pinLength: challenge.pinLength)
        }
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

extension RegistrationHandler: ONGRegistrationDelegate {
    func userClient(_: ONGUserClient, didReceive challenge: ONGBrowserRegistrationChallenge) {
        browserRegistrationChallenge = challenge
        registrationEventEmitter.onSendUrl(challenge.url)
    }

    func userClient(_: ONGUserClient, didReceivePinRegistrationChallenge challenge: ONGCreatePinChallenge) {
        handleDidReceivePinRegistrationChallenge(challenge)
    }

    func userClient(_: ONGUserClient, didReceiveCustomRegistrationInitChallenge challenge: ONGCustomRegistrationChallenge) {
        customRegistrationChallenge = challenge

        let result = NSMutableDictionary()
        result.setValue(challenge.identityProvider.identifier, forKey: "identityProviderId")

        sendCustomRegistrationNotification(CustomRegistrationNotification.initRegistration, result)
    }

    func userClient(_: ONGUserClient, didReceiveCustomRegistrationFinish challenge: ONGCustomRegistrationChallenge) {
        customRegistrationChallenge = challenge

        let result = NSMutableDictionary()
        result.setValue(challenge.identityProvider.identifier, forKey: "identityProviderId")

        if let info = challenge.info {
            let customInfo = NSMutableDictionary()

            customInfo.setValue(info.data, forKey: "data")
            customInfo.setValue(info.status, forKey: "status")
            result.setValue(customInfo, forKey: "customInfo")
        }

        sendCustomRegistrationNotification(CustomRegistrationNotification.finishRegistration, result)
    }
    
    func userClient(_ userClient: ONGUserClient, didRegisterUser userProfile: ONGUserProfile, identityProvider: ONGIdentityProvider, info: ONGCustomInfo?) {
        handleDidRegisterUser()
        signUpCompletion?(true, userProfile, nil)
        signUpCompletion = nil
    }
    
    func userClient(_ userClient: ONGUserClient, didFailToRegisterWith identityProvider: ONGIdentityProvider, error: Error) {
        handleDidFailToRegister()
        signUpCompletion?(false, nil, error)
        signUpCompletion = nil
    }

}
