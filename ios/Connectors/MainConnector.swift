import Foundation

protocol MainConnector {
    func startClient(_ resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock)
}

class DefaultMainConnector: MainConnector {
    let startClientConnector: StartClientConnector
    
    init(startClientConnector: StartClientConnector = DefaultStartClientConnector()) {
        self.startClientConnector = startClientConnector
    }
    
    func startClient(_ resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        startClientConnector.startClient{ $0.convertTo(resolve: resolve, reject: reject) }
    }
}
