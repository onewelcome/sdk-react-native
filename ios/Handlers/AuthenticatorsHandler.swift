protocol BridgeToAuthenticatorsHandlerProtocol: AnyObject {
    func registerAuthenticator(_ userProfile: ONGUserProfile,_ authenticatorType: ONGAuthenticatorType, _ completion: @escaping (Bool, Error?) -> Void)
    func deregisterAuthenticator(_ userProfile: ONGUserProfile, _ authenticatorId: ONGAuthenticatorType, _ completion: @escaping (Bool, Error?) -> Void)
    func setPreferredAuthenticator(_ userProfile: ONGUserProfile, _ authenticatorId: String, _ completion: @escaping (Bool, Error?) -> Void)
    func getAuthenticatorsListForUserProfile(_ userProfile: ONGUserProfile) -> Array<ONGAuthenticator>
    func isAuthenticatorRegistered(_ authenticatorType: ONGAuthenticatorType, _ userProfile: ONGUserProfile) -> Bool
}

class AuthenticatorsHandler: NSObject {
    private var pinChallenge: ONGPinChallenge?
    private var registrationCompletion: ((Bool, Error?) -> Void)?
    private var deregistrationCompletion: ((Bool, Error?) -> Void)?
    private let pinAuthenticationEventEmitter = PinAuthenticationEventEmitter()
    private let createPinEventEmitter = CreatePinEventEmitter()

    func handlePin(_ pin: String?) {
        guard let pinChallenge = self.pinChallenge else { return }
        guard let pin = pin else {
            pinChallenge.sender.cancel(pinChallenge)
            return
        }
        pinChallenge.sender.respond(withPin: pin, challenge: pinChallenge)
    }

    fileprivate func mapErrorFromPinChallenge(_ challenge: ONGPinChallenge) -> Error? {
        if let error = challenge.error {
            return error
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
    func registerAuthenticator(_ userProfile: ONGUserProfile, _ authenticatorType: ONGAuthenticatorType,_ completion: @escaping (Bool, Error?) -> Void) {
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

    func deregisterAuthenticator(_ userProfile: ONGUserProfile, _ authenticatorType: ONGAuthenticatorType,_ completion: @escaping (Bool, Error?) -> Void) {
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

    func setPreferredAuthenticator(_ userProfile: ONGUserProfile, _ authenticatorId: String,_ completion: @escaping (Bool, Error?) -> Void) {
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
        if let pinError = mapErrorFromPinChallenge(challenge) {
            pinAuthenticationEventEmitter.onIncorrectPin(error: pinError, remainingFailureCount: challenge.remainingFailureCount)
        } else {
            pinAuthenticationEventEmitter.onPinOpen(profileId: challenge.userProfile.profileId)
        }

    }

    func userClient(_: ONGUserClient, didReceive challenge: ONGCustomAuthFinishRegistrationChallenge) {
        // Will need this in the future
    }

    func userClient(_: ONGUserClient, didFailToRegister authenticator: ONGAuthenticator, forUser _: ONGUserProfile, error: Error) {
        createPinEventEmitter.onPinClose()
        registrationCompletion?(false, error)
        registrationCompletion = nil
    }

    func userClient(_: ONGUserClient, didRegister authenticator: ONGAuthenticator, forUser _: ONGUserProfile, info _: ONGCustomInfo?) {
        createPinEventEmitter.onPinClose()
        registrationCompletion?(true, nil)
        registrationCompletion = nil
    }
}

extension AuthenticatorsHandler: ONGAuthenticatorDeregistrationDelegate {
    func userClient(_: ONGUserClient, didDeregister _: ONGAuthenticator, forUser _: ONGUserProfile) {
        createPinEventEmitter.onPinClose()
        deregistrationCompletion?(true, nil)
        deregistrationCompletion = nil
    }

    func userClient(_: ONGUserClient, didReceive challenge: ONGCustomAuthDeregistrationChallenge) {
        // will need this in the future
    }

    func userClient(_: ONGUserClient, didFailToDeregister authenticator: ONGAuthenticator, forUser _: ONGUserProfile, error: Error) {
        createPinEventEmitter.onPinClose()
        deregistrationCompletion?(false, error)
        deregistrationCompletion = nil
    }
}
