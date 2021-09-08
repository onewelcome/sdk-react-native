//
//  AuthenticationSession.swift
//  onegini-react-native-sdk
//
//  Created by Jan Lipmann on 06/09/2021.
//

import AuthenticationServices
import SafariServices


protocol AuthenticationSessionProtocol {
    init(url URL: URL,
         callbackURLScheme: String?,
         completionHandler: @escaping (URL?, Error?) -> Void)
    func start() -> Bool
    func cancel()
}

class AuthenticationSession: NSObject, AuthenticationSessionProtocol {

    private var innerAuthenticationSession: AuthenticationSessionProtocol?

    required init(url URL: URL,
         callbackURLScheme: String?,
         completionHandler: @escaping (URL?, Error?) -> Void) {

        var asWebSession: AuthenticationSessionProtocol {
            if #available(iOS 12, *) {
                let asWebSession = ASWebAuthenticationSession(url: URL, callbackURLScheme: callbackURLScheme, completionHandler: completionHandler)
                if #available(iOS 13.0, *) {
                    asWebSession.prefersEphemeralWebBrowserSession = true
                    asWebSession.presentationContextProvider = self;
                }
                return asWebSession

            } else {
                return SFAuthenticationSession(url: URL, callbackURLScheme: callbackURLScheme, completionHandler: completionHandler)
            }
        }

        super.init()
        self.innerAuthenticationSession = asWebSession
    }

    func start() -> Bool {
        guard let innerAuthenticationSession = innerAuthenticationSession else { return false }

        return innerAuthenticationSession.start()
    }

    func cancel() {
        innerAuthenticationSession?.cancel()
    }
}

@available(iOS 12.0, *)
extension AuthenticationSession: ASWebAuthenticationPresentationContextProviding {
    func presentationAnchor(for session: ASWebAuthenticationSession) -> ASPresentationAnchor {
        UIApplication.shared.windows.first { $0.isKeyWindow }!
    }
}

extension SFAuthenticationSession: AuthenticationSessionProtocol {
}

@available(iOS 12.0, *)
extension ASWebAuthenticationSession: AuthenticationSessionProtocol {
}
