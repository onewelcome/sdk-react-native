import Foundation

typealias RegisterUserConnectorCompletionResult = Result<UserProfile, Error>

protocol RegisterUserConnector {
    func registerUser(identityProviderId: String?, scopes:[String], statusChanged: ((RegisterEvent) -> Void)?, completion: @escaping (RegisterUserConnectorCompletionResult) -> Void)
    
    func sendPin(_ pin: String)
    func cancelPinChallenge()
}

final class DefaultRegisterUserConnector:NSObject, RegisterUserConnector {
    private let userClient: ONGUserClientProtocol
    private let identityProviderConnector: IdentityProviderConnector
    
    private var completion: ((RegisterUserConnectorCompletionResult) -> Void)?
    private var statusChanged:  ((RegisterEvent) -> Void)?
    
    private var pinChallenge: ONGCreatePinChallenge?
    private var browserChallenge: ONGBrowserRegistrationChallenge?
    private var customChallenge: ONGCustomRegistrationChallenge?
    
    init(userClient: ONGUserClientProtocol = ONGUserClient.sharedInstance(),
         identityProviderConnector: IdentityProviderConnector = DefaultIdentityProviderConnector()) {
        self.userClient = userClient
        self.identityProviderConnector = identityProviderConnector
    }
    
    func registerUser(identityProviderId: String?, scopes: [String], statusChanged: ((RegisterEvent) -> Void)?, completion: @escaping (RegisterUserConnectorCompletionResult) -> Void) {
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

extension DefaultRegisterUserConnector: ONGRegistrationDelegate {
    func userClient(_ userClient: ONGUserClient, didReceivePinRegistrationChallenge challenge: ONGCreatePinChallenge) {
        pinChallenge = challenge
        statusChanged?(.pinChallengeReceived(pinLength: Int(challenge.pinLength), error: challenge.error))
    }
    
    func userClientDidStartRegistration(_ userClient: ONGUserClient) {
        statusChanged?(.registrationStarted)
    }
    
    func userClient(_ userClient: ONGUserClient, didReceive challenge: ONGBrowserRegistrationChallenge) {
        self.browserChallenge = challenge
        statusChanged?(.browserChallengeReceived(url: challenge.url, error: challenge.error))
    }
    
    func userClient(_ userClient: ONGUserClient, didReceiveCustomRegistrationFinish challenge: ONGCustomRegistrationChallenge) {
        customChallenge = challenge
        guard let info = challenge.info else {
            statusChanged?(.customChallengeFinishReceived(status: nil, data: nil, error: challenge.error))
            return
        }
        
        statusChanged?(.customChallengeFinishReceived(status: Int(info.status), data: info.data, error: challenge.error))
    }
    
    func userClient(_ userClient: ONGUserClient, didReceiveCustomRegistrationInitChallenge challenge: ONGCustomRegistrationChallenge) {
        customChallenge = challenge
        guard let info = challenge.info else {
            statusChanged?(.customChallengeInitReceived(status: nil, data: nil, error: challenge.error))
            return
        }
        
        statusChanged?(.customChallengeInitReceived(status: Int(info.status), data: info.data, error: challenge.error))
    }
    
    func userClient(_ userClient: ONGUserClient, didFailToRegisterWith identityProvider: ONGIdentityProvider, error: Error) {
        completion?(.failure(error))
    }
    
    func userClient(_ userClient: ONGUserClient, didRegisterUser userProfile: ONGUserProfile, identityProvider: ONGIdentityProvider, info: ONGCustomInfo?) {
        completion?(.success(UserProfile(identifier: userProfile.profileId)))
    }
}

// MARK: - Pin
extension DefaultRegisterUserConnector {
    func sendPin(_ pin: String) {
        guard let challenge = pinChallenge else { return }
        
        challenge.sender.respond(withCreatedPin: pin, challenge: challenge)
    }
    
    func cancelPinChallenge() {
        guard let challenge = pinChallenge else { return }
        
        challenge.sender.cancel(challenge)
    }
}
