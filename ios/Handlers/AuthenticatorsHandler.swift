protocol BridgeToAuthenticatorsHandlerProtocol: AnyObject {
    func registerAuthenticator(_ userProfile: ONGUserProfile,_ authenticatorType: ONGAuthenticatorType, _ completion: @escaping (Bool, NSError?) -> Void)
    func deregisterAuthenticator(_ userProfile: ONGUserProfile, _ authenticatorId: ONGAuthenticatorType, _ completion: @escaping (Bool, NSError?) -> Void)
    func setPreferredAuthenticator(_ userProfile: ONGUserProfile, _ authenticatorId: String, _ completion: @escaping (Bool, NSError?) -> Void)
    func getAuthenticatorsListForUserProfile(_ userProfile: ONGUserProfile) -> Array<ONGAuthenticator>
    func isAuthenticatorRegistered(_ authenticatorType: ONGAuthenticatorType, _ userProfile: ONGUserProfile) -> Bool
}

class AuthenticatorsHandler: NSObject, PinHandlerToReceiverProtocol {
    var pinChallenge: ONGPinChallenge?
    var registrationCompletion: ((Bool, NSError?) -> Void)?
    var deregistrationCompletion: ((Bool, NSError?) -> Void)?

    func handlePin(pin: String?) {
        guard let pinChallenge = self.pinChallenge else { return }

        if(pin != nil) {
            pinChallenge.sender.respond(withPin: pin!, challenge: pinChallenge)

        } else {
            pinChallenge.sender.cancel(pinChallenge)
        }
    }

    fileprivate func mapErrorFromPinChallenge(_ challenge: ONGPinChallenge) -> NSError? {
        if let error = challenge.error {
            return error as NSError
        } else {
            return nil
        }
    }

    fileprivate func sortAuthenticatorsList(_ authenticators: Array<ONGAuthenticator>) -> Array<ONGAuthenticator> {
        return authenticators.sorted {
            if $0.type.rawValue == $1.type.rawValue {
                return $0.name < $1.name
            } else {
                return $0.type.rawValue < $1.type.rawValue
            }
        }
    }
}

extension AuthenticatorsHandler: BridgeToAuthenticatorsHandlerProtocol {
    func registerAuthenticator(_ userProfile: ONGUserProfile, _ authenticatorType: ONGAuthenticatorType,_ completion: @escaping (Bool, NSError?) -> Void) {
        guard let authenticator = ONGUserClient.sharedInstance().allAuthenticators(forUser: userProfile).first(where: {$0.type.rawValue == authenticatorType.rawValue}) else {
            let error = NSError(domain: ONGAuthenticatorRegistrationErrorDomain, code: ONGAuthenticatorRegistrationError.authenticatorNotSupported.rawValue, userInfo: [NSLocalizedDescriptionKey : "This authenticator is not available."])
            completion(false, error)
            return;
        }

        if(authenticator.isRegistered == true) {
            let error = NSError(domain: ONGAuthenticatorRegistrationErrorDomain, code: ONGAuthenticatorRegistrationError.authenticatorAlreadyRegistered.rawValue, userInfo: [NSLocalizedDescriptionKey : "This authenticator is already registered."])
            completion(false, error)
            return;
        }

        registrationCompletion = completion;
        ONGUserClient.sharedInstance().register(authenticator, delegate: self);
    }

    func deregisterAuthenticator(_ userProfile: ONGUserProfile, _ authenticatorType: ONGAuthenticatorType,_ completion: @escaping (Bool, NSError?) -> Void) {
        guard let authenticator = ONGUserClient.sharedInstance().allAuthenticators(forUser: userProfile).first(where: {$0.type.rawValue == authenticatorType.rawValue}) else {
            let error = NSError(domain: ONGAuthenticatorDeregistrationErrorDomain, code: ONGAuthenticatorDeregistrationError.deregistrationErrorAuthenticatorNotSupported.rawValue, userInfo: [NSLocalizedDescriptionKey : "This authenticator is not available."])
            completion(false, error)
            return;
        }

        if(authenticator.isRegistered != true) {
            let error = NSError(domain: ONGAuthenticatorDeregistrationErrorDomain, code: ONGAuthenticatorDeregistrationError.deregistrationErrorAuthenticatorNotSupported.rawValue, userInfo: [NSLocalizedDescriptionKey : "This authenticator is not registered."])
            completion(false, error)
            return;
        }

        deregistrationCompletion = completion;
        ONGUserClient.sharedInstance().deregister(authenticator, delegate: self)
    }

    func setPreferredAuthenticator(_ userProfile: ONGUserProfile, _ authenticatorId: String,_ completion: @escaping (Bool, NSError?) -> Void) {
        guard let authenticator = ONGUserClient.sharedInstance().allAuthenticators(forUser: userProfile).first(where: {$0.identifier == authenticatorId}) else {
            let error = NSError(domain: ONGAuthenticatorRegistrationErrorDomain, code: ONGAuthenticatorRegistrationError.authenticatorNotSupported.rawValue, userInfo: [NSLocalizedDescriptionKey : "This authenticator is not available."])
            completion(false, error)
            return;
        }

        if(authenticator.isRegistered != true) {
            let error = NSError(domain: ONGAuthenticatorRegistrationErrorDomain, code: ONGAuthenticatorRegistrationError.authenticatorNotSupported.rawValue, userInfo: [NSLocalizedDescriptionKey : "This authenticator is not registered."])
            completion(false, error)
            return;
        }

        ONGUserClient.sharedInstance().preferredAuthenticator = authenticator
        completion(true, nil)
    }

    func getAuthenticatorsListForUserProfile(_ userProfile: ONGUserProfile) -> Array<ONGAuthenticator> {
        let authenticatros = ONGUserClient.sharedInstance().allAuthenticators(forUser: userProfile)
        return sortAuthenticatorsList(Array(authenticatros))
    }

    func isAuthenticatorRegistered(_ authenticatorType: ONGAuthenticatorType, _ userProfile: ONGUserProfile) -> Bool {
        return ONGUserClient.sharedInstance().registeredAuthenticators(forUser: userProfile).first(where: {$0.type.rawValue == authenticatorType.rawValue }) != nil;
    }
}

extension AuthenticatorsHandler: ONGAuthenticatorRegistrationDelegate {
    func userClient(_: ONGUserClient, didReceive challenge: ONGPinChallenge) {
        pinChallenge = challenge
        let pinError = mapErrorFromPinChallenge(challenge)
        BridgeConnector.shared?.toPinHandlerConnector.pinHandler.handleFlowUpdate(PinFlow.authentication, error: pinError, receiver: self, profileId: challenge.userProfile.profileId, userInfo: nil, data: nil)
    }

    func userClient(_: ONGUserClient, didReceive challenge: ONGCustomAuthFinishRegistrationChallenge) {
        // Will need this in the future
    }

    func userClient(_: ONGUserClient, didFailToRegister authenticator: ONGAuthenticator, forUser _: ONGUserProfile, error: Error) {
        BridgeConnector.shared?.toPinHandlerConnector.pinHandler.closeFlow()
        registrationCompletion!(false, error as NSError)
    }

    func userClient(_: ONGUserClient, didRegister authenticator: ONGAuthenticator, forUser _: ONGUserProfile, info _: ONGCustomInfo?) {
        registrationCompletion!(true, nil)
        BridgeConnector.shared?.toPinHandlerConnector.pinHandler.closeFlow()
    }
}

extension AuthenticatorsHandler: ONGAuthenticatorDeregistrationDelegate {
    func userClient(_: ONGUserClient, didDeregister _: ONGAuthenticator, forUser _: ONGUserProfile) {
        deregistrationCompletion!(true, nil)
        BridgeConnector.shared?.toPinHandlerConnector.pinHandler.closeFlow()
    }

    func userClient(_: ONGUserClient, didReceive challenge: ONGCustomAuthDeregistrationChallenge) {
        // will need this in the future
    }

    func userClient(_: ONGUserClient, didFailToDeregister authenticator: ONGAuthenticator, forUser _: ONGUserProfile, error: Error) {
        BridgeConnector.shared?.toPinHandlerConnector.pinHandler.closeFlow()
        deregistrationCompletion!(false, error as NSError)
    }
}
