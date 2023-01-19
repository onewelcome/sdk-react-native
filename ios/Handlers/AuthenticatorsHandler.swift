protocol BridgeToAuthenticatorsHandlerProtocol: AnyObject {
    func registerAuthenticator(_ userProfile: ONGUserProfile,_ authenticatorId: String, _ completion: @escaping (Bool, Error?) -> Void)
    func deregisterAuthenticator(_ userProfile: ONGUserProfile, _ authenticatorId: String, _ completion: @escaping (Error?) -> Void)
    func setPreferredAuthenticator(_ userProfile: ONGUserProfile, _ authenticatorId: String, _ completion: @escaping (Bool, Error?) -> Void)
    func getAuthenticatorsListForUserProfile(_ userProfile: ONGUserProfile) -> Array<ONGAuthenticator>
    func isAuthenticatorRegistered(_ authenticatorType: ONGAuthenticatorType, _ userProfile: ONGUserProfile) -> Bool
}

class AuthenticatorsHandler: NSObject {
    private var registrationCompletion: ((Bool, Error?) -> Void)?
    private var deregistrationCompletion: ((Error?) -> Void)?
    private let pinAuthenticationEventEmitter = PinAuthenticationEventEmitter()
    private let createPinEventEmitter = CreatePinEventEmitter()


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
    func registerAuthenticator(_ userProfile: ONGUserProfile, _ authenticatorId: String, _ completion: @escaping (Bool, Error?) -> Void) {
        guard let authenticator = ONGUserClient.sharedInstance().allAuthenticators(forUser: userProfile).first(where: {$0.identifier == authenticatorId}) else {
            completion(false, WrapperError.authenticatorDoesNotExist)
            return;
        }
        // We don't have to check if the authenticator is already registered as the sdk will do that for us.
        registrationCompletion = completion;
        ONGUserClient.sharedInstance().register(authenticator, delegate: self);
    }

    func deregisterAuthenticator(_ userProfile: ONGUserProfile, _ authenticatorId: String,_ completion: @escaping (Error?) -> Void) {
        guard let authenticator = ONGUserClient.sharedInstance().allAuthenticators(forUser: userProfile).first(where: {$0.identifier == authenticatorId}) else {
            completion(WrapperError.authenticatorDoesNotExist)
            return;
        }

        if(authenticator.isRegistered != true) {
            completion(WrapperError.authenticatorNotRegistered)
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
        BridgeConnector.shared?.toLoginHandler.setAuthPinChallenge(challenge)
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
        deregistrationCompletion?(nil)
        deregistrationCompletion = nil
    }

    func userClient(_: ONGUserClient, didReceive challenge: ONGCustomAuthDeregistrationChallenge) {
        // will need this in the future
    }

    func userClient(_: ONGUserClient, didFailToDeregister authenticator: ONGAuthenticator, forUser _: ONGUserProfile, error: Error) {
        deregistrationCompletion?(error)
        deregistrationCompletion = nil
    }
}
