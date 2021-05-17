import Foundation
import React

extension Result {
    func convertTo(resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        switch self {
        case let .success(result): resolve(result)
        case let .failure(error): reject(error.codeString , error.localizedDescription, error)
        }
    }
}
