extension Error {
    var domain: String { return (self as NSError).domain }
    var code: Int { return (self as NSError).code }
    var userInfo: [String: Any] { return (self as NSError).userInfo }
}
