protocol PinConnectorToPinHandler: AnyObject {
    func onChangePinCalled(completion: @escaping (Bool, NSError?) -> Void)
}

enum PINEntryMode {
    case login
    case registration
    
}

class ChangePinHandler: NSObject {
    var flow: PinFlow?
    var changePinCompletion: ((Bool, NSError?) -> Void)?
    let pinAuthenticationEventEmitter = PinAuthenticationEventEmitter()
    let createPinEventEmitter = CreatePinEventEmitter()
}

extension ChangePinHandler : PinConnectorToPinHandler {
    func onChangePinCalled(completion: @escaping (Bool, NSError?) -> Void) {
        ONGUserClient.sharedInstance().changePin(self)
        changePinCompletion = completion
    }
 }

extension ChangePinHandler: ONGChangePinDelegate {
    func userClient(_ userClient: ONGUserClient, didReceive challenge: ONGPinChallenge) {
        BridgeConnector.shared?.toLoginHandler.handleDidReceiveChallenge(challenge)
    }

    func userClient(_: ONGUserClient, didReceive challenge: ONGCreatePinChallenge) {
        BridgeConnector.shared?.toLoginHandler.handleDidAuthenticateUser()
        BridgeConnector.shared?.toRegistrationConnector.registrationHandler.handleDidReceivePinRegistrationChallenge(challenge)
    }
    
    func userClient(_: ONGUserClient, didFailToChangePinForUser profile: ONGUserProfile, error: Error) {
        BridgeConnector.shared?.toLoginHandler.handleDidFailToAuthenticateUser()
        BridgeConnector.shared?.toRegistrationConnector.registrationHandler.handleDidFailToRegister()
        changePinCompletion!(false, error as NSError)
    }

    func userClient(_ : ONGUserClient, didChangePinForUser _: ONGUserProfile) {
        BridgeConnector.shared?.toRegistrationConnector.registrationHandler.handleDidRegisterUser()
        changePinCompletion!(true, nil)
    }
}
