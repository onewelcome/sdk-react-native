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
        let scheme = "reactnativeexample";

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
        } else {
          // Fallback on earlier versions
        };

        webAuthSession?.start()
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
        var anchor: ASPresentationAnchor?;
        let group = DispatchGroup()
        group.enter()

        DispatchQueue.global(qos: .default).async {
            anchor = UIApplication.shared.keyWindow!
            group.leave()
        }

        // wait ...
        group.wait()

        return anchor!
    }
}
