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

extension MobileAuthHandler: MobileAuthConnectorToHandlerProtocol {

    func enrollForMobileAuth(_ completion: @escaping (Error?) -> Void) {
        ONGClient.sharedInstance().userClient.enroll { _, error in
            completion(error)
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
        // FIXME: RNP-94 Check if this method is implemented correctly
        guard let confirmation = confirmation else {
            completion(WrapperError.mobileAuthNotInProgress)
            return
        }
        confirmation(accepted)
        completion(nil)
        return
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
       // FIXME: RNP-94 use for PUSH with Pin
    }

    func userClient(_: ONGUserClient, didReceive challenge: ONGBiometricChallenge, for request: ONGMobileAuthRequest) {
        // FIXME: RNP-94 use for PUSH with Fingerprint
    }

    func userClient(_: ONGUserClient, didReceive challenge: ONGCustomAuthFinishAuthenticationChallenge, for request: ONGMobileAuthRequest) {
        // We don't support custom authenticators
    }

}
