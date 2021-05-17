//
//  Result+Extensions.swift
//  RNOneginiSdk
//
//  Created by Jan Lipmann on 17/05/2021.
//  Copyright © 2021 Facebook. All rights reserved.
//

import Foundation
import React

extension Result {
    func convertTo(resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        switch self {
        case let .success(s): resolve(s)
        case let .failure(e): reject(e.codeString , e.localizedDescription, e)
        }
    }
}
