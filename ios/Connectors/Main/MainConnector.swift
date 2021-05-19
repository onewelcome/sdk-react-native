import Foundation

protocol MainConnector {
    func startClient(_ resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock)
}

class DefaultMainConnector: MainConnector {
    let factory: MainConnectorFactory
    
    init(factory: MainConnectorFactory = DefaultMainConnectorFactory()) {
        self.factory = factory
    }
    
    func startClient(_ resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        factory.startClientConnector.startClient{ $0.convertTo(resolve: resolve, reject: reject) }
    }
    
    func registerUser(identityProviderId:String, scopes: [String], _ resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        factory.registerUserConnector.registerUser(identityProviderId: identityProviderId, scopes: scopes, progress: { action in
            // progress actions here
        }) { $0.convertTo(resolve: resolve, reject: reject) }
    }
}
