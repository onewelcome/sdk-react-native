protocol BridgeToLoginHandlerProtocol: AnyObject {
    func authenticateUser(_ profile: UserProfile,
                          authenticator: Authenticator?,
                          completion: @escaping (Result<RegistrationResponse, Error>) -> Void)
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

extension LoginHandler: BridgeToLoginHandlerProtocol {
    func authenticateUser(_ profile: UserProfile,
                          authenticator: Authenticator? = nil,
                          completion: @escaping (Result<RegistrationResponse, Error>) -> Void
    ) {
        let delegate = AuthenticationDelegateImpl(loginHandler: self, completion: completion)
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

class AuthenticationDelegateImpl: AuthenticationDelegate {
    private let completion: (Result<RegistrationResponse, Error>) -> Void
    private let loginHandler: LoginHandler

    init(loginHandler: LoginHandler, completion: @escaping (Result<RegistrationResponse, Error>) -> Void) {
        self.completion = completion
        self.loginHandler = loginHandler
    }

    func userClient(_ userClient: UserClient, didReceivePinChallenge challenge: PinChallenge) {
        loginHandler.handleDidReceiveChallenge(challenge)
    }

    func userClient(_ userClient: UserClient, didStartAuthenticationForUser profile: UserProfile, authenticator: Authenticator) {
        // unused
    }

    func userClient(_ userClient: UserClient, didReceiveCustomAuthFinishAuthenticationChallenge challenge: CustomAuthFinishAuthenticationChallenge) {
        // We don't support custom authenticators in React Native plugin right now.
    }

    func userClient(_ userClient: UserClient, didAuthenticateUser profile: UserProfile, authenticator: Authenticator, info customAuthInfo: CustomInfo?) {
        loginHandler.handleDidAuthenticateUser()
        completion(.success(RegistrationResponse(userProfile: profile, customInfo: customAuthInfo)))
    }

    func userClient(_ userClient: UserClient, didFailToAuthenticateUser profile: UserProfile, authenticator: Authenticator, error: Error) {
        completion(.failure(error))
        // We don't want to send a close pin event when we encounter an action already in progress
        if error.code == ONGGenericError.actionAlreadyInProgress.rawValue { return }
        loginHandler.handleDidFailToAuthenticateUser()

    }
}

private func mapErrorFromPinChallenge(_ challenge: PinChallenge) -> Error? {
    if let error = challenge.error, error.code != ONGAuthenticationError.touchIDAuthenticatorFailure.rawValue {
        return error
    } else {
        return nil
    }
}
