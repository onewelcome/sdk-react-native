protocol MobileAuthConnectorToHandlerProtocol: AnyObject {
    func enrollForMobileAuth(_ completion: @escaping (Bool?, NSError?) -> Void)
    func isUserEnrolledForMobileAuth() -> Bool
    func handleMobileAuthConfirmation(accepted: Bool)
    func handleOTPMobileAuth(_ otp: String, _ completion: @escaping (Bool, NSError?) -> Void)
}

enum MobileAuthAuthenticatorType: String {
    case fingerprint = "biometric"
    case pin = "PIN"
    case confirmation = ""
}

class MobileAuthHandler: NSObject {
    var userProfile: ONGUserProfile?
    var message: String?
    var authenticatorType: MobileAuthAuthenticatorType?
    var confirmation: ((Bool) -> Void)?
    var mobileAuthCompletion: ((Bool, NSError?) -> Void)?

    fileprivate func handleConfirmationMobileAuth(_ accepted: Bool) {
        guard let confirmation = confirmation else { fatalError() }

        confirmation(accepted)
    }


    private func sendConnectorNotification(_ event: MobileAuthNotification, _ requestMessage: String?, _ error: NSError?) {
        BridgeConnector.shared?.toMobileAuthConnector.sendNotification(event: event, requestMessage: requestMessage, error: error)
    }
}

extension MobileAuthHandler : MobileAuthConnectorToHandlerProtocol {
    func enrollForMobileAuth(_ completion: @escaping (Bool?, NSError?) -> Void) {
        ONGClient.sharedInstance().userClient.enroll { enrolled, error in
            if let error = error {
                completion(false, error as NSError);
              } else {
                completion(true, nil)
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

    func handleMobileAuthConfirmation(accepted: Bool) {
        if authenticatorType == .fingerprint {
            //@todo
        } else if authenticatorType == .confirmation {
            handleConfirmationMobileAuth(accepted)
        } else if authenticatorType == .pin {
            //@todo
        }
    }

    func handleOTPMobileAuth(_ otp: String, _ completion: @escaping (Bool, NSError?) -> Void) {
        mobileAuthCompletion = completion
        ONGUserClient.sharedInstance().handleOTPMobileAuthRequest(otp, delegate: self)
    }
}

extension MobileAuthHandler: ONGMobileAuthRequestDelegate {
    func userClient(_ userClient: ONGUserClient, didFailToHandle request: ONGMobileAuthRequest, authenticator: ONGAuthenticator?, error: Error) {
        mobileAuthCompletion!(false, error as NSError)
    }
    
    func userClient(_ userClient: ONGUserClient, didHandle request: ONGMobileAuthRequest, authenticator: ONGAuthenticator?, info customAuthenticatorInfo: ONGCustomInfo?) {
        mobileAuthCompletion!(true, nil)
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
