protocol PinConnectorToPinHandler: AnyObject {
    func onPinProvided(pin: (NSString))
    func onChangePinCalled(completion: @escaping (Bool, NSError?) -> Void)
    func onCancel()
    func handleFlowUpdate(_ flow: PinFlow, error: NSError?, receiver: PinHandlerToReceiverProtocol, userInfo: [String: Any]?)
    func closeFlow()
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
    var authPinChallenge: ONGPinChallenge?
    var createPinChallenge: ONGCreatePinChallenge?
    var flow: PinFlow?
    var mode: PINEntryMode?
    var pinEntryToVerify = Array<String>()
    var changePinCompletion: ((Bool, NSError?) -> Void)?
    unowned var pinReceiver: PinHandlerToReceiverProtocol?

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
                pinReceiver?.handlePin(pin: pincode)
              break
          case .none:
            let error = NSError(domain: ONGPinValidationErrorDomain, code: 0, userInfo: [NSLocalizedDescriptionKey : "Unexpected PIN mode."])
              notifyOnError(error)
              break
        }
    }

    private func processCancelAction() {
        mode = nil
        pinReceiver?.handlePin(pin: nil)
    }

    private func handleRegistrationPin(_ pinEntry: Array<String>) {
        pinEntryToVerify = pinEntry
        mode = .registrationConfirm
        sendConnectorNotification(PinNotification.confirm, flow, nil)
    }

    private func handleConfirmRegistrationPin(_ pinEntry: Array<String>, _ pincode: String) {
        let pincodeConfirm = pinEntryToVerify.joined()
        if pincode == pincodeConfirm {
            pinReceiver?.handlePin(pin: pincode)
        } else {
            mode = .registration
            let error = NSError(domain: ONGPinValidationErrorDomain, code: 0, userInfo: [NSLocalizedDescriptionKey : "The confirmation PIN does not match."])
            notifyOnError(error)
        }
    }

    func notifyOnError(_ error: NSError, userInfo: [String: Any]? = nil) {
        sendConnectorNotification(PinNotification.showError, flow, error, userInfo)

        if(mode == PINEntryMode.registrationConfirm) {
            mode = .registration
        }
    }

    private func sendConnectorNotification(_ event: PinNotification, _ flow: PinFlow?, _ error: NSError?,  _ userInfo: [String: Any]? = nil) {
        BridgeConnector.shared?.toPinHandlerConnector.sendNotification(event: event, flow: flow, error: error, userInfo: userInfo)
    }
}

extension PinHandler : PinConnectorToPinHandler {
    // @todo Support different pinLength
    func handleFlowUpdate(_ flow: PinFlow, error: NSError?, receiver: PinHandlerToReceiverProtocol, userInfo:[String: Any]? = nil) {
        if(self.flow == nil){
            self.flow = flow
            pinReceiver = receiver
        }


        if(error != nil){
            notifyOnError(error!, userInfo: userInfo)
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

                sendConnectorNotification(PinNotification.open, flow, nil, userInfo)
            }
        }
    }

    func closeFlow() {
        if(flow != nil){
            mode = nil
            flow = nil
            sendConnectorNotification(PinNotification.close, flow, nil)
        }
    }

    func onPinProvided(pin: (NSString)) {
      let characters: String = pin as String
      let pinArray: Array<String> = Array(arrayLiteral: characters)

      processPin(pinEntry: pinArray)
    }

    func onChangePinCalled(completion: @escaping (Bool, NSError?) -> Void) {
        changePinCompletion = completion
        ONGUserClient.sharedInstance().changePin(self)
    }

    func onCancel() {
        processCancelAction()
    }
 }

extension PinHandler : PinHandlerToReceiverProtocol {
    func handlePin(pin: String?) {
        if let authPinChallenge = self.authPinChallenge {
            if(pin != nil) {
                authPinChallenge.sender.respond(withPin: pin!, challenge: authPinChallenge)

            } else {
                authPinChallenge.sender.cancel(authPinChallenge)
            }

            return
        }
        
        if let createPinChallenge = self.createPinChallenge {
            if(pin != nil) {
                createPinChallenge.sender.respond(withCreatedPin: pin!, challenge: createPinChallenge)
            } else {
                createPinChallenge.sender.cancel(createPinChallenge)
            }
        }
    }

    fileprivate func mapErrorFromPinChallenge(_ challenge: ONGPinChallenge) -> NSError? {
        if let error = challenge.error {
            return error as NSError
        } else {
            return nil
        }
    }

    fileprivate func mapErrorFromCreatePinChallenge(_ challenge: ONGCreatePinChallenge) -> NSError? {
        if let error = challenge.error {
            return error as NSError
        } else {
            return nil
        }
    }
}

extension PinHandler: ONGChangePinDelegate {
    func userClient(_ userClient: ONGUserClient, didReceive challenge: ONGPinChallenge) {
        authPinChallenge = challenge
        let pinError = mapErrorFromPinChallenge(challenge)
        handleFlowUpdate(PinFlow.authentication, error: pinError, receiver: self)
    }

    func userClient(_: ONGUserClient, didReceive challenge: ONGCreatePinChallenge) {
        authPinChallenge = nil
        createPinChallenge = challenge
        let pinError = mapErrorFromCreatePinChallenge(challenge)
        
        // Fix: dont close when we have an error to show
        if pinError == nil {
            closeFlow()
        }
        
        handleFlowUpdate(PinFlow.create, error: pinError, receiver: self)
    }
    
    func userClient(_: ONGUserClient, didFailToChangePinForUser _: ONGUserProfile, error: Error) {
        authPinChallenge = nil
        createPinChallenge = nil
        closeFlow()
        handleFlowUpdate(PinFlow.change, error: error as NSError, receiver: self)
    }
    
    func userClient(_ userClient: ONGUserClient, didStartPinChangeForUser userProfile: ONGUserProfile) {
    }

    func userClient(_: ONGUserClient, didChangePinForUser _: ONGUserProfile) {
        createPinChallenge = nil
        closeFlow()
        changePinCompletion!(true, nil)
        
        // Fix: dont update when this flow is done
//        handleFlowUpdate(PinFlow.change, nil, receiver: self)
    }
}
