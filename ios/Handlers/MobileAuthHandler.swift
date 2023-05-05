protocol MobileAuthConnectorToHandlerProtocol: AnyObject {
    func enrollForMobileAuth(_ completion: @escaping (Result<Void, Error>) -> Void)
    func handleMobileAuthConfirmation(accepted: Bool, completion: @escaping (Result<Void, Error>) -> Void)
    func handleOTPMobileAuth(_ otp: String, _ completion: @escaping (Result<Void, Error>) -> Void)
}

private func sendConnectorNotification(_ event: MobileAuthNotification, _ requestMessage: String?, _ error: Error?) {
    BridgeConnector.shared?.toMobileAuthConnector.sendNotification(event: event, requestMessage: requestMessage, error: error)
}

class MobileAuthHandler: NSObject {
    private var mobileAuthConfirmation: ((Bool) -> Void)?
}

extension MobileAuthHandler: MobileAuthConnectorToHandlerProtocol {

    func enrollForMobileAuth(_ completion: @escaping (Result<Void, Error>) -> Void) {
        SharedUserClient.instance.enrollMobileAuth { error in
            guard let error = error else {
                completion(.success)
                return
            }
            completion(.failure(error))
        }
    }

    func handleMobileAuthConfirmation(accepted: Bool, completion: @escaping (Result<Void, Error>) -> Void) {
        guard let mobileAuthConfirmation = mobileAuthConfirmation else {
            completion(.failure(WrapperError.mobileAuthNotInProgress))
            return
        }
        mobileAuthConfirmation(accepted)
        completion(.success)
    }

    func handleOTPMobileAuth(_ otp: String, _ completion: @escaping (Result<Void, Error>) -> Void) {
        // Check to prevent breaking iOS SDK; https://onewelcome.atlassian.net/browse/SDKIOS-987
        guard SharedUserClient.instance.authenticatedUserProfile != nil else {
            completion(.failure(WrapperError.noProfileAuthenticated))
            return
        }

        // Prevent concurrent OTP mobile authentication flows at same time; https://onewelcome.atlassian.net/browse/SDKIOS-989
        guard mobileAuthConfirmation == nil else {
            completion(.failure(WrapperError.mobileAuthAlreadyInProgress))
            return
        }

        let delegate = MobileAuthDelegate(handler: self, completion: completion)
        SharedUserClient.instance.handleOTPMobileAuthRequest(otp: otp, delegate: delegate)
    }

    func handleDidReceiveMobileAuthConfirmation(_ confirmation: @escaping (Bool) -> Void) {
        mobileAuthConfirmation = confirmation
    }

    func handleDidFinishMobileAuth() {
        mobileAuthConfirmation = nil
    }
}

// MARK: - MobileAuthRequestDelegate
class MobileAuthDelegate: MobileAuthRequestDelegate {
    private let handleMobileAuthCompletion: (Result<Void, Error>) -> Void
    private let mobileAuthHandler: MobileAuthHandler

    init(handler: MobileAuthHandler, completion: @escaping (Result<Void, Error>) -> Void) {
        self.handleMobileAuthCompletion = completion
        self.mobileAuthHandler = handler
    }

    func userClient(_ userClient: UserClient, didReceiveConfirmation confirmation: @escaping (Bool) -> Void, for request: MobileAuthRequest) {
        mobileAuthHandler.handleDidReceiveMobileAuthConfirmation(confirmation)
        sendConnectorNotification(MobileAuthNotification.startAuthentication, request.message, nil)
    }

    func userClient(_ userClient: UserClient, didReceivePinChallenge challenge: PinChallenge, for request: MobileAuthRequest) {
        // todo: will need this for PUSH
    }

    func userClient(_ userClient: UserClient, didReceiveBiometricChallenge challenge: BiometricChallenge, for request: MobileAuthRequest) {
        // todo: will need this for PUSH
    }

    func userClient(_ userClient: UserClient, didReceiveCustomAuthFinishAuthenticationChallenge challenge: CustomAuthFinishAuthenticationChallenge, for request: MobileAuthRequest) {
        // todo: will need this for PUSH Custom
    }

    func userClient(_ userClient: UserClient, didFailToHandleRequest request: MobileAuthRequest, authenticator: Authenticator?, error: Error) {
        mobileAuthHandler.handleDidFinishMobileAuth()
        sendConnectorNotification(MobileAuthNotification.finishAuthentication, request.message, nil)
        handleMobileAuthCompletion(.failure(error))
    }

    func userClient(_ userClient: UserClient, didHandleRequest request: MobileAuthRequest, authenticator: Authenticator?, info customAuthenticatorInfo: CustomInfo?) {
        mobileAuthHandler.handleDidFinishMobileAuth()
        sendConnectorNotification(MobileAuthNotification.startAuthentication, request.message, nil)

        handleMobileAuthCompletion(.success)
    }
}
