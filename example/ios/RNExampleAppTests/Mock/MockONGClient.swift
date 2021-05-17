//
//  MockONGClient.swift
//  RNExampleAppTests
//
//  Created by Jan Lipmann on 17/05/2021.
//

import Foundation
@testable import onegini_react_native_sdk

class MockONGClient: ONGClientProtocol {
  var startResult: Result<Bool, Error>?
  var resetResult: Result<Bool, Error>?
  
  func start(_ completion: @escaping (Bool, Error?) -> Void) {
    guard let result = startResult else { return }
    
    switch result {
    case let .success(bool):
      completion(bool, nil)
    case let .failure(error):
      completion(false, error)
    }
  }
  
  func reset(_ completion: @escaping (Bool, Error?) -> Void) {
    guard let result = resetResult else { return }
    
    switch result {
    case let .success(bool):
      completion(bool, nil)
    case let .failure(error):
      completion(false, error)
    }
  }
}
