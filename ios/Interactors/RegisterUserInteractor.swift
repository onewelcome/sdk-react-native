protocol RegisterUserInteractorProtocol: AnyObject {
    func identityProviders() -> Array<ONGIdentityProvider>
    func startUserRegistration(identityProvider: ONGIdentityProvider?)
    func handleRedirectURL()
    func handleCreatedPin()
}

class RegisterUserInteractor: NSObject {
    weak var registerUserPresenter: RegisterUserInteractorToPresenterProtocol?
    var registerUserEntity = RegisterUserEntity()

    fileprivate func mapErrorFromChallenge(_ challenge: ONGCreatePinChallenge) {
        if let error = challenge.error {
            registerUserEntity.pinError = ErrorMapper().mapError(error)
        } else {
            registerUserEntity.pinError = nil
        }
    }
}

extension RegisterUserInteractor: RegisterUserInteractorProtocol {
    func identityProviders() -> Array<ONGIdentityProvider> {
        let identityProviders = ONGUserClient.sharedInstance().identityProviders()
        return Array(identityProviders)
    }

    func startUserRegistration(identityProvider: ONGIdentityProvider? = nil) {
        ONGUserClient.sharedInstance().registerUser(with: identityProvider, scopes: ["read"], delegate: self)
    }

    func handleRedirectURL() {
        guard let browserRegistrationChallenge = registerUserEntity.browserRegistrationChallenge else { return }
        if let url = registerUserEntity.redirectURL {
            browserRegistrationChallenge.sender.respond(with: url, challenge: browserRegistrationChallenge)
        } else {
            browserRegistrationChallenge.sender.cancel(browserRegistrationChallenge)
        }
    }

    func handleCreatedPin() {
        guard let createPinChallenge = registerUserEntity.createPinChallenge else { return }
        if let pin = registerUserEntity.pin {
            createPinChallenge.sender.respond(withCreatedPin: pin, challenge: createPinChallenge)
        } else {
            createPinChallenge.sender.cancel(createPinChallenge)
        }
    }
    
    fileprivate func mapErrorMessageFromStatus(_ status: Int) {
        if status == 2000 {
            registerUserEntity.errorMessage = nil
        } else if status == 4002 {
            registerUserEntity.errorMessage = "This code is not initialized on portal."
        } else {
            registerUserEntity.errorMessage = "Provided code is incorrect."
        }
    }
}

extension RegisterUserInteractor: ONGRegistrationDelegate {
    func userClient(_: ONGUserClient, didReceive challenge: ONGBrowserRegistrationChallenge) {
        registerUserEntity.browserRegistrationChallenge = challenge
        registerUserEntity.registrationUserURL = challenge.url
        registerUserPresenter?.presentBrowserUserRegistrationView(registerUserEntity: registerUserEntity)
    }

    func userClient(_: ONGUserClient, didReceivePinRegistrationChallenge challenge: ONGCreatePinChallenge) {
        registerUserEntity.createPinChallenge = challenge
        registerUserEntity.pinLength = Int(challenge.pinLength)
        mapErrorFromChallenge(challenge)
        registerUserPresenter?.presentCreatePinView(registerUserEntity: registerUserEntity)
    }

    func userClient(_: ONGUserClient, didRegisterUser userProfile: ONGUserProfile, info _: ONGCustomInfo?) {
      registerUserPresenter?.registerUserActionSuccess(authenticatedUserProfile: userProfile)
    }

    func userClient(_: ONGUserClient, didFailToRegisterWithError error: Error) {
        if error.code == ONGGenericError.actionCancelled.rawValue {
            registerUserPresenter?.registerUserActionCancelled()
        } else {
            let mappedError = ErrorMapper().mapError(error)
            registerUserPresenter?.registerUserActionFailed(mappedError)
        }
    }
}
