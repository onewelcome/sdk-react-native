protocol RegistrationConnectorToHandlerProtocol: AnyObject {
    func signUp(identityProvider: ONGIdentityProvider?, completion: @escaping (Bool, ONGUserProfile?, SdkError?) -> Void)
    func processRedirectURL(url: URL)
    func processOTPCode(code: String?)
    func cancelRegistration()
    func cancelCustomRegistration()
}


class RegistrationHandler: NSObject, BrowserHandlerToRegisterHandlerProtocol, PinHandlerToReceiverProtocol {
    var createPinChallenge: ONGCreatePinChallenge?
    var browserRegistrationChallenge: ONGBrowserRegistrationChallenge?
    var customRegistrationChallenge: ONGCustomRegistrationChallenge?
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

    func handleOTPCode(_ code: String? = nil, _ cancelled: Bool? = false) {
        guard let customRegistrationChallenge = self.customRegistrationChallenge else { return }
        if(cancelled == true) {
            customRegistrationChallenge.sender.cancel(customRegistrationChallenge)
            return;
        }
        customRegistrationChallenge.sender.respond(withData: code, challenge: customRegistrationChallenge)
    }

    fileprivate func mapErrorFromPinChallenge(_ challenge: ONGCreatePinChallenge) -> SdkError? {
        if let error = challenge.error {
            return ErrorMapper().mapError(error)
        } else {
            return nil
        }
    }

    private func sendCustomRegistrationNotification(_ event: CustomRegistrationNotification,_ data: NSMutableDictionary?) {
        BridgeConnector.shared?.toRegistrationConnector.sendCustomRegistrationNotification(event, data);
    }
}

extension RegistrationHandler : RegistrationConnectorToHandlerProtocol {
    func signUp(identityProvider: ONGIdentityProvider? = nil, completion: @escaping (Bool, ONGUserProfile?, SdkError?) -> Void) {
        signUpCompletion = completion
        ONGUserClient.sharedInstance().registerUser(with: identityProvider, scopes: ["read"], delegate: self)
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
        BridgeConnector.shared?.toPinHandlerConnector.pinHandler.handleFlowUpdate(PinFlow.create, pinError, receiver: self)
    }

    func userClient(_: ONGUserClient, didRegisterUser userProfile: ONGUserProfile, info _: ONGCustomInfo?) {
        createPinChallenge = nil
        customRegistrationChallenge = nil
        signUpCompletion!(true, userProfile, nil)
        BridgeConnector.shared?.toPinHandlerConnector.pinHandler.closeFlow()
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

    func userClient(_: ONGUserClient, didFailToRegisterWithError error: Error) {
        createPinChallenge = nil
        customRegistrationChallenge = nil
        BridgeConnector.shared?.toPinHandlerConnector.pinHandler.closeFlow()

        if error.code == ONGGenericError.actionCancelled.rawValue {
            signUpCompletion!(false, nil, SdkError(errorDescription: "Registration  cancelled."))
        } else {
            let mappedError = ErrorMapper().mapError(error)
            signUpCompletion!(false, nil, mappedError)
        }
    }
}
