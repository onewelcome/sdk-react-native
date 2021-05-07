import Foundation
import OneginiSDKiOS

typealias StartClientConnectorResult = Result<Bool, Error>

protocol StartClientConnector {
    func startClient(_ completion: @escaping (StartClientConnectorResult) -> Void)
}

class DefaultStartClientConnector: StartClientConnector {
    func startClient(_ completion: @escaping (StartClientConnectorResult) -> Void) {
        ONGClientBuilder().build()
        ONGClient.sharedInstance().start { result, error in
            if let error = error {
                completion(.failure(error))
            } else {
                completion(.success(result))
            }
        }
    }
}
