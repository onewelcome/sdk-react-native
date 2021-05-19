typealias GetIdentityProviderResult = Result<ONGIdentityProvider?, Error>
typealias GetIdentityProvidersResult = Result<Set<ONGIdentityProvider>, Error>


protocol IdentityProviderConnector {
    func getIdentityProviders(_ completion: (GetIdentityProvidersResult) -> Void)
    func getIdentityProvider(with identifier: String, completion: (GetIdentityProviderResult) -> Void)
}

final class DefaultIdentityProviderConnector: IdentityProviderConnector {
    let userClient: ONGUserClientProtocol
    
    init(userClient: ONGUserClientProtocol = ONGClient.sharedInstance().userClient) {
        self.userClient = userClient
    }
    
    func getIdentityProviders(_ completion: (GetIdentityProvidersResult) -> Void) {
        let providers = userClient.identityProviders()
        completion(.success(providers))
    }
    
    func getIdentityProvider(with identifier: String, completion: (GetIdentityProviderResult) -> Void) {
        let provider = userClient.identityProviders().first(where: {$0.identifier == identifier})
        completion(.success(provider))
    }
}
