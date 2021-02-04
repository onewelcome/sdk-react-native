protocol BridgeToResourceFetchHandlerProtocol: AnyObject {
    func getAppDetails(_ completion: @escaping (ApplicationDetails?, SdkError?) -> Void)
    func getDeviceList(_ completion: @escaping (Devices?, SdkError?) -> Void)
    func getImplicitData(_ profile: ONGUserProfile, _ completion: @escaping (String?, SdkError?) -> Void)
}


class ResourceFetchHandler: BridgeToResourceFetchHandlerProtocol {
    let appDetailsFetcher: AppDetailsFetcherProtocol = AppDetailsFetcher()
    let deviceListFetcher: DeviceListFetcherProtocol = DeviceListFetcher()
    let implicitDataFetcher: ImplicitDataFetcherProtocol = ImplicitDataFetcher()

    func getAppDetails(_ completion: @escaping (ApplicationDetails?, SdkError?) -> Void) {
        appDetailsFetcher.fetchResources(completion)
    }

    func getDeviceList(_ completion: @escaping (Devices?, SdkError?) -> Void) {
        deviceListFetcher.fetchResources(completion)
    }
    
    func getImplicitData(_ profile: ONGUserProfile, _ completion: @escaping (String?, SdkError?) -> Void) {
        implicitDataFetcher.fetchResources(profile, completion)
    }
}
