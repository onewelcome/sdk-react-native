protocol BridgeToResourceHandlerProtocol: AnyObject {
    func authenticateDevice(_ scopes:[String], _ completion: @escaping (Bool, NSError?) -> Void)
    func authenticateImplicitly(_ profile: ONGUserProfile, scopes: [String], _ completion: @escaping (Bool, NSError?) -> Void)
    func resourceRequest(_ type: ResourceRequestType, _ details: NSDictionary, _ completion: @escaping (String?, NSError?) -> Void)
}

enum ResourceRequestType: String {
    case User = "User"
    case Implicit = "ImplicitUser"
    case Anonymous = "Anonymous"
}

class ResourceHandler: BridgeToResourceHandlerProtocol {
    func authenticateDevice(_ scopes:[String], _ completion: @escaping (Bool, NSError?) -> Void) {
        ONGDeviceClient.sharedInstance().authenticateDevice(scopes) { success, error in
            if let error = error {
                completion(success, error as NSError)
            } else {
                completion(success, nil)
            }
        }
    }

    func authenticateImplicitly(_ profile: ONGUserProfile, scopes:[String], _ completion: @escaping (Bool, NSError?) -> Void) {
        authenticateProfileImplicitly(profile, scopes: scopes) { success, error in
            if let error = error {
                completion(false, error as NSError)
            } else {
                completion(true, nil)
            }
        }
    }

    func resourceRequest(_ type: ResourceRequestType, _ details: NSDictionary, _ completion: @escaping (String?, NSError?) -> Void) {
        switch(type) {
        case .Anonymous: anonymousResourcesRequest(details, completion);
        case .Implicit: implicitResourcesRequest(details, completion);
        case .User: userResourcesRequest(details, completion);
        }
    }

    fileprivate func isProfileImplicitlyAuthenticated(_ profile: ONGUserProfile) -> Bool {
        let implicitlyAuthenticatedProfile = ONGUserClient.sharedInstance().implicitlyAuthenticatedUserProfile()
        return implicitlyAuthenticatedProfile != nil && implicitlyAuthenticatedProfile == profile
    }

    fileprivate func authenticateProfileImplicitly(_ profile: ONGUserProfile, scopes: [String], completion: @escaping (Bool, NSError?) -> Void) {
        ONGUserClient.sharedInstance().implicitlyAuthenticateUser(profile, scopes: scopes) { success, error in
            if !success {
                completion(success, error as NSError?)
            }
            completion(success, nil)
        }
    }

    fileprivate func userResourcesRequest(_ details: NSDictionary, _ completion: @escaping (String?, NSError?) -> Void) {
        let encoding = getEncodingByValue(details["encoding"] as! String);

        let request = ONGResourceRequest.init(path: details["path"] as! String, method: details["method"] as! String, parameters: details["parameters"] as? [String : Any], encoding: encoding, headers: details["headers"] as? [String : String]);

        ONGUserClient.sharedInstance().fetchResource(request) { response, error in
            if let error = error {
                completion(nil, error as NSError)
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
    
    fileprivate func anonymousResourcesRequest(_ details: NSDictionary, _ completion: @escaping (String?, NSError?) -> Void) {
        let encoding = getEncodingByValue(details["encoding"] as! String);

        let request = ONGResourceRequest.init(path: details["path"] as! String, method: details["method"] as! String, parameters: details["parameters"] as? [String : Any], encoding: encoding, headers: details["headers"] as? [String : String]);

        ONGDeviceClient.sharedInstance().fetchResource(request) { response, error in
            if let error = error {
                completion(nil, error as NSError)
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

    fileprivate func implicitResourcesRequest(_ details: NSDictionary, _ completion: @escaping (String?, NSError?) -> Void) {
        let encoding = getEncodingByValue(details["encoding"] as! String);

        let implicitRequest = ONGResourceRequest.init(path: details["path"] as! String, method: details["method"] as! String, parameters: details["parameters"] as? [String : Any], encoding: encoding, headers: details["headers"] as? [String : String]);

        ONGUserClient.sharedInstance().fetchImplicitResource(implicitRequest) { response, error in
            if let error = error {
                completion(nil, error as NSError)
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

    fileprivate func getEncodingByValue(_ value: String) -> ONGParametersEncoding {
        switch value {
        case "application/json":
            return ONGParametersEncoding.JSON;
        case "application/x-www-form-urlencoded":
            return ONGParametersEncoding.formURL;
        default:
            return ONGParametersEncoding.JSON;
        }
    }
}
