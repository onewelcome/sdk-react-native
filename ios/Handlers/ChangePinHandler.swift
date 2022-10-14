protocol PinConnectorToPinHandler: AnyObject {
    func onChangePinCalled(completion: @escaping (Bool, Error?) -> Void)
}

enum PINEntryMode {
    case login
    case registration
    
}

class ChangePinHandler: NSObject {
    private var flow: PinFlow?
    private var changePinCompletion: ((Bool, Error?) -> Void)?
    private let pinAuthenticationEventEmitter = PinAuthenticationEventEmitter()
    private let createPinEventEmitter = CreatePinEventEmitter()
    private let loginHandler: LoginHandler
    private let registrationHandler: RegistrationConnectorToHandlerProtocol
    
    init(loginHandler: LoginHandler, registrationHandler: RegistrationConnectorToHandlerProtocol) {
        self.loginHandler = loginHandler
        self.registrationHandler = registrationHandler
    }
    
}

extension ChangePinHandler: PinConnectorToPinHandler {
    func onChangePinCalled(completion: @escaping (Bool, Error?) -> Void) {
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
        changePinCompletion?(false, error)
        changePinCompletion = nil
    }

    func userClient(_ : ONGUserClient, didChangePinForUser _: ONGUserProfile) {
        registrationHandler.handleDidRegisterUser()
        changePinCompletion?(true, nil)
        changePinCompletion = nil
    }
}
