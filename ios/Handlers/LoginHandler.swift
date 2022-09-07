protocol BridgeToLoginHandlerProtocol: AnyObject {
    func authenticateUser(_ profile: ONGUserProfile, authenticator: ONGAuthenticator?, completion: @escaping (ONGUserProfile?, Error?) -> Void)
    func setAuthPinChallenge(_ challenge: ONGPinChallenge?)
    func handlePinAction(_ pin: String, action: String)
}


class LoginHandler: NSObject {
    var pinChallenge: ONGPinChallenge?
    var loginCompletion: ((ONGUserProfile?, Error?) -> Void)?
    let pinAuthenticationEventEmitter = PinAuthenticationEventEmitter()

    func handlePin(_ pin: String?) {
        guard let pinChallenge = self.pinChallenge else { return }

        if(pin != nil) {
            pinChallenge.sender.respond(withPin: pin!, challenge: pinChallenge)
        } else {
            pinChallenge.sender.cancel(pinChallenge)
        }
    }

    fileprivate func mapErrorFromPinChallenge(_ challenge: ONGPinChallenge) -> Error? {
        if let error = challenge.error, error.code != ONGAuthenticationError.touchIDAuthenticatorFailure.rawValue {
            return error
        } else {
            return nil
        }
    }
    
    func handleDidReceiveChallenge(_ challenge: ONGPinChallenge) {
        pinChallenge = challenge
        if let pinError = mapErrorFromPinChallenge(challenge) {
            if (challenge.remainingFailureCount != challenge.maxFailureCount) {
                pinAuthenticationEventEmitter.onWrongPin(error: pinError, remainingFailureCount: challenge.remainingFailureCount)
            } else {
                pinAuthenticationEventEmitter.onPinError(error: pinError)
            }
        } else {
            pinAuthenticationEventEmitter.onPinOpen(profileId: challenge.userProfile.profileId)
        }
    }
    
    func handleDidFailToAuthenticateUser() {
        pinChallenge = nil
        pinAuthenticationEventEmitter.onPinClose()
    }
    
    func handleDidAuthenticateUser() {
        pinChallenge = nil
        pinAuthenticationEventEmitter.onPinClose()
    }

}

extension LoginHandler : BridgeToLoginHandlerProtocol {
    func authenticateUser(_ profile: ONGUserProfile, authenticator: ONGAuthenticator? = nil, completion: @escaping (ONGUserProfile?, Error?) -> Void) {
        loginCompletion = completion
        ONGUserClient.sharedInstance().authenticateUser(profile, authenticator: authenticator, delegate: self)
    }
    func setAuthPinChallenge(_ challenge: ONGPinChallenge?) {
        pinChallenge = challenge
    }
    func handlePinAction(_ pin: String, action: String) {
        switch action {
            case PinAction.provide.rawValue:
                handlePin(pin)
            case PinAction.cancel.rawValue:
                handlePin(nil)
            default:
                return
        }
    }
}

extension LoginHandler: ONGAuthenticationDelegate {
    func userClient(_ : ONGUserClient, didReceive challenge: ONGPinChallenge) {
        handleDidReceiveChallenge(challenge)
    }

    func userClient(_: ONGUserClient, didReceive challenge: ONGCustomAuthFinishAuthenticationChallenge) {
        // Will need this in the future
    }

    func userClient(_: ONGUserClient, didReceive challenge: ONGBiometricChallenge) {
        challenge.sender.respondWithPinFallback(for: challenge)
    }
    
    func userClient(_ userClient: ONGUserClient, didAuthenticateUser userProfile: ONGUserProfile, authenticator: ONGAuthenticator, info customAuthInfo: ONGCustomInfo?) {
        handleDidAuthenticateUser()
        loginCompletion?(userProfile, nil)
        loginCompletion = nil
    }
    
    func userClient(_ userClient: ONGUserClient, didFailToAuthenticateUser userProfile: ONGUserProfile, authenticator: ONGAuthenticator, error: Error) {
        handleDidFailToAuthenticateUser()
        // ChangePinHandler also makes use of the handle function but has it's own seperate completion callback, so let's leave the loginCompletion here.
        loginCompletion?(nil, error)
        loginCompletion = nil
    }
}
