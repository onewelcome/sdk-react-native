protocol BridgeToResourceHandlerProtocol: AnyObject {
    func resourceRequest(_ type: ResourceRequestType, _ details: NSDictionary, _ completion: @escaping (String?, Error?) -> Void)
}

enum ResourceRequestType: String {
    case user
    case implicitUser
    case anonymous
    
    init(rawValue: String) {
        switch rawValue {
        case "User": self = .user
        case "ImplicitUser": self = .implicitUser
        case "Anonymous": self = .anonymous
        default: self = .anonymous
        }
    }
}

class ResourceHandler: BridgeToResourceHandlerProtocol {
    private var deviceClient: DeviceClient {
        return SharedDeviceClient.instance
    }
    
    private var userClient: UserClient {
        return SharedUserClient.instance
    }
    
    func resourceRequest(_ type: ResourceRequestType, _ details: NSDictionary, _ completion: @escaping (String?, Error?) -> Void) {
        guard let path = details["path"] as? String else {
            let error = NSError(domain: ONGFetchImplicitResourceErrorDomain, code: 0, userInfo: [NSLocalizedDescriptionKey : "'path' can not be empty"])
            completion(nil, error)
            return
        }
        guard let methodName = details["method"] as? String, let method = HTTPMethod(rawValue: methodName.uppercased()) else {
            let error = NSError(domain: ONGFetchImplicitResourceErrorDomain, code: 0, userInfo: [NSLocalizedDescriptionKey : "'method' can not be empty"])
            completion(nil, error)
            return
        }

        let request = ResourceRequestFactory.makeResourceRequest(path: path, method: method, parameters: details["parameters"] as? [String: Any], body: nil, headers: details["headers"] as? [String: String], parametersEncoding: .formURL)
        
        let completionHandler = { (_ response: ResourceResponse?,_ error: Error?) in
            if let error = error {
                completion(nil, error)
            } else {
                if let data = response?.data {
                    completion(String(data: data, encoding: .utf8), nil)
                } else {
                    let error = NSError(domain: ONGFetchResourceErrorDomain, code: 0, userInfo: [NSLocalizedDescriptionKey : "Response doesn't contain data."])
                    completion(nil, error)
                }
            }
        }
        
        switch(type) {
        // It is unclear if deviceClient.sendRequest is the correct method to call, but the deviceClient.sendUnauthenticatedRequest method does NOT seem to work, so let's use this one instead which appears to work.
        case .anonymous: deviceClient.sendRequest(request, completion: completionHandler)
        case .implicitUser: userClient.sendImplicitRequest(request, completion: completionHandler)
        case .user: userClient.sendAuthenticatedRequest(request, completion: completionHandler)
        }
    }
}
