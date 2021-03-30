protocol BridgeToResourceHandlerProtocol: AnyObject {
    func authenticateDevice(_ path: NSString, _ completion: @escaping (Bool, NSError?) -> Void)
    func authenticateImplicitly(_ profile: ONGUserProfile, _ completion: @escaping (Bool, NSError?) -> Void)
    func resourceRequest(_ isImplicit: Bool, _ details: NSDictionary, _ completion: @escaping ([String: Any]?, NSError?) -> Void)
}


class ResourceHandler: BridgeToResourceHandlerProtocol {
    func authenticateDevice(_ path: NSString, _ completion: @escaping (Bool, NSError?) -> Void) {
        ONGDeviceClient.sharedInstance().authenticateDevice([path as String]) { success, error in
            if let error = error {
                completion(success, error as NSError)
            } else {
                completion(success, nil)
            }
        }
    }

    func authenticateImplicitly(_ profile: ONGUserProfile, _ completion: @escaping (Bool, NSError?) -> Void) {
        if isProfileImplicitlyAuthenticated(profile) {
            completion(true, nil)
        } else {
            authenticateProfileImplicitly(profile) { success, error in
                if let error = error {
                    completion(false, error as NSError)
                } else {
                    completion(true, nil)
                }
            }
        }
    }

    func resourceRequest(_ isImplicit: Bool, _ details: NSDictionary, _ completion: @escaping ([String: Any]?, NSError?) -> Void) {
        if(isImplicit == true){
            implicitResourcesRequest(details, completion);
        } else{
            simpleResourcesRequest(details, completion);
        }
    }

    fileprivate func isProfileImplicitlyAuthenticated(_ profile: ONGUserProfile) -> Bool {
        let implicitlyAuthenticatedProfile = ONGUserClient.sharedInstance().implicitlyAuthenticatedUserProfile()
        return implicitlyAuthenticatedProfile != nil && implicitlyAuthenticatedProfile == profile
    }

    fileprivate func authenticateProfileImplicitly(_ profile: ONGUserProfile, completion: @escaping (Bool, NSError?) -> Void) {
        ONGUserClient.sharedInstance().implicitlyAuthenticateUser(profile, scopes: nil) { success, error in
            if !success {
                completion(success, error as NSError)
            }
            completion(success, nil)
        }
    }

    fileprivate func simpleResourcesRequest(_ details: NSDictionary, _ completion: @escaping ([String: Any]?, NSError?) -> Void) {
        let encoding = getEncodingByValue(details["encoding"] as! String);

        let request = ONGResourceRequest.init(path: details["path"] as! String, method: details["method"] as! String, parameters: details["parameters"] as? [String : Any], encoding: encoding, headers: details["headers"] as? [String : String]);

        ONGUserClient.sharedInstance().fetchResource(request) { response, error in
            if let error = error {
                completion(nil, error as NSError)
            } else {
                if let data = response?.data {
                    if let responseData = try? JSONSerialization.jsonObject(with: data, options: []) as? [String: Any] {
                        completion(responseData, nil)
                    } else {
                        let error = NSError(domain: ONGFetchResourceErrorDomain, code: 0, userInfo: [NSLocalizedDescriptionKey : "Failed to parse data."])
                        completion(nil, error)
                    }
                } else {
                    let error = NSError(domain: ONGFetchResourceErrorDomain, code: 0, userInfo: [NSLocalizedDescriptionKey : "Response doesn't contain data."])
                    completion(nil, error)
                }
            }
        }
    }

    fileprivate func implicitResourcesRequest(_ details: NSDictionary, _ completion: @escaping ([String: Any]?, NSError?) -> Void) {
        let encoding = getEncodingByValue(details["encoding"] as! String);

        let implicitRequest = ONGResourceRequest.init(path: details["path"] as! String, method: details["method"] as! String, parameters: details["parameters"] as? [String : Any], encoding: encoding, headers: details["headers"] as? [String : String]);

        ONGUserClient.sharedInstance().fetchImplicitResource(implicitRequest) { response, error in
            if let error = error {
                completion(nil, error as NSError)
            } else {
                if let data = response?.data {
                    if let responseData = try? JSONSerialization.jsonObject(with: data, options: []) as? [String: Any] {
                        completion(responseData, nil)
                    } else {
                        let error = NSError(domain: ONGFetchImplicitResourceErrorDomain, code: 0, userInfo: [NSLocalizedDescriptionKey : "Failed to parse data."])
                        completion(nil, error)
                    }
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
