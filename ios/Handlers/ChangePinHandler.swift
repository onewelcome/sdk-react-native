protocol PinConnectorToPinHandler: AnyObject {
    func onChangePinCalled(completion: @escaping (Error?) -> Void)
}

enum PINEntryMode {
    case login
    case registration

}

class ChangePinHandler: NSObject {
    private var flow: PinFlow?
    private var changePinCompletion: ((Error?) -> Void)?
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
    func onChangePinCalled(completion: @escaping (Error?) -> Void) {
        SharedUserClient.instance.changePin(delegate: self)
        changePinCompletion = completion
    }
 }

extension ChangePinHandler: ChangePinDelegate {
    func userClient(_ userClient: UserClient, didReceivePinChallenge challenge: PinChallenge) {
        loginHandler.handleDidReceiveChallenge(challenge)
    }

    func userClient(_ userClient: UserClient, didReceiveCreatePinChallenge challenge: CreatePinChallenge) {
        loginHandler.handleDidAuthenticateUser()
        registrationHandler.handleDidReceivePinRegistrationChallenge(challenge)
    }

    func userClient(_ userClient: UserClient, didStartPinChangeForUser profile: UserProfile) {
        // Not used
    }

    func userClient(_ userClient: UserClient, didChangePinForUser profile: UserProfile) {
        registrationHandler.handleDidRegisterUser()
        changePinCompletion?(nil)
        changePinCompletion = nil
    }

    func userClient(_ userClient: UserClient, didFailToChangePinForUser profile: UserProfile, error: Error) {
        loginHandler.handleDidFailToAuthenticateUser()
        registrationHandler.handleDidFailToRegister()
        changePinCompletion?(error)
        changePinCompletion = nil
    }
}
