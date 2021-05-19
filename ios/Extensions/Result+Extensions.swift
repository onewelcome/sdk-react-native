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

extension Result where Success == Encodable {
    func convertTo(resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        switch self {
        case let .success(result):
            guard let encoded = result.dict else {
                reject(nil, ResultConvertError.encodingError.localizedDescription, ResultConvertError.encodingError)
                return
            }
            resolve(encoded)
        case let .failure(error): reject(error.codeString , error.localizedDescription, error)
        }
    }
}

extension Encodable {
    var dict : [String: Any]? {
        guard let data = try? JSONEncoder().encode(self) else { return nil }
        guard let json = try? JSONSerialization.jsonObject(with: data, options: []) as? [String:Any] else { return nil }
        return json
    }
}

enum ResultConvertError: LocalizedError {
    case encodingError
    
    var errorDescription: String? {
        switch self {
        case .encodingError: return "Error with result data encoding"
        }
    }
}

