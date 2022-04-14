protocol BridgeToLoginHandlerProtocol: AnyObject {
    func authenticateUser(_ profile: ONGUserProfile, completion: @escaping (ONGUserProfile?, NSError?) -> Void)
}

class LoginHandler: NSObject, PinHandlerToReceiverProtocol {
    var pinChallenge: ONGPinChallenge?
    var loginCompletion: ((ONGUserProfile?, NSError?) -> Void)?


    func handlePin(pin: String?) {
        guard let pinChallenge = self.pinChallenge else { return }

        if(pin != nil) {
            pinChallenge.sender.respond(withPin: pin!, challenge: pinChallenge)

        } else {
            pinChallenge.sender.cancel(pinChallenge)
        }
    }

    fileprivate func mapErrorFromPinChallenge(_ challenge: ONGPinChallenge) -> NSError? {
        if let error = challenge.error, error.code != ONGAuthenticationError.touchIDAuthenticatorFailure.rawValue {
            return error as NSError
        } else {
            return nil
        }
    }
}

extension LoginHandler : BridgeToLoginHandlerProtocol {
    //@todo add support for multiple authenticators in the future
    func authenticateUser(_ profile: ONGUserProfile, completion: @escaping (ONGUserProfile?, NSError?) -> Void) {
        loginCompletion = completion
        ONGUserClient.sharedInstance().authenticateUser(profile, delegate: self)
    }
}

extension LoginHandler: ONGAuthenticationDelegate {
    func userClient(_ : ONGUserClient, didReceive challenge: ONGPinChallenge) {
        pinChallenge = challenge
        let pinError = mapErrorFromPinChallenge(challenge)
        let pinConfig = DefaultKeysUtil.getPinConfig(profileId: challenge.userProfile.profileId)
        BridgeConnector.shared?.toPinHandlerConnector.pinHandler.handleFlowUpdate(PinFlow.authentication, error: pinError, receiver: self, userInfo: challenge.userInfo, data: pinConfig?.pinLength)
    }

    func userClient(_: ONGUserClient, didReceive challenge: ONGCustomAuthFinishAuthenticationChallenge) {
        // Will need this in the future
    }

    func userClient(_: ONGUserClient, didReceive challenge: ONGBiometricChallenge) {
        challenge.sender.respondWithDefaultPrompt(for: challenge)
    }

    func userClient(_: ONGUserClient, didAuthenticateUser userProfile: ONGUserProfile, info _: ONGCustomInfo?) {
        pinChallenge = nil
        loginCompletion!(userProfile, nil)
        BridgeConnector.shared?.toPinHandlerConnector.pinHandler.closeFlow()
    }

    func userClient(_: ONGUserClient, didFailToAuthenticateUser profile: ONGUserProfile, error: Error) {
        pinChallenge = nil
        BridgeConnector.shared?.toPinHandlerConnector.pinHandler.closeFlow()
        loginCompletion!(nil, error as NSError)
    }
}


extension ONGPinChallenge {
    enum UserInfoKey {
        static let maxFailureCount = "maxFailureCount"
        static let previousFailureCount = "previousFailureCount"
        static let remainingFailureCount = "remainingFailureCount"
    }
    
    var userInfo: [String: Any] {
        [
            UserInfoKey.maxFailureCount: maxFailureCount,
            UserInfoKey.previousFailureCount: previousFailureCount,
            UserInfoKey.remainingFailureCount: remainingFailureCount
        ]
    }
}
