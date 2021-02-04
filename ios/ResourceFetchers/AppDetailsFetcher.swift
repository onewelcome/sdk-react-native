protocol AppDetailsFetcherProtocol: AnyObject {
    func fetchResources(_ completion: @escaping (ApplicationDetails?, SdkError?) -> Void)
}

class AppDetailsFetcher: AppDetailsFetcherProtocol {
    let decoder = JSONDecoder()

    func fetchResources(_ completion: @escaping (ApplicationDetails?, SdkError?) -> Void) {
        authenticateDevice { success, error in
            if success {
                self.deviceResourcesRequest(completion: { applicationDetails, error in
                    if let applicationDetails = applicationDetails {
                        completion(applicationDetails, nil)
                    } else if let error = error {
                        completion(nil, error)
                    }
                })
            } else if let error = error {
                completion(nil, error)
            }
        }
    }

    fileprivate func authenticateDevice(completion: @escaping (Bool, SdkError?) -> Void) {
        ONGDeviceClient.sharedInstance().authenticateDevice(["application-details"]) { success, error in
            if let error = error {
                let mappedError = ErrorMapper().mapError(error)
                completion(success, mappedError)
            } else {
                completion(success, nil)
            }
        }
    }

    fileprivate func deviceResourcesRequest(completion: @escaping (ApplicationDetails?, SdkError?) -> Void) {
        let resourceRequest = ONGResourceRequest(path: "application-details", method: "GET")
        ONGDeviceClient.sharedInstance().fetchResource(resourceRequest) { response, error in
            if let error = error {
                let mappedError = ErrorMapper().mapError(error)
                completion(nil, mappedError)
            } else {
                if let data = response?.data {
                    if let appDetails = try? self.decoder.decode(ApplicationDetails.self, from: data) {
                        completion(appDetails, nil)
                    }
                }
            }
        }
    }
}

struct ApplicationDetails: Codable {
    private enum CodingKeys: String, CodingKey {
        case applicationIdentifier = "application_identifier"
        case applicationVersion = "application_version"
        case applicationPlatform = "application_platform"
    }

    let applicationIdentifier: String
    let applicationVersion: String
    let applicationPlatform: String
}
