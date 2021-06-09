import Foundation

protocol MainConnector {
    var sendEventHandler: ((Event) -> ())? { get set }
    var supportedEventsNames: [String] { get }
    func startClient(_ resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock)
    
    func registerUser(identityProviderId:String?, scopes: [String], _ resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock)
    func openURL(_ url: URL, browser: WebBrowser)
    func sendPin(_ pin: String)
    
}

class DefaultMainConnector: MainConnector {
    let factory: MainConnectorFactory
    private var registerUserConnector: RegisterUserConnector?
    
    var sendEventHandler: ((Event) -> ())?
    
    var supportedEventsNames: [String] {
        return RegisterEvent.allNames
    }
    
    init(factory: MainConnectorFactory = DefaultMainConnectorFactory()) {
        self.factory = factory
    }
    
    func startClient(_ resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        factory.startClientConnector.startClient{ $0.convertTo(resolve: resolve, reject: reject) }
    }
    
    func registerUser(identityProviderId:String?, scopes: [String], _ resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        registerUserConnector = factory.registerUserConnector
        registerUserConnector?.registerUser(identityProviderId: identityProviderId, scopes: scopes, statusChanged: { [weak self] status in
            self?.sendEventHandler?(status)
        }) { $0.convertTo(resolve: resolve, reject: reject) }
    }
    
    func openURL(_ url: URL, browser: WebBrowser = .safari ) {
        
    }

    func sendPin(_ pin: String) {
        registerUserConnector?.sendPin(pin)
    }
}

enum WebBrowser {
    case safari
    case external
}
