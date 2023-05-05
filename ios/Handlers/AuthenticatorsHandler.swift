import OneginiSDKiOS
protocol BridgeToAuthenticatorsHandlerProtocol: AnyObject {
    func registerAuthenticator(_ userProfile: UserProfile, _ authenticatorId: String, completion: @escaping (Result<Void, Error>) -> Void)
    func deregisterAuthenticator(_ userProfile: UserProfile, _ authenticatorId: String, completion: @escaping (Result<Void, Error>) -> Void)
    func setPreferredAuthenticator(_ userProfile: UserProfile, _ authenticatorId: String, completion: @escaping (Result<Void, Error>) -> Void)
    func getAuthenticatorsListForUserProfile(_ userProfile: UserProfile) -> [Authenticator]
    func isAuthenticatorRegistered(_ authenticatorType: AuthenticatorType, _ userProfile: UserProfile) -> Bool
}

class AuthenticatorsHandler: NSObject {
    private let pinAuthenticationEventEmitter = PinAuthenticationEventEmitter()
    private let createPinEventEmitter = CreatePinEventEmitter()
    private let loginHandler: LoginHandler

    init(loginHandler: LoginHandler) {
        self.loginHandler = loginHandler
    }

    fileprivate func mapErrorFromPinChallenge(_ challenge: PinChallenge) -> Error? {
        if let error = challenge.error {
            return error
        } else {
            return nil
        }
    }
}

extension AuthenticatorsHandler: BridgeToAuthenticatorsHandlerProtocol {
    func registerAuthenticator(_ userProfile: UserProfile,
                               _ authenticatorId: String,
                               completion: @escaping (Result<Void, Error>) -> Void) {
        guard let authenticator = SharedUserClient.instance.authenticators(.all, for: userProfile).first(where: {$0.identifier == authenticatorId}) else {
            completion(.failure(WrapperError.authenticatorDoesNotExist))
            return
        }
        // We don't have to check if the authenticator is already registered as the sdk will do that for us.
        let delegate = AuthenticatorRegistrationDelegateImpl(loginHandler: loginHandler, completion: completion)
        SharedUserClient.instance.register(authenticator: authenticator, delegate: delegate)
    }

    func deregisterAuthenticator(_ userProfile: UserProfile,
                                 _ authenticatorId: String,
                                 completion: @escaping (Result<Void, Error>) -> Void) {
        guard let authenticator = SharedUserClient.instance.authenticators(.all, for: userProfile).first(where: {$0.identifier == authenticatorId}) else {
            completion(.failure(WrapperError.authenticatorDoesNotExist))
            return
        }

        if authenticator.isRegistered != true {
            completion(.failure(WrapperError.authenticatorNotRegistered))
            return
        }

        let delegate = AuthenticatorDeregistrationDelegateImpl(completion: completion)
        SharedUserClient.instance.deregister(authenticator: authenticator, delegate: delegate)
    }

    func setPreferredAuthenticator(_ userProfile: UserProfile, _ authenticatorId: String, completion: @escaping (Result<Void, Error>) -> Void) {
        guard let authenticator = SharedUserClient.instance.authenticators(.all, for: userProfile).first(where: {$0.identifier == authenticatorId}) else {
            let error = NSError(domain: ONGAuthenticatorRegistrationErrorDomain,
                                code: ONGAuthenticatorRegistrationError.authenticatorNotSupported.rawValue,
                                userInfo: [NSLocalizedDescriptionKey: "This authenticator is not available."])
            completion(.failure(error))
            return
        }

        if authenticator.isRegistered != true {
            let error = NSError(domain: ONGAuthenticatorRegistrationErrorDomain,
                                code: ONGAuthenticatorRegistrationError.authenticatorNotSupported.rawValue,
                                userInfo: [NSLocalizedDescriptionKey: "This authenticator is not registered."])
            completion(.failure(error))
            return
        }
        SharedUserClient.instance.setPreferredAuthenticator(authenticator)
        completion(.success)
    }

    func getAuthenticatorsListForUserProfile(_ userProfile: UserProfile) -> [Authenticator] {
        return SharedUserClient.instance.authenticators(.all, for: userProfile)
    }

    func isAuthenticatorRegistered(_ authenticatorType: AuthenticatorType, _ userProfile: UserProfile) -> Bool {
        return SharedUserClient.instance.authenticators(.registered, for: userProfile).first(where: {$0.type.rawValue == authenticatorType.rawValue }) != nil
    }
}

class AuthenticatorRegistrationDelegateImpl: AuthenticatorRegistrationDelegate {
    private let completion: ((Result<Void, Error>) -> Void)
    private let loginHandler: LoginHandler

    init(loginHandler: LoginHandler, completion: (@escaping (Result<Void, Error>) -> Void)) {
        self.completion = completion
        self.loginHandler = loginHandler
    }

    func userClient(_ userClient: UserClient, didReceivePinChallenge challenge: PinChallenge) {
        loginHandler.handleDidReceiveChallenge(challenge)
    }

    func userClient(_ userClient: UserClient, didReceiveCustomAuthFinishRegistrationChallenge challenge: CustomAuthFinishRegistrationChallenge) {
        // We currently don't support custom authenticators
    }

    func userClient(_ userClient: UserClient, didStartRegistering authenticator: Authenticator, for userProfile: UserProfile) {
        // Unused
    }

    func userClient(_ userClient: UserClient, didFailToRegister authenticator: Authenticator, for userProfile: UserProfile, error: Error) {
        completion(.failure(error))
    }

    func userClient(_ userClient: UserClient, didRegister authenticator: Authenticator, for userProfile: UserProfile, info customAuthInfo: CustomInfo?) {
        loginHandler.handleDidAuthenticateUser()
        completion(.success)
    }
}

class AuthenticatorDeregistrationDelegateImpl: AuthenticatorDeregistrationDelegate {
    private let completion: ((Result<Void, Error>) -> Void)

    init(completion: @escaping (Result<Void, Error>) -> Void) {
        self.completion = completion
    }

    func userClient(_ userClient: UserClient, didStartDeregistering authenticator: Authenticator, forUser userProfile: UserProfile) {
        // Unused
    }

    func userClient(_ userClient: UserClient, didDeregister authenticator: Authenticator, forUser userProfile: UserProfile) {
        completion(.success)
    }

    func userClient(_ userClient: UserClient, didFailToDeregister authenticator: Authenticator, forUser userProfile: UserProfile, error: Error) {
        completion(.failure(error))
    }

    func userClient(_ userClient: UserClient, didReceiveCustomAuthDeregistrationChallenge challenge: CustomAuthDeregistrationChallenge) {
        // We currently don't support custom authenticators
    }
}
