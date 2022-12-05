protocol BridgeToLoginHandlerProtocol: AnyObject {
    func authenticateUser(_ profile: ONGUserProfile, authenticator: ONGAuthenticator?, completion: @escaping (ONGUserProfile?, Error?) -> Void)
    func setAuthPinChallenge(_ challenge: ONGPinChallenge?)
    func handlePin(_ pin: String?) throws
    func cancelPinAuthentication() throws
}


class LoginHandler: NSObject {
    private var pinChallenge: ONGPinChallenge?
    private var loginCompletion: ((ONGUserProfile?, Error?) -> Void)?
    private let pinAuthenticationEventEmitter = PinAuthenticationEventEmitter()

    func handlePin(_ pin: String?) throws {
        guard let pinChallenge = self.pinChallenge else { throw WrapperError.authenticationNotInProgress }
        guard let pin = pin else {
            pinChallenge.sender.cancel(pinChallenge)
            return
        }
        pinChallenge.sender.respond(withPin: pin, challenge: pinChallenge)
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
    func authenticateUser(_ profile: ONGUserProfile, authenticator: ONGAuthenticator? = nil, completion: @escaping (ONGUserProfile?, Error?) -> Void) {
        loginCompletion = completion
        ONGUserClient.sharedInstance().authenticateUser(profile, authenticator: authenticator, delegate: self)
    }
    func setAuthPinChallenge(_ challenge: ONGPinChallenge?) {
        pinChallenge = challenge
    }
    func cancelPinAuthentication() throws {
        guard let pinChallenge = self.pinChallenge else { throw WrapperError.authenticationNotInProgress }
        pinChallenge.sender.cancel(pinChallenge)
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
