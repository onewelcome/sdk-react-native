import AuthenticationServices

protocol BrowserHandlerProtocol {
    func handleUrl(url: URL)
}

protocol BrowserHandlerToRegisterHandlerProtocol: AnyObject {
    func handleRedirectURL(url: URL?)
}

@available(iOS 12.0, *)
class BrowserViewController: NSObject, BrowserHandlerProtocol {
    var webAuthSession: ASWebAuthenticationSession?

    let registerHandler: BrowserHandlerToRegisterHandlerProtocol

    init(registerHandlerProtocol: BrowserHandlerToRegisterHandlerProtocol) {
        self.registerHandler = registerHandlerProtocol
    }

    func handleUrl(url: URL) {
        let scheme = URL(string: ONGClient.sharedInstance().configModel.redirectURL)!.scheme
        webAuthSession = ASWebAuthenticationSession(url: url, callbackURLScheme: scheme, completionHandler: { callbackURL, error in
          guard error == nil, let successURL = callbackURL else {
            self.cancelButtonPressed()
            return;
          }

          self.handleSuccessUrl(url: successURL)
        })

        if #available(iOS 13.0, *) {
            webAuthSession?.prefersEphemeralWebBrowserSession = true
            webAuthSession?.presentationContextProvider = self;
        }

        DispatchQueue.main.async {
            self.webAuthSession?.start()
        }
    }

    private func handleSuccessUrl(url: URL) {
        registerHandler.handleRedirectURL(url: url)
    }

    private func cancelButtonPressed() {
        registerHandler.handleRedirectURL(url: nil)
    }

}

@available(iOS 12.0, *)
extension BrowserViewController: ASWebAuthenticationPresentationContextProviding {
    func presentationAnchor(for session: ASWebAuthenticationSession) -> ASPresentationAnchor {
        return UIApplication.shared.windows.first { $0.isKeyWindow }!
    }
}
