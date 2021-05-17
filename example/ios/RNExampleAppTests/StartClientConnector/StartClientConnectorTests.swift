//
//  StartClientConnectorTestCase.swift
//  RNExampleAppTests
//
//  Created by Jan Lipmann on 17/05/2021.
//

import XCTest
@testable import onegini_react_native_sdk

class StartClientConnectorTests: XCTestCase {
  var connector: StartClientConnector!
  var mockONGClient = MockONGClient()
  
  override func setUp() {
    connector = DefaultStartClientConnector(client: mockONGClient)
  }

  func testStartWithSuccess() {
    mockONGClient.startResult = .success(true)
    
    var error: Error?
    var result: Bool?
    
    let exp = expectation(description: "Start with success expectation")
    
    connector.startClient { r in
      switch r {
      case let .success(bool): result = bool
      case let .failure(e): error = e
      }
      exp.fulfill()
    }
    
    waitForExpectations(timeout: 0.2, handler: nil)
    
    XCTAssertNil(error)
    XCTAssertNotNil(result)
    XCTAssertTrue(result!)
  }
  
  func testStartWithError() {
    mockONGClient.startResult = .failure(TestingError.blank)
    
    var error: Error?
    var result: Bool?
    
    let exp = expectation(description: "Start with success expectation")
    
    connector.startClient { r in
      switch r {
      case let .success(bool): result = bool
      case let .failure(e): error = e
      }
      exp.fulfill()
    }
    
    waitForExpectations(timeout: 0.2, handler: nil)
    
    XCTAssertNil(result)
    XCTAssertNotNil(error)
    XCTAssertEqual(error as? TestingError, TestingError.blank)
  }

}


enum TestingError: Error {
  case blank
}
