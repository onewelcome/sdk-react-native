protocol BridgeToLoginHandlerProtocol: AnyObject {
    func authenticateUser(_ profile: ONGUserProfile, completion: @escaping (ONGUserProfile?, SdkError?) -> Void)
}


class LoginHandler: NSObject, PinHandlerToReceiverProtocol {
    var pinChallenge: ONGPinChallenge?
    var loginCompletion: ((ONGUserProfile?, SdkError?) -> Void)?
    

    func handlePin(pin: String?) {
        guard let pinChallenge = self.pinChallenge else { return }

        if(pin != nil) {
            pinChallenge.sender.respond(withPin: pin!, challenge: pinChallenge)

        } else {
            pinChallenge.sender.cancel(pinChallenge)
        }
    }

    fileprivate func mapErrorFromPinChallenge(_ challenge: ONGPinChallenge) -> SdkError? {
        if let error = challenge.error, error.code != ONGAuthenticationError.touchIDAuthenticatorFailure.rawValue {
            return ErrorMapper().mapError(error, pinChallenge: challenge)
        } else {
            return nil
        }
    }
}

extension LoginHandler : BridgeToLoginHandlerProtocol {
    //@todo add support for multiple authenticators
    func authenticateUser(_ profile: ONGUserProfile, completion: @escaping (ONGUserProfile?, SdkError?) -> Void) {
        loginCompletion = completion
        ONGUserClient.sharedInstance().authenticateUser(profile, delegate: self)
    }
}

extension LoginHandler: ONGAuthenticationDelegate {
    func userClient(_: ONGUserClient, didReceive challenge: ONGPinChallenge) {
        pinChallenge = challenge
        let pinError = mapErrorFromPinChallenge(challenge)
        BridgeConnector.shared?.toPinHandlerConnector.pinHandler.handleFlowUpdate(PinFlow.authentication, pinError, reciever: self)
    }

    func userClient(_: ONGUserClient, didReceive challenge: ONGCustomAuthFinishAuthenticationChallenge) {
        // Will need this in the future
    }

    func userClient(_: ONGUserClient, didAuthenticateUser userProfile: ONGUserProfile, info _: ONGCustomInfo?) {
        loginCompletion!(userProfile, nil)
        BridgeConnector.shared?.toPinHandlerConnector.pinHandler.closeFlow()
    }

    func userClient(_: ONGUserClient, didFailToAuthenticateUser profile: ONGUserProfile, error: Error) {
        if error.code == ONGGenericError.actionCancelled.rawValue {
            loginCompletion!(nil, SdkError(errorDescription: "Login cancelled."))
            BridgeConnector.shared?.toPinHandlerConnector.pinHandler.closeFlow()
        } else {
            let mappedError = ErrorMapper().mapError(error)
            loginCompletion!(nil, mappedError)
        }
    }
}
