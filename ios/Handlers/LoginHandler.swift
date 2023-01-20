protocol BridgeToLoginHandlerProtocol: AnyObject {
    func authenticateUser(_ profile: ONGUserProfile, authenticator: ONGAuthenticator?, completion: @escaping (ONGUserProfile, Error?) -> Void)
    func setAuthPinChallenge(_ challenge: ONGPinChallenge?)
    func handlePin(_ pin: String?, completion: @escaping (Error?) -> Void)
    func cancelPinAuthentication(completion: @escaping (Error?) -> Void)
}


class LoginHandler: NSObject {
    private var pinChallenge: ONGPinChallenge?
    private var loginCompletion: ((ONGUserProfile, Error?) -> Void)?
    private let pinAuthenticationEventEmitter = PinAuthenticationEventEmitter()

    func handlePin(_ pin: String?, completion: @escaping (Error?) -> Void) {
        guard let pinChallenge = self.pinChallenge else {
            completion(WrapperError.authenticationNotInProgress)
            return
        }
        guard let pin = pin else {
            pinChallenge.sender.cancel(pinChallenge)
            completion(nil)
            return
        }
        pinChallenge.sender.respond(withPin: pin, challenge: pinChallenge)
        completion(nil)
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
            pinAuthenticationEventEmitter.onIncorrectPin(error: pinError, remainingFailureCount: challenge.remainingFailureCount)
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
    func authenticateUser(_ profile: ONGUserProfile, authenticator: ONGAuthenticator? = nil, completion: @escaping (ONGUserProfile, Error?) -> Void) {
        loginCompletion = completion
        ONGUserClient.sharedInstance().authenticateUser(profile, authenticator: authenticator, delegate: self)
    }
    func setAuthPinChallenge(_ challenge: ONGPinChallenge?) {
        pinChallenge = challenge
    }
    func cancelPinAuthentication(completion: @escaping (Error?) -> Void) {
        guard let pinChallenge = self.pinChallenge else {
            completion(WrapperError.authenticationNotInProgress)
            return
        }
        pinChallenge.sender.cancel(pinChallenge)
        completion(nil)
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
        challenge.sender.respond(withPrompt: "", challenge: challenge)
    }
    
    func userClient(_ userClient: ONGUserClient, didAuthenticateUser userProfile: ONGUserProfile, authenticator: ONGAuthenticator, info customAuthInfo: ONGCustomInfo?) {
        handleDidAuthenticateUser()
        loginCompletion?(userProfile, nil)
        loginCompletion = nil
    }
    
    func userClient(_ userClient: ONGUserClient, didFailToAuthenticateUser userProfile: ONGUserProfile, authenticator: ONGAuthenticator, error: Error) {
        handleDidFailToAuthenticateUser()
        // ChangePinHandler also makes use of the handle function but has it's own seperate completion callback, so let's leave the loginCompletion here.
        loginCompletion?(userProfile, error)
        loginCompletion = nil
    }
}
