protocol ImplicitDataFetcherProtocol: AnyObject {
    func fetchResources(_ profile: ONGUserProfile, _ completion: @escaping (String?, SdkError?) -> Void)
}

class ImplicitDataFetcher: ImplicitDataFetcherProtocol {

    func fetchResources(_ profile: ONGUserProfile, _ completion: @escaping (String?, SdkError?) -> Void) {
        if isProfileImplicitlyAuthenticated(profile) {
            implicitResourcesRequest { userIdDecorated, error in
                if let userIdDecorated = userIdDecorated {
                    completion(userIdDecorated, nil)
                } else if let error = error {
                    completion(nil, error)
                }
            }
        } else {
            authenticateUserImplicitly(profile) { success, error in
                if success {
                    self.implicitResourcesRequest { userIdDecorated, error in
                        if let userIdDecorated = userIdDecorated {
                            completion(userIdDecorated, nil)
                        } else if let error = error {
                            completion(nil, error)
                        }
                    }
                } else {
                    if let error = error {
                        completion(nil, error)
                    }
                }
            }
        }
    }

    fileprivate func isProfileImplicitlyAuthenticated(_ profile: ONGUserProfile) -> Bool {
        let implicitlyAuthenticatedProfile = ONGUserClient.sharedInstance().implicitlyAuthenticatedUserProfile()
        return implicitlyAuthenticatedProfile != nil && implicitlyAuthenticatedProfile == profile
    }

    fileprivate func authenticateUserImplicitly(_ profile: ONGUserProfile, completion: @escaping (Bool, SdkError?) -> Void) {
        ONGUserClient.sharedInstance().implicitlyAuthenticateUser(profile, scopes: nil) { success, error in
            if !success {
                let mappedError = ErrorMapper().mapError(error)
                completion(success, mappedError)
            }
            completion(success, nil)
        }
    }

    fileprivate func implicitResourcesRequest(completion: @escaping (String?, SdkError?) -> Void) {
        let implicitRequest = ONGResourceRequest(path: "user-id-decorated", method: "GET")
        ONGUserClient.sharedInstance().fetchImplicitResource(implicitRequest) { response, error in
            if let error = error {
                let mappedError = ErrorMapper().mapError(error)
                completion(nil, mappedError)
            } else {
                if let data = response?.data {
                    if let responseData = try? JSONSerialization.jsonObject(with: data, options: []) as! [String: String],
                        let userIdDecorated = responseData["user-id-decorated"] {
                        completion(userIdDecorated, nil)
                    }
                }
            }
        }
    }
}
