protocol MobileAuthConnectorToHandlerProtocol: AnyObject {
    func enrollForMobileAuth(_ completion: @escaping (Bool?, SdkError?) -> Void)
    func isUserEnrolledForMobileAuth() -> Bool
    func handleMobileAuthConfirmation(cancelled: Bool)
    func handleOTPMobileAuth(_ otp: String, _ completion: @escaping (Bool, SdkError?) -> Void)
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
    var mobileAuthCompletion: ((Bool, SdkError?) -> Void)?
    
    fileprivate func handleConfirmationMobileAuth(_ cancelled: Bool) {
        guard let confirmation = confirmation else { fatalError() }
        
        confirmation(cancelled)
    }

    
    private func sendConnectorNotification(_ event: MobileAuthNotification, _ requestMessage: String?, _ error: SdkError?) {
        BridgeConnector.shared?.toMobileAuthConnector.sendNotification(event: event, requestMessage: requestMessage, error: error)
    }
}

extension MobileAuthHandler : MobileAuthConnectorToHandlerProtocol {
    func enrollForMobileAuth(_ completion: @escaping (Bool?, SdkError?) -> Void) {
        ONGClient.sharedInstance().userClient.enroll { enrolled, error in
            if let error = error {
                let mappedError = ErrorMapper().mapError(error);
                completion(false, mappedError)
              } else {
                if(enrolled == false){
                    completion(false, SdkError(errorDescription: "Enrollment failed. Please try again or contact maintainer."))
                    return;
                }
                
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
    
    func handleMobileAuthConfirmation(cancelled: Bool) {
        if authenticatorType == .fingerprint {
            //@todo
        } else if authenticatorType == .confirmation {
            handleConfirmationMobileAuth(cancelled)
        } else if authenticatorType == .pin {
            //@todo
        }
    }
    
    func handleOTPMobileAuth(_ otp: String, _ completion: @escaping (Bool, SdkError?) -> Void) {
        mobileAuthCompletion = completion
        ONGUserClient.sharedInstance().handleOTPMobileAuthRequest(otp, delegate: self)
    }
}

extension MobileAuthHandler: ONGMobileAuthRequestDelegate {
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

    func userClient(_: ONGUserClient, didFailToHandle _: ONGMobileAuthRequest, error: Error) {
        if error.code == ONGGenericError.actionCancelled.rawValue {
            mobileAuthCompletion!(false, SdkError(errorDescription: "Authentication cancelled."))
        } else {
            let mappedError = ErrorMapper().mapError(error)
            mobileAuthCompletion!(false, mappedError)
        }
    }

    func userClient(_: ONGUserClient, didHandle _: ONGMobileAuthRequest, info _: ONGCustomInfo?) {
        mobileAuthCompletion!(true, nil)
    }
}
