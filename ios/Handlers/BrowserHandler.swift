import AuthenticationServices

protocol BrowserViewControllerProtocol {
    func handleUrl(url: URL)
}

protocol BrowserViewControllerEntityProtocol {
    var browserRegistrationChallenge: ONGBrowserRegistrationChallenge? { get }
    var registrationUserURL: URL? { get }
    var redirectURL: URL? { get set }
    var pin: String? { get set }
}

@available(iOS 12.0, *)
class BrowserViewController: NSObject, BrowserViewControllerProtocol {
    var webAuthSession: ASWebAuthenticationSession?

    var registerUserEntity: BrowserViewControllerEntityProtocol
    let registerUserViewToPresenterProtocol: RegisterUserViewToPresenterProtocol

    init(registerUserEntity: BrowserViewControllerEntityProtocol, registerUserViewToPresenterProtocol: RegisterUserViewToPresenterProtocol) {
        self.registerUserEntity = registerUserEntity
        self.registerUserViewToPresenterProtocol = registerUserViewToPresenterProtocol
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
        registerUserEntity.redirectURL = url
        registerUserViewToPresenterProtocol.handleRedirectURL()
    }
  
    private func cancelButtonPressed() {
        registerUserEntity.redirectURL = nil
        registerUserViewToPresenterProtocol.handleRedirectURL()
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
