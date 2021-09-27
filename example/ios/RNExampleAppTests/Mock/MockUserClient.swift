import Foundation
@testable import onegini_react_native_sdk

final class MockUserClient: ONGUserClient {
  private weak var delegate: ONGRegistrationDelegate?

  private let browserChallenge = BrowserChallenge()

  enum Action {
    case browserChallengeAction(BrowserChallenge.Action)
  }

  var performAction: ((Action) -> Void)?

  override func identityProviders() -> Set<ONGIdentityProvider> {
    Set<ONGIdentityProvider>()
  }

  override func registerUser(with: ONGIdentityProvider?, scopes: [String]?, delegate: ONGRegistrationDelegate) {
    self.delegate = delegate

    mimicDidReceiveBrowserChallenge()
  }

  private func mimicDidReceiveBrowserChallenge() {
    browserChallenge.performAction = { self.performAction?(.browserChallengeAction($0)) }

    self.delegate?.userClient?(self, didReceive: browserChallenge)
  }
}

class BrowserChallenge: ONGBrowserRegistrationChallenge {
  enum Action {
    case senderAction(BrowserChallengeSender.Action)
  }

  var performAction: ((Action) -> Void)?

  override var sender: ONGBrowserRegistrationChallengeSender {
    let sender = BrowserChallengeSender()
    sender.performAction = { self.performAction?(.senderAction($0)) }
    return sender
  }

  override var url: URL {
    URL(string: "http://google.com")!
  }
}

class BrowserChallengeSender: NSObject, ONGBrowserRegistrationChallengeSender {
  enum Action {
    case didRespond
    case didCancel
  }

  var performAction: ((Action) -> Void)?


  func respond(with url: URL, challenge: ONGBrowserRegistrationChallenge) {
    performAction?(.didRespond)
  }

  func cancel(_ challenge: ONGBrowserRegistrationChallenge) {
    performAction?(.didCancel)
  }
}
