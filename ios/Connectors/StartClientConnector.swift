import Foundation
import OneginiSDKiOS

typealias StartClientConnectorResult = Result<Bool, Error>

protocol StartClientConnector {
    func startClient(_ completion: @escaping (StartClientConnectorResult) -> Void)
}

class DefaultStartClientConnector: StartClientConnector {
    private let client: ONGClientProtocol
    
    init(client: ONGClientProtocol = ONGClientBuilder().build()) {
        self.client = client
    }
    
    func startClient(_ completion: @escaping (StartClientConnectorResult) -> Void) {
        client.start { result, error in
            if let error = error {
                completion(.failure(error))
            } else {
                completion(.success(result))
            }
        }
    }
}
