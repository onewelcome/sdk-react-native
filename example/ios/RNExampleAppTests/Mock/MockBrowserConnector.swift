import Foundation

@testable import onegini_react_native_sdk

class MockBrowserConnector: BrowserConnector {
  func handleURL(_ url: URL, completion: @escaping BrowserConnectorCompletion) {
    completion(.handleUrl(url: URL(string: "https://google.com")!))
  }
}
