protocol RegistrationConnectorToHandlerProtocol: AnyObject {
    func signUp(identityProvider: ONGIdentityProvider?, scopes: [String], completion: @escaping (Bool, ONGUserProfile?, Error?) -> Void)
    func processRedirectURL(url: URL)
    func processOTPCode(code: String?)
    func cancelRegistration()
    func cancelCustomRegistration()
    func setCreatePinChallenge(_ challenge: ONGCreatePinChallenge?)
    func handlePinAction(_ pin: String?, action: PinAction)
    func handleDidReceivePinRegistrationChallenge(_ challenge: ONGCreatePinChallenge)
    func handleDidFailToRegister()
    func handleDidRegisterUser()
}


class RegistrationHandler: NSObject, BrowserHandlerToRegisterHandlerProtocol {
    private var createPinChallenge: ONGCreatePinChallenge?
    private var browserRegistrationChallenge: ONGBrowserRegistrationChallenge?
    private var customRegistrationChallenge: ONGCustomRegistrationChallenge?
    private var signUpCompletion: ((Bool, ONGUserProfile?, Error?) -> Void)?
    private let createPinEventEmitter = CreatePinEventEmitter()
    private let registrationEventEmitter = RegistrationEventEmitter()

    func handleRedirectURL(url: URL?) {
        guard let browserRegistrationChallenge = self.browserRegistrationChallenge else { return }
        if let url = url {
            browserRegistrationChallenge.sender.respond(with: url, challenge: browserRegistrationChallenge)
        } else {
            browserRegistrationChallenge.sender.cancel(browserRegistrationChallenge)
        }
    }

    func handlePin(_ pin: String?) {
        
        guard let createPinChallenge = self.createPinChallenge else { return }
        guard let pin = pin else{
            createPinChallenge.sender.cancel(createPinChallenge)
            return
        }
        createPinChallenge.sender.respond(withCreatedPin: pin, challenge: createPinChallenge)
    }

    func handleOTPCode(_ code: String? = nil, _ cancelled: Bool? = false) {
        guard let customRegistrationChallenge = self.customRegistrationChallenge else { return }
        if(cancelled == true) {
            customRegistrationChallenge.sender.cancel(customRegistrationChallenge)
            return;
        }
        customRegistrationChallenge.sender.respond(withData: code, challenge: customRegistrationChallenge)
    }

    fileprivate func mapErrorFromPinChallenge(_ challenge: ONGCreatePinChallenge) -> Error? {
        if let error = challenge.error {
            return error
        } else {
            return nil
        }
    }

    private func sendCustomRegistrationNotification(_ event: CustomRegistrationNotification,_ data: NSMutableDictionary) {
        BridgeConnector.shared?.toRegistrationConnector.sendCustomRegistrationNotification(event, data);
    }
}

extension RegistrationHandler : RegistrationConnectorToHandlerProtocol {
    func setCreatePinChallenge(_ challenge: ONGCreatePinChallenge?) {
        createPinChallenge = challenge
    }
    
    func signUp(identityProvider: ONGIdentityProvider? = nil, scopes: [String], completion: @escaping (Bool, ONGUserProfile?, Error?) -> Void) {
        signUpCompletion = completion
        ONGUserClient.sharedInstance().registerUser(with: identityProvider, scopes: scopes, delegate: self)
    }

    func processRedirectURL(url: URL) {
        handleRedirectURL(url: url)
    }

    func processOTPCode(code: String?) {
        handleOTPCode(code)
    }

    func cancelCustomRegistration() {
        handleOTPCode(nil, true)
    }

    func cancelRegistration() {
        handleRedirectURL(url: nil)
        guard let createPinChallenge = self.createPinChallenge else { return }
        createPinChallenge.sender.cancel(createPinChallenge)
    }
    
    func handlePinAction(_ pin: String?, action: PinAction) {
        switch action {
            case PinAction.provide:
                handlePin(pin)
            case PinAction.cancel:
                cancelRegistration()
        }
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
        createPinEventEmitter.onPinClose()
    }
    
    
    func handleDidRegisterUser() {
        createPinChallenge = nil
        customRegistrationChallenge = nil
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
