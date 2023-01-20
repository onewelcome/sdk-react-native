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

        let completionHandler = { (_ response: ResourceResponse?,_ error: Error?) in
            if let error = error {
                completion(nil, error)
            } else {
                completion(String(data: response?.data ?? Data.init(), encoding: .utf8), nil)
            }
        }
        
        do {
            let request = try buildRequest(details)
            switch(type) {
            case .anonymous: deviceClient.sendRequest(request, completion: completionHandler)
            case .implicitUser: userClient.sendImplicitRequest(request, completion: completionHandler)
            case .user: userClient.sendAuthenticatedRequest(request, completion: completionHandler)
            }
        } catch {
            completion(nil, error)
            return
        }
    }
    
    private func buildRequest(_ details: NSDictionary) throws -> ResourceRequest {
        guard let path = details["path"] as? String else {
            throw WrapperError.parametersNotCorrect(description: "'path' can not be empty")
        }
        guard let methodName = details["method"] as? String,
              let method = HTTPMethod(rawValue: methodName.uppercased())
        else {
            throw WrapperError.parametersNotCorrect(
                description: "'method' must be either 'GET', 'POST', 'PUT', 'DELETE', 'PATCH' or 'HEAD'")
        }
        return ResourceRequestFactory.makeResourceRequest(
            path: path, method: method, parameters: details["parameters"] as? [String: Any], body: nil,
            headers: details["headers"] as? [String: String], parametersEncoding: .formURL)
    }
}
