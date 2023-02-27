import OneginiSDKiOS
protocol BridgeToAuthenticatorsHandlerProtocol: AnyObject {
    func registerAuthenticator(_ userProfile: UserProfile, _ authenticatorId: String, completion: @escaping (Error?) -> Void)
    func deregisterAuthenticator(_ userProfile: UserProfile, _ authenticatorId: String, completion: @escaping (Error?) -> Void)
    func setPreferredAuthenticator(_ userProfile: UserProfile, _ authenticatorId: String, completion: @escaping (Error?) -> Void)
    func getAuthenticatorsListForUserProfile(_ userProfile: UserProfile) -> [Authenticator]
    func isAuthenticatorRegistered(_ authenticatorType: AuthenticatorType, _ userProfile: UserProfile) -> Bool
}

class AuthenticatorsHandler: NSObject {
    private var registrationCompletion: ((Error?) -> Void)?
    private var deregistrationCompletion: ((Error?) -> Void)?
    private let pinAuthenticationEventEmitter = PinAuthenticationEventEmitter()
    private let createPinEventEmitter = CreatePinEventEmitter()

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
                               completion: @escaping (Error?) -> Void) {
        guard let authenticator = SharedUserClient.instance.authenticators(.all, for: userProfile).first(where: {$0.identifier == authenticatorId}) else {
            completion(WrapperError.authenticatorDoesNotExist)
            return
        }
        // We don't have to check if the authenticator is already registered as the sdk will do that for us.
        registrationCompletion = completion
        SharedUserClient.instance.register(authenticator: authenticator, delegate: self)
    }

    func deregisterAuthenticator(_ userProfile: UserProfile,
                                 _ authenticatorId: String,
                                 completion: @escaping (Error?) -> Void) {
        guard let authenticator = SharedUserClient.instance.authenticators(.all, for: userProfile).first(where: {$0.identifier == authenticatorId}) else {
            completion(WrapperError.authenticatorDoesNotExist)
            return
        }

        if authenticator.isRegistered != true {
            completion(WrapperError.authenticatorNotRegistered)
            return
        }

        deregistrationCompletion = completion
        SharedUserClient.instance.deregister(authenticator: authenticator, delegate: self)
    }

    func setPreferredAuthenticator(_ userProfile: UserProfile, _ authenticatorId: String, completion: @escaping (Error?) -> Void) {
        guard let authenticator = SharedUserClient.instance.authenticators(.all, for: userProfile).first(where: {$0.identifier == authenticatorId}) else {
            let error = NSError(domain: ONGAuthenticatorRegistrationErrorDomain,
                                code: ONGAuthenticatorRegistrationError.authenticatorNotSupported.rawValue,
                                userInfo: [NSLocalizedDescriptionKey: "This authenticator is not available."])
            completion(error)
            return
        }

        if authenticator.isRegistered != true {
            let error = NSError(domain: ONGAuthenticatorRegistrationErrorDomain,
                                code: ONGAuthenticatorRegistrationError.authenticatorNotSupported.rawValue,
                                userInfo: [NSLocalizedDescriptionKey: "This authenticator is not registered."])
            completion(error)
            return
        }
        SharedUserClient.instance.setPreferredAuthenticator(authenticator)
        completion(nil)
    }

    func getAuthenticatorsListForUserProfile(_ userProfile: UserProfile) -> [Authenticator] {
        return SharedUserClient.instance.authenticators(.all, for: userProfile)
    }

    func isAuthenticatorRegistered(_ authenticatorType: AuthenticatorType, _ userProfile: UserProfile) -> Bool {
        return SharedUserClient.instance.authenticators(.registered, for: userProfile).first(where: {$0.type.rawValue == authenticatorType.rawValue }) != nil
    }
}

extension AuthenticatorsHandler: AuthenticatorRegistrationDelegate {

    func userClient(_ userClient: UserClient, didReceivePinChallenge challenge: PinChallenge) {
        BridgeConnector.shared?.toLoginHandler.setAuthPinChallenge(challenge)
        if let pinError = mapErrorFromPinChallenge(challenge) {
            pinAuthenticationEventEmitter.onIncorrectPin(error: pinError, remainingFailureCount: challenge.remainingFailureCount)
        } else {
            pinAuthenticationEventEmitter.onPinOpen(profileId: challenge.userProfile.profileId)
        }
    }

    func userClient(_ userClient: UserClient, didReceiveCustomAuthFinishRegistrationChallenge challenge: CustomAuthFinishRegistrationChallenge) {
        // Will need this in the future
    }

    func userClient(_ userClient: UserClient, didFailToRegister authenticator: Authenticator, for userProfile: UserProfile, error: Error) {
        createPinEventEmitter.onPinClose()
        registrationCompletion?(error)
        registrationCompletion = nil
    }

    func userClient(_ userClient: UserClient, didRegister authenticator: Authenticator, for userProfile: UserProfile, info customAuthInfo: CustomInfo?) {
        createPinEventEmitter.onPinClose()
        registrationCompletion?(nil)
        registrationCompletion = nil
    }
}

extension AuthenticatorsHandler: AuthenticatorDeregistrationDelegate {

    func userClient(_ userClient: UserClient, didDeregister authenticator: Authenticator, forUser userProfile: UserProfile) {
        deregistrationCompletion?(nil)
        deregistrationCompletion = nil
    }

    func userClient(_ userClient: UserClient, didFailToDeregister authenticator: Authenticator, forUser userProfile: UserProfile, error: Error) {
        deregistrationCompletion?(error)
        deregistrationCompletion = nil
    }

    func userClient(_ userClient: UserClient, didReceiveCustomAuthDeregistrationChallenge challenge: CustomAuthDeregistrationChallenge) {
        // will need this in the future
    }
}
