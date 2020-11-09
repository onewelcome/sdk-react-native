import Foundation
import React
import OneginiSDKiOS


protocol ConnectorToRNBridgeProtocol: NSObject {
  func sendBridgeEvent(eventName: OneginiBridgeEvents, data: Any!) -> Void
}

// Pin notification actions for RN Bridge
enum OneginiBridgeEvents : String {
    case pinNotification = "ONEGINI_PIN_NOTIFICATION"
}

@objc(RNOneginiSdk)
class RNOneginiSdk: RCTEventEmitter, ConnectorToRNBridgeProtocol {

  var bridgeConnector: BridgeConnector

    override init() {
        self.bridgeConnector = BridgeConnector()
        super.init()
        self.bridgeConnector.bridge = self
    }

    override func supportedEvents() -> [String]! {
        return [OneginiBridgeEvents.pinNotification.rawValue]
    }

    @objc
    override static func requiresMainQueueSetup() -> Bool {
        return true
    }

    @objc
    func startClient(_ callback: @escaping (RCTResponseSenderBlock)) -> Void {
        self.oneginiSDKStartup { _, error in
            if let error = error {
                callback([["success": false, "errorMsg": error.errorDescription]])
            } else {
                callback([["success": true]])
            }
        }
    }

    @objc
    func getRedirectUri(_ callback: (RCTResponseSenderBlock)) -> Void {
        let redirectUri = ONGClient.sharedInstance().configModel.redirectURL;

        callback([["success" : true, "redirectUri" : redirectUri!]])
    }

    @objc
    func registerUser(_ identityProviderId: (NSString?), callback: @escaping (RCTResponseSenderBlock)) -> Void {
        bridgeConnector.toRegistrationHandler.signUp {
          (_, userProfile, error) -> Void in

            if let userProfile = userProfile {
                callback([["success" : true, "profileId" : userProfile.profileId!]])
            } else {
                callback([["success" : false, "errorMsg" : error?.errorDescription ?? "Unexpected Error."]])
            }

        }
    }

    @objc
    func handleRegistrationCallback(_ url: (NSString)) -> Void {
        bridgeConnector.toRegistrationHandler.handleRedirectURL(url: URL(string: url as String)!)
    }

    @objc
    func cancelRegistration() -> Void {
        bridgeConnector.toRegistrationHandler.cancelRegistration()
    }

    @objc
    func submitPinAction(_ action: (NSString), isCreatePinFlow: (NSNumber), pin: (NSString)) -> Void {
        bridgeConnector.toChangePinConnector.handlePinAction(action, isCreatePinFlow, pin)
    }

    func sendBridgeEvent(eventName: OneginiBridgeEvents, data: Any!) -> Void {
      self.sendEvent(withName: eventName.rawValue, body: data)
    }

    private func oneginiSDKStartup(completion: @escaping (Bool, SdkError?) -> Void) {
        ONGClientBuilder().build()
        ONGClient.sharedInstance().start { result, error in
            if let error = error {
                let mappedError = ErrorMapper().mapError(error)
                completion(result, mappedError)
            } else {
                completion(result, nil)
            }
        }
    }
}
