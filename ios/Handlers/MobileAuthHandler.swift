protocol MobileAuthConnectorToHandlerProtocol: AnyObject {
    func enrollForMobileAuth(_ completion: @escaping (Error?) -> Void)
    func isUserEnrolledForMobileAuth() -> Bool
    func handleMobileAuthConfirmation(accepted: Bool, completion: @escaping (Error?) -> Void)
    func handleOTPMobileAuth(_ otp: String, _ completion: @escaping (Error?) -> Void)
}

enum MobileAuthAuthenticatorType: String {
    case fingerprint = "biometric"
    case pin = "PIN"
    case confirmation = ""
}

class MobileAuthHandler: NSObject {
    private var userProfile: ONGUserProfile?
    private var message: String?
    private var authenticatorType: MobileAuthAuthenticatorType?
    private var confirmation: ((Bool) -> Void)?
    private var mobileAuthCompletion: ((Error?) -> Void)?


    private func sendConnectorNotification(_ event: MobileAuthNotification, _ requestMessage: String?, _ error: Error?) {
        BridgeConnector.shared?.toMobileAuthConnector.sendNotification(event: event, requestMessage: requestMessage, error: error)
    }
}

extension MobileAuthHandler : MobileAuthConnectorToHandlerProtocol {
    
    func enrollForMobileAuth(_ completion: @escaping (Error?) -> Void) {
        ONGClient.sharedInstance().userClient.enroll { enrolled, error in
            if let error = error {
                completion(error);
              } else {
                completion(nil)
              }
        }
    }

    func isUserEnrolledForMobileAuth() -> Bool {
        let userClient = ONGUserClient.sharedInstance()
        if let userProfile = userClient.authenticatedUserProfile() {
            return userClient.isUserEnrolled(forMobileAuth: userProfile)
        }
        return false
    }

    func handleMobileAuthConfirmation(accepted: Bool, completion: @escaping (Error?) -> Void) {
        if authenticatorType == .fingerprint {
            //@todo RN-94
        } else if authenticatorType == .confirmation {
            guard let confirmation = confirmation else {
                return completion(WrapperError.mobileAuthNotInProgress)
            }
            confirmation(accepted)
            return completion(nil)
        } else if authenticatorType == .pin {
            //@todo RN-94
        }
        return completion(WrapperError.mobileAuthNotInProgress)
    }

    func handleOTPMobileAuth(_ otp: String, _ completion: @escaping (Error?) -> Void) {
        mobileAuthCompletion = completion
        ONGUserClient.sharedInstance().handleOTPMobileAuthRequest(otp, delegate: self)
    }
}

extension MobileAuthHandler: ONGMobileAuthRequestDelegate {
    func userClient(_ userClient: ONGUserClient, didFailToHandle request: ONGMobileAuthRequest, authenticator: ONGAuthenticator?, error: Error) {
        mobileAuthCompletion?(error)
        mobileAuthCompletion = nil
    }
    
    func userClient(_ userClient: ONGUserClient, didHandle request: ONGMobileAuthRequest, authenticator: ONGAuthenticator?, info customAuthenticatorInfo: ONGCustomInfo?) {
        mobileAuthCompletion?(nil)
        mobileAuthCompletion = nil
    }
    
    func userClient(_: ONGUserClient, didReceiveConfirmationChallenge confirmation: @escaping (Bool) -> Void, for request: ONGMobileAuthRequest) {
        message = request.message
        userProfile = request.userProfile
        authenticatorType = .confirmation
        self.confirmation = confirmation
        sendConnectorNotification(MobileAuthNotification.startAuthentication, request.message, nil)
    }

    func userClient(_: ONGUserClient, didReceive challenge: ONGPinChallenge, for request: ONGMobileAuthRequest) {
       //@todo will need this for PUSH PIN?
    }

    func userClient(_: ONGUserClient, didReceive challenge: ONGBiometricChallenge, for request: ONGMobileAuthRequest) {
        //@todo will need this for PUSH Fingerprint?
    }

    func userClient(_: ONGUserClient, didReceive challenge: ONGCustomAuthFinishAuthenticationChallenge, for request: ONGMobileAuthRequest) {
        //@todo will need this for PUSH Custom?
    }

}
