import Foundation

protocol MainConnector {
    var sendEventHandler: ((Event) -> ())? { get set }
    func startClient(_ resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock)
    
    func registerUser(identityProviderId:String?, scopes: [String], _ resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock)
    func handleCustomRegistrationAction(_ action: NSString, _ identityProviderId: NSString, _ code: NSString?)
    func sendPin(_ pin: String)
    func handlePinAction(_ flow: NSString, _ action: NSString, _ pin: NSString)
    func cancelRegistration()
    func processRedirectURL(url: URL)
}

class DefaultMainConnector: MainConnector {
    let factory: MainConnectorFactory
    private var registerUserConnector: RegisterUserConnector?
    private let pinConnector: PinConnector
    
    var sendEventHandler: ((Event) -> ())?
    
    init(factory: MainConnectorFactory = DefaultMainConnectorFactory()) {
        self.factory = factory
        self.pinConnector = factory.pinConnector
    }
    
    func startClient(_ resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        factory.startClientConnector.startClient{ $0.convertTo(resolve: resolve, reject: reject) }
    }
    
    func registerUser(identityProviderId:String?, scopes: [String], _ resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        registerUserConnector = factory.registerUserConnector(with: pinConnector)
        registerUserConnector?.registerUser(identityProviderId: identityProviderId, scopes: scopes, statusChanged: { [weak self] status in
            self?.sendEventHandler?(status)
        }) { $0.convertTo(resolve: resolve, reject: reject) }
    }

    func handleCustomRegistrationAction(_ action: NSString, _ identityProviderId: NSString, _ code: NSString?) {
        registerUserConnector?.handleCustomRegistrationAction(action, identityProviderId, code)
    }

    func sendPin(_ pin: String) {
        registerUserConnector?.sendPin(pin)
    }

    func handlePinAction(_ flow: NSString, _ action: NSString, _ pin: NSString) {
        pinConnector.handlePinAction(flow, action, pin)
    }

    func cancelRegistration() {
        registerUserConnector?.cancelRegistration()
    }

    func processRedirectURL(url: URL) {
        registerUserConnector?.processRedirectURL(url: url)
    }
}
