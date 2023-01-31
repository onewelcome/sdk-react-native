protocol BridgeToLoginHandlerProtocol: AnyObject {
    func authenticateUser(_ profile: UserProfile, authenticator: Authenticator?, completion: @escaping (UserProfile, Error?) -> Void)
    func setAuthPinChallenge(_ challenge: PinChallenge?)
    func handlePin(_ pin: String?, completion: @escaping (Error?) -> Void)
    func cancelPinAuthentication(completion: @escaping (Error?) -> Void)
}


class LoginHandler: NSObject {
    private var pinChallenge: PinChallenge?
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
        pinChallenge.sender.respond(with: pin, to: pinChallenge)
        completion(nil)
    }

    func handleDidReceiveChallenge(_ challenge: PinChallenge) {
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
    func authenticateUser(_ profile: UserProfile, authenticator: Authenticator? = nil, completion: @escaping (UserProfile, Error?) -> Void) {
        let delegate = loginDelegate(loginCompletion: completion)
        SharedUserClient.instance.authenticateUserWith(profile: profile, authenticator: authenticator, delegate: delegate)
    }
    
    func setAuthPinChallenge(_ challenge: PinChallenge?) {
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

class loginDelegate: AuthenticationDelegate {

    
    private var loginCompletion: ((UserProfile, Error?) -> Void)?
    
    init(loginCompletion: ((UserProfile, Error?) -> Void)?) {
        self.loginCompletion = loginCompletion
    }
    
    func userClient(_ userClient: UserClient, didReceivePinChallenge challenge: PinChallenge) {
        BridgeConnector.shared?.toLoginHandler.handleDidReceiveChallenge(challenge)
    }
    
    func userClient(_ userClient: UserClient, didReceiveBiometricChallenge challenge: BiometricChallenge) {
        challenge.sender.respond(with: "", to: challenge)
    }

    func userClient(_ userClient: OneginiSDKiOS.UserClient, didAuthenticateUser profile: OneginiSDKiOS.UserProfile, authenticator: OneginiSDKiOS.Authenticator, info customAuthInfo: OneginiSDKiOS.CustomInfo?) {
            BridgeConnector.shared?.toLoginHandler.handleDidAuthenticateUser()
            loginCompletion?(profile, nil)
            loginCompletion = nil
    }
    
    func userClient(_ userClient: OneginiSDKiOS.UserClient, didFailToAuthenticateUser profile: OneginiSDKiOS.UserProfile, authenticator: OneginiSDKiOS.Authenticator, error: Error) {
        loginCompletion?(profile, error)
        loginCompletion = nil
        switch error.code {
            // We don't want to send a close pin event when we encounter an action already in progress
        case ONGGenericError.actionAlreadyInProgress.rawValue:
            return
        default:
            break
        }
        BridgeConnector.shared?.toLoginHandler.handleDidFailToAuthenticateUser()
    }
}


fileprivate func mapErrorFromPinChallenge(_ challenge: PinChallenge) -> Error? {
    if let error = challenge.error, error.code != ONGAuthenticationError.touchIDAuthenticatorFailure.rawValue {
        return error
    } else {
        return nil
    }
}
