import Foundation

typealias RegisterUserConnectorCompletionResult = Result<UserProfile, Error>

protocol RegisterUserConnector {
    func registerUser(identityProviderId: String?, scopes:[String], statusChanged: ((Event) -> Void)?, completion: @escaping (RegisterUserConnectorCompletionResult) -> Void)
    
    func sendPin(_ pin: String)
    func cancelPinChallenge()
}

final class DefaultRegisterUserConnector:NSObject, RegisterUserConnector {
    private let userClient: ONGUserClientProtocol
    private let identityProviderConnector: IdentityProviderConnector
    
    private var completion: ((RegisterUserConnectorCompletionResult) -> Void)?
    private var statusChanged:  ((Event) -> Void)?
    
    private var pinChallenge: ONGCreatePinChallenge?
    private var browserChallenge: ONGBrowserRegistrationChallenge?
    private var customChallenge: ONGCustomRegistrationChallenge?

    private var browserConnector: BrowserConnector?
    private var pinConnector: PinConnector
    
    init(userClient: ONGUserClientProtocol = ONGUserClient.sharedInstance(),
         identityProviderConnector: IdentityProviderConnector = DefaultIdentityProviderConnector(),
         browserConnector: BrowserConnector = DefaultBrowserConnector(),
         pinConnector: PinConnector = DefaultPinConnector()) {
        self.userClient = userClient
        self.identityProviderConnector = identityProviderConnector
        self.browserConnector = browserConnector
        self.pinConnector = pinConnector

        super.init()

        bind()
    }

    private func bind() {
        self.pinConnector.sendEventHandler = {[weak self] event in
            self?.statusChanged?(event)
        }
    }
    
    func registerUser(identityProviderId: String?, scopes: [String], statusChanged: ((Event) -> Void)?, completion: @escaping (RegisterUserConnectorCompletionResult) -> Void) {
        self.completion = completion
        self.statusChanged = statusChanged
        
        guard let identityProviderId = identityProviderId else {
            userClient.registerUser(with: nil, scopes: scopes, delegate: self)
            return
        }
        
        identityProviderConnector.getIdentityProvider(with: identityProviderId) { [weak self] result in
            guard let self = self else { return }
            
            switch result {
            case let .success(provider): userClient.registerUser(with: provider, scopes: scopes, delegate: self)
            case let .failure(error): self.completion?(.failure(error))
            }
        }
    }
}

// MARK: - Browser

extension DefaultRegisterUserConnector {
    func handleRedirectURL(url: URL?) {
        guard let browserChallenge = self.browserChallenge else { return }

        guard let url = url else {
            browserChallenge.sender.cancel(browserChallenge)
            return
        }

        browserChallenge.sender.respond(with: url, challenge: browserChallenge)
    }

    func presentBrowserUserRegistrationView(registrationUserURL: URL) {
        browserConnector?.handleURL(registrationUserURL, completion: { [weak self] action in
            switch action {
            case .cancel:
                guard let browserChallenge = self?.browserChallenge else { return }

                browserChallenge.sender.cancel(browserChallenge)
            case .handleUrl(url: let url):
                guard let browserChallenge = self?.browserChallenge else { return }

                browserChallenge.sender.respond(with: url, challenge: browserChallenge)
            case .handleError(let error):
                debugPrint("[BrowserConnector handleURL] error: ", error.localizedDescription)
            }
        })
    }
}

extension DefaultRegisterUserConnector: ONGRegistrationDelegate {
    func userClient(_ userClient: ONGUserClient, didReceivePinRegistrationChallenge challenge: ONGCreatePinChallenge) {
        pinChallenge = challenge

        let pinError = mapErrorFromPinChallenge(challenge)
        pinConnector.handleFlowUpdate(PinFlow.create, pinError, receiver: self)
    }
    
    func userClient(_ userClient: ONGUserClient, didReceive challenge: ONGBrowserRegistrationChallenge) {
        self.browserChallenge = challenge

        presentBrowserUserRegistrationView(registrationUserURL: challenge.url)
    }
    
    func userClient(_ userClient: ONGUserClient, didReceiveCustomRegistrationFinish challenge: ONGCustomRegistrationChallenge) {
        customChallenge = challenge

        var data: [String: Any] = ["identityProviderId" : challenge.identityProvider.identifier,
                                   "action": CustomRegistrationNotification.finishRegistration.rawValue]
        if let info = challenge.info {
            let customInfo: [String: Any] = ["data":  info.data, "status": info.status]
            data["customInfo"] = customInfo
        }
        statusChanged?(RegisterEvent.customChallengeFinishReceived(data: data, error: challenge.error))
    }
    
    func userClient(_ userClient: ONGUserClient, didReceiveCustomRegistrationInitChallenge challenge: ONGCustomRegistrationChallenge) {
        customChallenge = challenge

        let data = ["identityProviderId" : challenge.identityProvider.identifier,
                    "action": CustomRegistrationNotification.initRegistration.rawValue]
        statusChanged?(RegisterEvent.customChallengeInitReceived(data: data, error: challenge.error))
    }
    
    func userClient(_ userClient: ONGUserClient, didFailToRegisterWith identityProvider: ONGIdentityProvider, error: Error) {
        completion?(.failure(error))
    }
    
    func userClient(_ userClient: ONGUserClient, didRegisterUser userProfile: ONGUserProfile, identityProvider: ONGIdentityProvider, info: ONGCustomInfo?) {
        completion?(.success(UserProfile(identifier: userProfile.profileId)))
    }
}

// MARK: - Pin
extension DefaultRegisterUserConnector: PinHandlerToReceiverProtocol {
    func handlePin(pin: String?) {
        guard let challenge = pinChallenge, let pin = pin else { return }

        challenge.sender.respond(withCreatedPin: pin, challenge: challenge)
    }

    func sendPin(_ pin: String) {
        guard let challenge = pinChallenge else { return }
        
        challenge.sender.respond(withCreatedPin: pin, challenge: challenge)
    }
    
    func cancelPinChallenge() {
        guard let challenge = pinChallenge else { return }
        
        challenge.sender.cancel(challenge)
    }

    fileprivate func mapErrorFromPinChallenge(_ challenge: ONGCreatePinChallenge) -> NSError? {
        if let error = challenge.error {
            return error as NSError
        } else {
            return nil
        }
    }
}


// MARK: - Events

private extension DefaultRegisterUserConnector {
    private func sendCustomRegistrationNotification(_ event: CustomRegistrationNotification,_ data: [String: Any]?)  {
        
    }
}


