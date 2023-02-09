protocol BridgeToResourceHandlerProtocol: AnyObject {
    func resourceRequest(_ type: ResourceRequestType, _ details: Dictionary<String, Any?>, _ completion: @escaping (Result<WrapperResourceResponse, Error>) -> Void)
}

enum ResourceRequestType: String {
    case user
    case implicitUser
    case anonymous
    
    init?(rawValue: String) {
        switch rawValue {
        case "User": self = .user
        case "ImplicitUser": self = .implicitUser
        case "Anonymous": self = .anonymous
        default: return nil
        }
    }
}

struct WrapperResourceResponse {
    var body: String
    var headers: [AnyHashable: String]
    var ok: Bool
    var status: Int
    
    init(_ response: ResourceResponse) {
        body = String(data: response.data ?? Data(), encoding: .utf8) ?? ""
        headers = response.allHeaderFields.compactMapValues { $0 as? String }
        ok = response.statusCode <= 299 && response.statusCode >= 200
        status = response.statusCode
    }
    
    var asDictionary: [String: Any] {
        ["body": body, "headers": headers, "ok": ok, "status": status]
    }
}

class ResourceHandler: BridgeToResourceHandlerProtocol {
    
    private let deviceClient = SharedDeviceClient.instance
    private let userClient = SharedUserClient.instance
    
    func resourceRequest(_ type: ResourceRequestType, _ details: Dictionary<String, Any?>, _ completion: @escaping (Result<WrapperResourceResponse, Error>) -> Void) {

        let completionHandler = { (_ response: ResourceResponse?,_ error: Error?) in
            if let error = error {
                guard let response = response else {
                    completion(.failure(error))
                    return
                }
                completion(.success(WrapperResourceResponse(response)))
            } else {
                guard let response = response else {
                    completion(.failure(WrapperError.resourceCallError))
                    return
                }
                completion(.success(WrapperResourceResponse(response)))
            }
        }
        
        do {
            let request = try buildRequest(details)
            switch type {
            case .anonymous: sendRequest(request, completion: completionHandler)
            case .implicitUser: userClient.sendImplicitRequest(request, completion: completionHandler)
            case .user: userClient.sendAuthenticatedRequest(request, completion: completionHandler)
            }
        } catch {
            completion(.failure(error))
            return
        }
    }
    
    private func buildRequest(_ details: Dictionary<String, Any?>) throws -> ResourceRequest {
        guard let path = details["path"] as? String else {
            throw WrapperError.parametersNotCorrect(description: "'path' can not be empty")
        }
        guard let methodName = details["method"] as? String,
              let method = HTTPMethod(rawValue: methodName.uppercased())
        else {
            throw WrapperError.parametersNotCorrect(
                description: "'method' must be either 'GET', 'POST', 'PUT', 'DELETE', 'PATCH' or 'HEAD'")
        }
        
        let body = details["body"] as? String
        
        let headers = try getHeaders(details)
        return ResourceRequestFactory.makeResourceRequest(
            path: path,
            method: method,
            body: body?.data(using: .utf8),
            headers: headers
        )
    }
    
    private func getHeaders(_ details: Dictionary<String, Any?>) throws -> [String: String] {
        if details["headers"] == nil {
            return [:]
        }
        guard let headers = details["headers"] as? [String: Any] else {
            throw WrapperError.parametersNotCorrect(
                description: "'headers' must be an object with only Strings as keys")
        }
        return headers.compactMapValues { $0 as? String }
    }
    
    // We copy over the swift api from the iOS SDK due to a bug in the sdk, because not all classes it uses are public we need to copy those over aswell. All below code can be removed once that fix is in the iOS SDK.
    // FIXME: Remove when using native sdk swift api for Anonymous resource requests
    @discardableResult
    func sendRequest(_ resourceRequest: ResourceRequest, completion: @escaping ((_ response: ResourceResponse?, _ error: Error?) -> Void)) -> NetworkTask? {
        let ongNetworkTask = ONGDeviceClient.sharedInstance().fetchResource(resourceRequest.ongRequest) { response, error in
            completion(response != nil ? ResourceResponseImplementation(response!) : nil, error)
        }
        guard let ongNetworkTask = ongNetworkTask else { return nil }
        return NetworkTaskImplementation(ongNetworkTask)
    }
}

// FIXME: Remove when using native sdk swift api for Anonymous resource requests
private extension ResourceRequest {
    var ongRequest: ONGResourceRequest {
        let ongParametersEncoding: ONGParametersEncoding = parametersEncoding == .formURL ? .formURL : .JSON
        let multipartData = multipartData?.map({ data -> ONGMultipartData in
            let multipartData = ONGMultipartData()
            multipartData.data = data.data
            multipartData.fileName = data.fileName
            multipartData.name = data.name
            multipartData.mimeType = data.mimeType
            return multipartData
        })
        if let multipartData = multipartData, multipartData.count > 0 {
            return ONGResourceRequest(path: path,
                                      method: method.rawValue,
                                      parameters: parameters,
                                      multipartData: multipartData)
        } else {
            let requestBuilder = ONGRequestBuilder()
            if let body = body { requestBuilder.setBody(body) }
            requestBuilder.setPath(path)
            requestBuilder.setMethod(method.rawValue)
            if let headers = headers { requestBuilder.setHeaders(headers) }
            requestBuilder.setParametersEncoding(ongParametersEncoding)
            if let parameters = parameters { requestBuilder.setParameters(parameters) }
            return requestBuilder.build()
        }
    }
}
// FIXME: Remove when using native sdk swift api for Anonymous resource requests
private class ResourceResponseImplementation: ResourceResponse {
    var response: HTTPURLResponse
    var allHeaderFields: [AnyHashable : Any]
    var statusCode: Int
    var data: Data?
    
    init(_ resourceResponse: ONGResourceResponse) {
        self.response = resourceResponse.rawResponse
        self.allHeaderFields = resourceResponse.allHeaderFields
        self.statusCode = resourceResponse.statusCode
        self.data = resourceResponse.data
    }
    
    required init(response: HTTPURLResponse, data: Data?) {
        self.response = response
        self.allHeaderFields = response.allHeaderFields
        self.statusCode = response.statusCode
        self.data = data
    }
}

// FIXME: Remove when using native sdk swift api for Anonymous resource requests
private class NetworkTaskImplementation: NetworkTask {
    
    var identifier: String
    var state: NetworkTaskState
    var request: ResourceRequest
    var response: ResourceResponse?
    var error: Error?
    
    init(_ networkTask: ONGNetworkTask) {
        self.identifier = networkTask.identifier
        self.state = NetworkTaskState(rawValue: networkTask.state.rawValue)!
        self.request = ResourceRequestImplementation(networkTask.request)
        if let response = networkTask.response {
            self.response = ResourceResponseImplementation(response)
        } else {
            self.response = nil
        }
        self.error = networkTask.error
    }
}

// FIXME: Remove when using native sdk swift api for Anonymous resource requests
private class ResourceRequestImplementation: ResourceRequest {
    
    let path: String
    let method: HTTPMethod
    var headers: [String: String]?
    var parameters: [String: Any]?
    let parametersEncoding: ParametersEncoding?
    var body: Data?
    var multipartData: [MultipartData]?
    
    init(_ resourceRequest: ONGResourceRequest) {
        self.path = resourceRequest.path
        self.method = HTTPMethod(rawValue: resourceRequest.method)!
        self.headers = resourceRequest.headers
        self.parameters = resourceRequest.parameters
        var parametersEncoding: ParametersEncoding
        if resourceRequest.parametersEncoding == .formURL {
            parametersEncoding = .formURL
        } else {
            parametersEncoding = .JSON
        }
        self.parametersEncoding = parametersEncoding
        self.body = resourceRequest.body
        self.multipartData = resourceRequest.multipartData?.map({ data -> MultipartData in
            return MultipartDataImplementation(data)
        })
    }
    
    init(path: String,
         method: HTTPMethod = .get,
         parameters: [String: Any]? = nil,
         body: Data? = nil,
         headers: [String: String]? = nil,
         parametersEncoding: ParametersEncoding = .JSON) {
        
        self.path = path
        self.method = method
        self.parameters = parameters
        self.body = body
        self.headers = headers
        self.parametersEncoding = parametersEncoding
    }
    
    init(path: String,
         method: HTTPMethod,
         parameters: [String: Any]?,
         multipartData: [MultipartData]) {
        
        self.path = path
        self.method = method
        self.parameters = parameters
        self.multipartData = multipartData
        self.parametersEncoding = .JSON
    }
}

// FIXME: Remove when using native sdk swift api for Anonymous resource requests
private class MultipartDataImplementation: MultipartData {
    var data: Data
    var name: String
    var fileName: String
    var mimeType: String
    
    init(_ multipartData: ONGMultipartData) {
        self.data = multipartData.data
        self.name = multipartData.name
        self.fileName = multipartData.fileName
        self.mimeType = multipartData.mimeType
    }
}
