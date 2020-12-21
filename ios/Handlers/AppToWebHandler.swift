import Foundation
import OneginiSDKiOS
import OneginiCrypto

protocol AppToWebHandlerProtocol: AnyObject {
    func signInAppToWeb(targetURL: URL?, completion: @escaping ([String: Any?]?, SdkError?) -> Void)
}

class AppToWebHandler: AppToWebHandlerProtocol {
    
    func signInAppToWeb(targetURL: URL?, completion: @escaping ([String: Any?]?, SdkError?) -> Void) {
        guard let _targetURL = targetURL else {
            completion(nil, SdkError.init(errorDescription: "Provided url is incorrect."))
            return
        }
        
        ONGUserClient.sharedInstance().appToWebSingleSignOn(withTargetUrl: _targetURL) { (url, token, error) in
            if let _url = url, let _token = token {
                completion(["token": _token, "url": _url], nil)
            } else if let error = error {
                // Handle error
                debugPrint(error)
                
                let _error = SdkError(errorDescription: error.localizedDescription)
                completion(nil, _error)
            }
        }
    }
}
