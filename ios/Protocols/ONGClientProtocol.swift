import OneginiSDKiOS

protocol ONGClientProtocol {
    func start(_ completion:@escaping (Bool, Error?) -> Void)
    func reset(_ completion:@escaping (Bool, Error?) -> Void)
}

extension ONGClient: ONGClientProtocol {}
