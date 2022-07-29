protocol PinConnectorToPinHandler: AnyObject {
    func onPinProvided(pin: (NSString))
    func onChangePinCalled(completion: @escaping (Bool, NSError?) -> Void)
    func onCancel()
    func handleFlowUpdate(_ flow: PinFlow, error: NSError?, receiver: PinHandlerToReceiverProtocol, profileId: String, userInfo: [String: Any]?, data: Any?)
    func closeFlow()
}

protocol PinHandlerToReceiverProtocol: AnyObject {
    func handlePin(pin: String?)
}

enum PINEntryMode {
    case login
    case registration
    
}

class PinHandler: NSObject {
    var authPinChallenge: ONGPinChallenge?
    var createPinChallenge: ONGCreatePinChallenge?
    var flow: PinFlow?
    var mode: PINEntryMode?
    var changePinCompletion: ((Bool, NSError?) -> Void)?
    unowned var pinReceiver: PinHandlerToReceiverProtocol?

    func processPin(pinEntry: Array<String>) {
        let pincode = pinEntry.joined()
        switch mode {
          case .registration:
              handleRegistrationPin(pincode)
          case .login:
                pinReceiver?.handlePin(pin: pincode)
          case .none:
            let error = NSError(domain: ONGPinValidationErrorDomain, code: 0, userInfo: [NSLocalizedDescriptionKey : "Unexpected PIN mode."])
              notifyOnError(error)
        }
    }

    private func processCancelAction() {
        mode = nil
        pinReceiver?.handlePin(pin: nil)
    }

    private func handleRegistrationPin(_ pincode: String) {
        pinReceiver?.handlePin(pin: pincode)
    }

    func notifyOnError(_ error: NSError, userInfo: [String: Any]? = nil) {
        sendConnectorNotification(PinNotification.showError, flow, error, nil, userInfo, nil)
    }

    private func sendConnectorNotification(_ event: PinNotification, _ flow: PinFlow?, _ error: NSError?,  _ profileId: String?, _ userInfo: [String: Any]? = nil, _ data: Any?) {
        BridgeConnector.shared?.toPinHandlerConnector.sendNotification(event: event, flow: flow, error: error, profileId: profileId, userInfo: userInfo, data: data)
    }
}

extension PinHandler : PinConnectorToPinHandler {
    // @todo Support different pinLength
    func handleFlowUpdate(_ flow: PinFlow, error: NSError?, receiver: PinHandlerToReceiverProtocol, profileId: String, userInfo:[String: Any]? = nil, data: Any? = nil) {
        if(self.flow == nil){
            self.flow = flow
            pinReceiver = receiver
        }
        if(error != nil){
            notifyOnError(error!, userInfo: userInfo)
            return
        }
        if(mode == nil) {
            switch flow {
                case PinFlow.authentication:
                    mode = .login
                case PinFlow.create:
                    mode = .registration
                case PinFlow.change:
                    mode = .login
            }
            sendConnectorNotification(PinNotification.open, flow, nil, profileId, userInfo, data)
        }
    
    }

    func closeFlow() {
        if(flow != nil){
            mode = nil
            flow = nil
            sendConnectorNotification(PinNotification.close, flow, nil, nil, nil, nil)
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
        handleFlowUpdate(PinFlow.authentication, error: pinError, receiver: self, profileId: challenge.userProfile.profileId, userInfo: challenge.userInfo)
    }

    func userClient(_: ONGUserClient, didReceive challenge: ONGCreatePinChallenge) {
        authPinChallenge = nil
        createPinChallenge = challenge
        let pinError = mapErrorFromCreatePinChallenge(challenge)
        
        // Fix: dont close when we have an error to show
        if pinError == nil {
            closeFlow()
        }
        
        handleFlowUpdate(PinFlow.create, error: pinError, receiver: self, profileId: challenge.userProfile.profileId, userInfo: pinError?.userInfo, data: challenge.pinLength)
    }
    
    func userClient(_: ONGUserClient, didFailToChangePinForUser profile: ONGUserProfile, error: Error) {
        authPinChallenge = nil
        createPinChallenge = nil
        closeFlow()
        changePinCompletion!(false, error as NSError)
    }
    
    func userClient(_ userClient: ONGUserClient, didStartPinChangeForUser userProfile: ONGUserProfile) {
        //TODO: Notify react-native that we are starting a pinChange.
    }

    func userClient(_ : ONGUserClient, didChangePinForUser _: ONGUserProfile) {
        createPinChallenge = nil
        closeFlow()
        changePinCompletion!(true, nil)
    }
}
