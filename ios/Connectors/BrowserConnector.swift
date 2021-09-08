//
//  BrowserConnector.swift
//  onegini-react-native-sdk
//
//  Created by Jan Lipmann on 06/09/2021.
//

typealias BrowserConnectorCompletion = ((BrowserConnectorAction) -> Void)

enum BrowserConnectorAction {
    case cancel
    case handleUrl(url: URL)
    case handleError(Error)
}

protocol BrowserConnector {
    func handleURL(_ url: URL, completion: @escaping BrowserConnectorCompletion)
}

final class DefaultBrowserConnector: NSObject, BrowserConnector {
    private var webAuthSession: AuthenticationSessionProtocol?
    private var completion: BrowserConnectorCompletion?

    func handleURL(_ url: URL, completion: @escaping BrowserConnectorCompletion) {
        let scheme = URL(string: ONGClient.sharedInstance().configModel.redirectURL)!.scheme

        webAuthSession = AuthenticationSession(url: url, callbackURLScheme: scheme, completionHandler: { callbackURL, error in
            if let error = error {
                completion(.handleError(error))
                return
            }
            guard let successURL = callbackURL else {
                self.completion?(.cancel)
                return;
            }

            completion(.handleUrl(url: successURL))
        })

        DispatchQueue.main.async {
            self.webAuthSession?.start()
        }
    }
}
