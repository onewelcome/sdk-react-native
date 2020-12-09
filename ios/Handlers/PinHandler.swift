protocol PinConnectorToPinHandler: AnyObject {
    func onPinProvided(pin: (NSString))
    func onCancel()
    func handleFlowUpdate(_ flow: PinFlow, _ error: SdkError?, reciever: PinHandlerToReceiverProtocol)
    func closeFlow()
}

protocol ChangePinProtocol: AnyObject {
    func startChangePinFlow()
    func presentCreatePinFlow(error: SdkError?)
    func presentProfileView()
    func changePinActionFailed(_ error: SdkError)
    func popToWelcomeViewWithError(_ error: SdkError)
}

protocol PinHandlerToReceiverProtocol: class {
    func handlePin(pin: String?)
}

enum PINEntryMode {
    case login
    case registration
    case registrationConfirm
}

class PinHandler: NSObject {
    var createPinChallenge: ONGCreatePinChallenge?
    var flow: PinFlow?
    var mode: PINEntryMode?
    var pinEntryToVerify = Array<String>()
    unowned var pinReciever: PinHandlerToReceiverProtocol?

    func processPin(pinEntry: Array<String>) {
        let pincode = pinEntry.joined()
        switch mode {
          case .registration:
              handleRegistrationPin(pinEntry)
              break
          case .registrationConfirm:
              handleConfirmRegistrationPin(pinEntry, pincode)
              break
          case .login:
                pinReciever?.handlePin(pin: pincode)
              break
          case .none:
              notifyOnError(SdkError(title: "Pin validation error", errorDescription: "Unexpected PIN mode.", recoverySuggestion: "Open and close modal."))
              break
        }
    }

    private func processCancelAction() {
        mode = nil
        pinReciever?.handlePin(pin: nil)
    }

    private func handleRegistrationPin(_ pinEntry: Array<String>) {
        pinEntryToVerify = pinEntry
        mode = .registrationConfirm
        sendConnectorNotification(PinNotification.confirm, mode, nil)
    }

    private func handleConfirmRegistrationPin(_ pinEntry: Array<String>, _ pincode: String) {
        let pincodeConfirm = pinEntryToVerify.joined()
        if pincode == pincodeConfirm {
            pinReciever?.handlePin(pin: pincode)
        } else {
            mode = .registration
            notifyOnError(SdkError(title: "Pin validation error", errorDescription: "The confirmation PIN does not match.", recoverySuggestion: "Try a different one."))
        }
    }

    func notifyOnError(_ error: SdkError) {
        sendConnectorNotification(PinNotification.showError, mode, error)

        if(mode == PINEntryMode.registrationConfirm) {
            mode = .registration
        }
    }

    private func sendConnectorNotification(_ event: PinNotification, _ mode: PINEntryMode?, _ error: SdkError?) {
        BridgeConnector.shared?.toPinHandlerConnector.sendNotification(event: event, mode: mode, error: error)
    }
}

extension PinHandler : PinConnectorToPinHandler {
    // @todo Support different pinLength
    func handleFlowUpdate(_ flow: PinFlow, _ error: SdkError?, reciever: PinHandlerToReceiverProtocol) {

        pinReciever = reciever

        if(error != nil){
            notifyOnError(error!)
        } else {
            if(mode == nil) {
                switch flow {
                    case PinFlow.authentication:
                        mode = .login
                        break
                    case PinFlow.create:
                        mode = .registration
                        break
                    default:
                        mode = .registration
                        break
                }

                sendConnectorNotification(PinNotification.open, mode, nil)
            }
        }
    }

    func closeFlow() {
        mode = nil
        flow = nil
        sendConnectorNotification(PinNotification.close, mode, nil)
    }

    func onPinProvided(pin: (NSString)) {
      let characters: String = pin as String
      let pinArray: Array<String> = Array(arrayLiteral: characters)

      processPin(pinEntry: pinArray)
    }

    func onCancel() {
        processCancelAction()
    }
 }

extension PinHandler : ChangePinProtocol {
    func startChangePinFlow() {

    }

    func presentCreatePinFlow(error: SdkError?) {
//        BridgeConnector.shared?.toPinHandlerConnector.pinHandler.setPinReciever(reciever: self)
//
//        if(error != nil){
//            BridgeConnector.shared?.toPinHandlerConnector.pinHandler.notifyOnError(error!)
//        } else {
//            BridgeConnector.shared?.toPinHandlerConnector.pinHandler.openFlow(PinFlow.change)
//        }
    }

    func presentProfileView() {
        //@todo will need this at MVP phase
    }

    func popToWelcomeViewWithError(_ error: SdkError) {
      //@todo will need this at MVP phase
    }

    func changePinActionFailed(_ error: SdkError) {
      //@todo will need this at MVP phase
    }

    func handlePin(pin: String?) {
        guard let createPinChallenge = self.createPinChallenge else { return }

        if(pin != nil) {
            createPinChallenge.sender.respond(withCreatedPin: pin!, challenge: createPinChallenge)

        } else {
            createPinChallenge.sender.cancel(createPinChallenge)
        }
    }

    fileprivate func mapErrorFromPinChallenge(_ challenge: ONGCreatePinChallenge) -> SdkError? {
        if let error = challenge.error {
            return ErrorMapper().mapError(error)
        } else {
            return nil
        }
    }
}

extension PinHandler: ONGChangePinDelegate {
    func userClient(_ userClient: ONGUserClient, didReceive challenge: ONGPinChallenge) {
//        changePinEntity.loginPinChallenge = challenge
//        changePinEntity.pinLength = 5
//        mapErrorFromPinChallenge(challenge)
//        changePinPresenter?.presentLoginPinView(changePinEntity: changePinEntity)

      //@todo will need this in the future
    }

    func userClient(_: ONGUserClient, didReceive challenge: ONGCreatePinChallenge) {
        self.createPinChallenge = challenge
        let pinError = mapErrorFromPinChallenge(challenge)
        self.presentCreatePinFlow(error: pinError)
    }

    func userClient(_: ONGUserClient, didFailToChangePinForUser _: ONGUserProfile, error: Error) {
        self.createPinChallenge = nil
        let mappedError = ErrorMapper().mapError(error)

        if error.code == ONGGenericError.actionCancelled.rawValue {
            self.presentProfileView()
        } else if error.code == ONGGenericError.userDeregistered.rawValue {
            self.popToWelcomeViewWithError(mappedError)
        } else {
            self.changePinActionFailed(mappedError)
        }
    }

    func userClient(_: ONGUserClient, didChangePinForUser _: ONGUserProfile) {
        self.createPinChallenge = nil
        self.presentProfileView()
    }
}
