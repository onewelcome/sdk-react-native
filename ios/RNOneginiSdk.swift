import Foundation
import React
import OneginiSDKiOS


protocol ConnectorToRNBridgeProtocol: NSObject {
  func sendBridgeEvent(eventName: OneginiBridgeEvents, data: Any!) -> Void
}

// Pin notification actions for RN Bridge
enum OneginiBridgeEvents : String {
    case pinNotification = "ONEGINI_PIN_NOTIFICATION"
    case fingerprintNotification = "ONEGINI_FINGERPRINT_NOTIFICATION"
    case customRegistrationNotification = "ONEGINI_CUSTOM_REGISTRATION_NOTIFICATION"
    case authWithOtpNotification = "ONEGINI_MOBILE_AUTH_OTP_NOTIFICATION"
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
        return [OneginiBridgeEvents.pinNotification.rawValue, OneginiBridgeEvents.fingerprintNotification.rawValue, OneginiBridgeEvents.customRegistrationNotification.rawValue, OneginiBridgeEvents.authWithOtpNotification.rawValue]
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
    func getUserProfiles(_ resolve: RCTPromiseResolveBlock, rejecter reject:RCTPromiseRejectBlock) -> Void {
        let profiles = ONGClient.sharedInstance().userClient.userProfiles()
        var result: NSMutableArray  = []

        for profile in profiles {
            result.add(["profileId": profile.value(forKey: "profileId")])
        }

        resolve(result)
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
    func deregisterUser(_ profileId: (NSString),
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile: ONGUserProfile = ONGClient.sharedInstance().userClient.userProfiles().first(where: { $0.value(forKey: "profileId") as! NSObject == profileId })!;

        ONGClient.sharedInstance().userClient.deregisterUser(profile) {
            (result: Bool, error) -> Void in

            if let error = error {
                let mappedError = ErrorMapper().mapError(error);
                reject(nil, mappedError.errorDescription, nil)
              } else {
                resolve(true)
              }
        }
    }

    @objc
    func handleRegistrationCallback(_ url: (NSString)) -> Void {
        bridgeConnector.toRegistrationHandler.processRedirectURL(url: URL(string: url as String)!)
    }

    @objc
    func cancelRegistration() -> Void {
        bridgeConnector.toRegistrationHandler.cancelRegistration()
    }

    @objc
    func submitPinAction(_ flow: (NSString), action: (NSString), pin: (NSString)) -> Void {
        bridgeConnector.toPinHandlerConnector.handlePinAction(flow, action, pin)
    }

    @objc
    func changePin(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        bridgeConnector.toPinHandlerConnector.pinHandler.onChangePinCalled() {
            (_, error) -> Void in

            if let error = error {
                reject(nil, error.errorDescription, nil)
            } else {
                resolve(true)
            }
        }
    }

    @objc
    func authenticateUser(_ profileId: (NSString),
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let profile: ONGUserProfile = ONGClient.sharedInstance().userClient.userProfiles().first(where: { $0.value(forKey: "profileId") as! NSObject == profileId })!;

        bridgeConnector.toLoginHandler.authenticateUser(profile) {
            (userProfile, error) -> Void in

            if let error = error {
                reject(nil, error.errorDescription, nil)
              } else {
                resolve(true)
              }
        }
    }

    @objc
    func logout(_ callback: @escaping (RCTResponseSenderBlock)) -> Void {
        ONGClient.sharedInstance().userClient.logoutUser { _, error in
            if let error = error {
                let mappedError = ErrorMapper().mapError(error);
                callback([["success" : false, "errorMsg" : mappedError.errorDescription]])
              } else {
                callback([["success" : true]])
              }
        }
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
    
    @objc
    func startSingleSignOn(_ url: (NSString),
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        let _url = URL(string: url)
        bridgeConnector.toAppToWebHandler.signInAppToWeb(targetURL: _url, completion: { (result, error) in
            if let error = error {
                reject(nil, error.errorDescription, nil)
            } else {
                resolve(result)
            }
        })
    }
}
