import OneginiSDKiOS

protocol ONGUserClientProtocol {
    func identityProviders() -> Set<ONGIdentityProvider>
    func registerUser(with: ONGIdentityProvider?, scopes: [String]?, delegate: ONGRegistrationDelegate)
}

extension ONGUserClient: ONGUserClientProtocol {}
