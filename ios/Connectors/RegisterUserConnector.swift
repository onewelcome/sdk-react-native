import Foundation

typealias RegisterUserConnectorCompletionResult = Result<UserProfile, Error>

protocol RegisterUserConnector {
    func registerUser(identityProviderId: String?, scopes:[String], completion: @escaping (RegisterUserConnectorCompletionResult) -> Void)
}

final class DefaultRegisterUserConnector:NSObject, RegisterUserConnector {
    private let userClient: ONGUserClientProtocol
    private let identityProviderConnector: IdentityProviderConnector
    
    private var completion: ((RegisterUserConnectorCompletionResult) -> Void)?
    
    init(userClient: ONGUserClientProtocol = ONGUserClient.sharedInstance(), identityProviderConnector: IdentityProviderConnector) {
        self.userClient = userClient
        self.identityProviderConnector = identityProviderConnector
    }
    
    func registerUser(identityProviderId: String?, scopes: [String], completion: @escaping (RegisterUserConnectorCompletionResult) -> Void) {
        self.completion = completion
        
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
        
    }
    
    func userClientDidStartRegistration(_ userClient: ONGUserClient) {
        
    }
    
    func userClient(_ userClient: ONGUserClient, didReceive challenge: ONGBrowserRegistrationChallenge) {
        
    }
    
    func userClient(_ userClient: ONGUserClient, didReceiveCustomRegistrationFinish challenge: ONGCustomRegistrationChallenge) {
        
    }
    
    func userClient(_ userClient: ONGUserClient, didReceiveCustomRegistrationInitChallenge challenge: ONGCustomRegistrationChallenge) {
        
    }
    
    func userClient(_ userClient: ONGUserClient, didFailToRegisterWith identityProvider: ONGIdentityProvider, error: Error) {
        completion?(.failure(error))
    }
    
    func userClient(_ userClient: ONGUserClient, didRegisterUser userProfile: ONGUserProfile, identityProvider: ONGIdentityProvider, info: ONGCustomInfo?) {
        completion?(.success(UserProfile(identifier: userProfile.profileId)))
    }
}
