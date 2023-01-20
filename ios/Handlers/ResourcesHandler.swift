protocol BridgeToResourceHandlerProtocol: AnyObject {
    func resourceRequest(_ type: ResourceRequestType, _ details: NSDictionary, _ completion: @escaping (String?, Error?) -> Void)
}

enum ResourceRequestType: String {
    case user
    case implicitUser
    case anonymous
    
    init(rawValue: String) {
        switch rawValue.lowercased() {
        case "user": self = .user
        case "implicitUser": self = .implicitUser
        case "anonymous": self = .anonymous
        default: self = .anonymous
        }
    }
}

class ResourceHandler: BridgeToResourceHandlerProtocol {
    func resourceRequest(_ type: ResourceRequestType, _ details: NSDictionary, _ completion: @escaping (String?, Error?) -> Void) {
        guard let path = details["path"] as? String else {
            let error = NSError(domain: ONGFetchImplicitResourceErrorDomain, code: 0, userInfo: [NSLocalizedDescriptionKey : "'path' can not be empty"])
            completion(nil, error)
            return
        }
        guard let method = details["method"] as? String else {
            let error = NSError(domain: ONGFetchImplicitResourceErrorDomain, code: 0, userInfo: [NSLocalizedDescriptionKey : "'method' can not be empty"])
            completion(nil, error)
            return
        }
        let request = ONGResourceRequest(path: path, method: method, parameters: details["parameters"] as? [String : Any], encoding: ONGParametersEncoding.formURL, headers: details["headers"] as? [String : String]);
        switch(type) {
        case .anonymous: anonymousResourcesRequest(request, completion);
        case .implicitUser: implicitResourcesRequest(request, completion);
        case .user: userResourcesRequest(request, completion);
        }
    }



    fileprivate func userResourcesRequest(_ request: ONGResourceRequest, _ completion: @escaping (String?, Error?) -> Void) {
        ONGUserClient.sharedInstance().fetchResource(request) { response, error in
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
    }
    
    fileprivate func anonymousResourcesRequest(_ request: ONGResourceRequest, _ completion: @escaping (String?, Error?) -> Void) {
        ONGDeviceClient.sharedInstance().fetchResource(request) { response, error in
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
    }

    fileprivate func implicitResourcesRequest(_ request: ONGResourceRequest, _ completion: @escaping (String?, Error?) -> Void) {
        ONGUserClient.sharedInstance().fetchImplicitResource(request) { response, error in
            if let error = error {
                completion(nil, error)
            } else {
                if let data = response?.data {
                    completion(String(data: data, encoding: .utf8), nil)
                } else {
                    let error = NSError(domain: ONGFetchImplicitResourceErrorDomain, code: 0, userInfo: [NSLocalizedDescriptionKey : "Response doesn't contain data."])
                    completion(nil, error)
                }
            }
        }
    }
}
