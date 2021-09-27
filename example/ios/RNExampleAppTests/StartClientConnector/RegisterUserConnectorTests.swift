import XCTest
@testable import onegini_react_native_sdk

class RegisterUserConnectorTests: XCTestCase {

    var connector: RegisterUserConnector!
    var mockUserClient = MockUserClient()
    var browserConnector = MockBrowserConnector()

  override func setUp() {
    connector = DefaultRegisterUserConnector(userClient: mockUserClient, browserConnector: browserConnector)
  }

  func testStartBrowserRegistrationWithSuccess() {
    let exp = expectation(description: "Start with success expectation")
    var responded: Bool = false
    mockUserClient.performAction = { action in
      switch action {
      case .browserChallengeAction(let browserAction):
        switch browserAction {
        case .senderAction(let senderAction):
          switch senderAction {
          case .didRespond:
            responded = true
          default: break
          }
        }
      }
      exp.fulfill()

      XCTAssertTrue(responded)
    }

    connector.registerUser(identityProviderId: nil, scopes: ["read"]) { event in

    } completion: { result in

    }

    waitForExpectations(timeout: 0.2, handler: nil)
  }
}
