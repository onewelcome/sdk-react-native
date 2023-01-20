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
        ONGUserClient.sharedInstance().changePin(self)
        changePinCompletion = completion
    }
 }

extension ChangePinHandler: ONGChangePinDelegate {
    func userClient(_ userClient: ONGUserClient, didReceive challenge: ONGPinChallenge) {
        loginHandler.handleDidReceiveChallenge(challenge)
    }

    func userClient(_: ONGUserClient, didReceive challenge: ONGCreatePinChallenge) {
        loginHandler.handleDidAuthenticateUser()
        registrationHandler.handleDidReceivePinRegistrationChallenge(challenge)
    }
    
    func userClient(_: ONGUserClient, didFailToChangePinForUser profile: ONGUserProfile, error: Error) {
        loginHandler.handleDidFailToAuthenticateUser()
        registrationHandler.handleDidFailToRegister()
        changePinCompletion?(error)
        changePinCompletion = nil
    }

    func userClient(_ : ONGUserClient, didChangePinForUser _: ONGUserProfile) {
        registrationHandler.handleDidRegisterUser()
        changePinCompletion?(nil)
        changePinCompletion = nil
    }
}
