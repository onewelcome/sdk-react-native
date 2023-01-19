protocol BridgeToResourceHandlerProtocol: AnyObject {
    func authenticateDevice(_ scopes:[String], _ completion: @escaping (Bool, Error?) -> Void)
    func authenticateImplicitly(_ profile: ONGUserProfile, scopes: [String], _ completion: @escaping (Bool, Error?) -> Void)
    func resourceRequest(_ type: ResourceRequestType, _ details: NSDictionary, _ completion: @escaping (String?, Error?) -> Void)
}

enum ResourceRequestType: String {
    case User
    case ImplicitUser
    case Anonymous
}

class ResourceHandler: BridgeToResourceHandlerProtocol {
    func authenticateDevice(_ scopes:[String], _ completion: @escaping (Bool, Error?) -> Void) {
        ONGDeviceClient.sharedInstance().authenticateDevice(scopes) { success, error in
            if let error = error {
                completion(success, error)
            } else {
                completion(success, nil)
            }
        }
    }

    func authenticateImplicitly(_ profile: ONGUserProfile, scopes:[String], _ completion: @escaping (Bool, Error?) -> Void) {
        ONGUserClient.sharedInstance().implicitlyAuthenticateUser(profile, scopes: scopes) { _, error in
            if let error = error {
                completion(false, error)
            } else {
                completion(true, nil)
            }
        }
    }

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
        case .Anonymous: anonymousResourcesRequest(request, completion);
        case .ImplicitUser: implicitResourcesRequest(request, completion);
        case .User: userResourcesRequest(request, completion);
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
