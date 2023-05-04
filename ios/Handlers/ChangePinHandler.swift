protocol PinConnectorToPinHandler: AnyObject {
    func onChangePinCalled(completion: @escaping (Result<Void, Error>) -> Void)
}

class ChangePinHandler: NSObject {
    private var flow: PinFlow?
    private let pinAuthenticationEventEmitter = PinAuthenticationEventEmitter()
    private let createPinEventEmitter = CreatePinEventEmitter()
    private let loginHandler: LoginHandler
    private let registrationHandler: RegistrationHandler

    init(loginHandler: LoginHandler, registrationHandler: RegistrationHandler) {
        self.loginHandler = loginHandler
        self.registrationHandler = registrationHandler
    }

}

extension ChangePinHandler: PinConnectorToPinHandler {
    func onChangePinCalled(completion: @escaping (Result<Void, Error>) -> Void) {
        let delegate = ChangePinDelegateImpl(loginHandler: loginHandler, registrationHandler: registrationHandler, completion: completion)
        SharedUserClient.instance.changePin(delegate: delegate)
    }
 }

class ChangePinDelegateImpl: ChangePinDelegate {
    private let completion: ((Result<Void, Error>) -> Void)
    private let loginHandler: LoginHandler
    private let registrationHandler: RegistrationHandler

    init(loginHandler: LoginHandler, registrationHandler: RegistrationHandler, completion: @escaping (Result<Void, Error>) -> Void) {
        self.completion = completion
        self.loginHandler = loginHandler
        self.registrationHandler = registrationHandler
    }

    func userClient(_ userClient: UserClient, didReceivePinChallenge challenge: PinChallenge) {
        loginHandler.handleDidReceiveChallenge(challenge)
    }

    func userClient(_ userClient: UserClient, didReceiveCreatePinChallenge challenge: CreatePinChallenge) {
        loginHandler.handleDidAuthenticateUser()
        registrationHandler.handleDidReceivePinRegistrationChallenge(challenge)
    }

    func userClient(_ userClient: UserClient, didStartPinChangeForUser profile: UserProfile) {
        // Unused
    }

    func userClient(_ userClient: UserClient, didChangePinForUser profile: UserProfile) {
        registrationHandler.handleDidRegisterUser()
        completion(.success)
    }

    func userClient(_ userClient: UserClient, didFailToChangePinForUser profile: UserProfile, error: Error) {
        loginHandler.handleDidFailToAuthenticateUser()
        registrationHandler.handleDidFailToRegister()
        completion(.failure(error))
    }
}
