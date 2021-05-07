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
        startClientConnector.startClient { result in
            switch result {
            case let .success(data):
                resolve(data)
            case let .failure(error):
                reject(error.codeString, error.localizedDescription, error)
            }
        }
    }
}
