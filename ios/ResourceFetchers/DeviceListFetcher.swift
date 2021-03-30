protocol DeviceListFetcherProtocol: AnyObject {
    func fetchResources(_ completion: @escaping (Devices?, SdkError?) -> Void)
}

class DeviceListFetcher: DeviceListFetcherProtocol {
    let decoder = JSONDecoder()

    func fetchResources(_ completion: @escaping (Devices?, SdkError?) -> Void) {
        let request = ONGResourceRequest(path: "devices", method: "GET")
        ONGUserClient.sharedInstance().fetchResource(request) { response, error in
            if let error = error {
                let mappedError = ErrorMapper().mapError(error)
                completion(nil, mappedError)
            } else {
                if let data = response?.data,
                    let deviceList = try? self.decoder.decode(Devices.self, from: data) {
                    completion(deviceList, nil)
                }
            }
        }
    }
}

struct Devices: Codable {
    var devices: [Device]
}

struct Device: Codable {
    var id: String
    var name: String
    var application: String
    var platform: String
}
