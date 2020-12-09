protocol BridgeToRegisterHandlerProtocol: AnyObject {
    func signUp(completion: @escaping (Bool, ONGUserProfile?, SdkError?) -> Void)
    func processRedirectURL(url: URL)
    func cancelRegistration()
}


class RegistrationHandler: NSObject, BrowserHandlerToRegisterHandlerProtocol, PinHandlerToReceiverProtocol {
    var createPinChallenge: ONGCreatePinChallenge?
    var browserRegistrationChallenge: ONGBrowserRegistrationChallenge?
    var browserConntroller: BrowserHandlerProtocol?
    var signUpCompletion: ((Bool, ONGUserProfile?, SdkError?) -> Void)?

    func presentBrowserUserRegistrationView(registrationUserURL: URL) {
        if(browserConntroller != nil) {
            browserConntroller?.handleUrl(url: registrationUserURL)
        } else {
            if #available(iOS 12.0, *) {
                browserConntroller = BrowserViewController(registerHandlerProtocol: self)
                browserConntroller?.handleUrl(url: registrationUserURL)
            } else {
              // Fallback on earlier versions
            }
        }
    }

    func handleRedirectURL(url: URL?) {
        guard let browserRegistrationChallenge = self.browserRegistrationChallenge else { return }
        if(url != nil) {
            browserRegistrationChallenge.sender.respond(with: url!, challenge: browserRegistrationChallenge)
        } else {
            browserRegistrationChallenge.sender.cancel(browserRegistrationChallenge)
        }
    }

    func handlePin(pin: String?) {
        guard let createPinChallenge = self.createPinChallenge else { return }

        if(pin != nil) {
            createPinChallenge.sender.respond(withCreatedPin: pin!, challenge: createPinChallenge)

        } else {
            createPinChallenge.sender.cancel(createPinChallenge)
        }
    }

    fileprivate func mapErrorFromPinChallenge(_ challenge: ONGCreatePinChallenge) -> SdkError? {
        if let error = challenge.error {
            return ErrorMapper().mapError(error)
        } else {
            return nil
        }
    }
}

extension RegistrationHandler : BridgeToRegisterHandlerProtocol {
    //@todo add support for multiple providers
    func signUp(completion: @escaping (Bool, ONGUserProfile?, SdkError?) -> Void) {
        signUpCompletion = completion
        ONGUserClient.sharedInstance().registerUser(with: nil, scopes: ["read"], delegate: self)
    }

    func processRedirectURL(url: URL) {
        handleRedirectURL(url: url)
    }

    func cancelRegistration() {
        handleRedirectURL(url: nil)
    }
}

extension RegistrationHandler: ONGRegistrationDelegate {
    func userClient(_: ONGUserClient, didReceive challenge: ONGBrowserRegistrationChallenge) {
        browserRegistrationChallenge = challenge
        presentBrowserUserRegistrationView(registrationUserURL: challenge.url)
    }

    func userClient(_: ONGUserClient, didReceivePinRegistrationChallenge challenge: ONGCreatePinChallenge) {
        createPinChallenge = challenge
        let pinError = mapErrorFromPinChallenge(challenge)
        BridgeConnector.shared?.toPinHandlerConnector.pinHandler.handleFlowUpdate(PinFlow.create, pinError, reciever: self)
    }

    func userClient(_: ONGUserClient, didRegisterUser userProfile: ONGUserProfile, info _: ONGCustomInfo?) {
        signUpCompletion!(true, userProfile, nil)
        BridgeConnector.shared?.toPinHandlerConnector.pinHandler.closeFlow()
    }

    func userClient(_: ONGUserClient, didFailToRegisterWithError error: Error) {
        if error.code == ONGGenericError.actionCancelled.rawValue {
            signUpCompletion!(false, nil, SdkError(errorDescription: "Registration  cancelled."))
            BridgeConnector.shared?.toPinHandlerConnector.pinHandler.closeFlow()
        } else {
            let mappedError = ErrorMapper().mapError(error)
            signUpCompletion!(false, nil, mappedError)
        }
    }
}
