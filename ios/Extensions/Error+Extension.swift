extension Error {
    var codeString: String { String(describing: code) }
    
    var domain: String { return (self as NSError).domain }
    var code: Int { return (self as NSError).code }
    var userInfo: Dictionary<String, Any> { return (self as NSError).userInfo }

    var dictionaryRepresentation: [String: Any] {
        return ["code": code, "domain": domain, "userInfo": userInfo]
    }
}
