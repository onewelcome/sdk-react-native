import Foundation
import OneginiSDKiOS

protocol AppToWebHandlerProtocol: AnyObject {
    func signInAppToWeb(targetURL: URL?, completion: @escaping (NSMutableDictionary?, NSError?) -> Void)
}

class AppToWebHandler: AppToWebHandlerProtocol {

    func signInAppToWeb(targetURL: URL?, completion: @escaping (NSMutableDictionary?, NSError?) -> Void) {
        guard let _targetURL = targetURL else {
            let error = NSError(domain: ONGAppToWebSingleSignOnErrorDomain, code: 0, userInfo: [NSLocalizedDescriptionKey : "Provided url is incorrect."])
            completion(nil, error)
            return
        }

        ONGUserClient.sharedInstance().appToWebSingleSignOn(withTargetUrl: _targetURL) { (url, token, error) in
            if let _url = url, let _token = token {
                completion(["token": _token, "url": _url.absoluteString ], nil)
            } else if let _error = error {
                completion(nil, _error as NSError)
            }
        }
    }
}
