import Foundation
import OneginiSDKiOS

protocol AppToWebHandlerProtocol: AnyObject {
    func signInAppToWeb(targetURL: URL?, completion: @escaping (NSMutableDictionary?, Error?) -> Void)
}

class AppToWebHandler: AppToWebHandlerProtocol {

    func signInAppToWeb(targetURL: URL?, completion: @escaping (NSMutableDictionary?, Error?) -> Void) {
        guard let targetURL = targetURL else {
            let error = NSError(domain: ONGAppToWebSingleSignOnErrorDomain, code: 0, userInfo: [NSLocalizedDescriptionKey: "Provided url is incorrect."])
            completion(nil, error)
            return
        }

        ONGUserClient.sharedInstance().appToWebSingleSignOn(withTargetUrl: targetURL) { (url, token, error) in
            if let url = url, let token = token {
                completion(["token": token, "url": url.absoluteString ], nil)
            } else if let error = error {
                completion(nil, error)
            }
        }
    }
}
