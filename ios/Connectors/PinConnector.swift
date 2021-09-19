//
//  PinConnector.swift
//  onegini-react-native-sdk
//
//  Created by Jan Lipmann on 06/09/2021.
//

import Foundation

protocol PinConnector {
    var sendEventHandler: ((Event) -> Void)? { get set }
    
    func handleFlowUpdate(_ flow: PinFlow, _ error: NSError?, receiver: PinHandlerToReceiverProtocol)
    func handlePinAction(_ flow: (NSString), _ action: (NSString), _ pin: (NSString))
    func closeFlow()
}

final class DefaultPinConnector: PinConnector {
    var flow: PinFlow?
    var mode: PINEntryMode?
    var pinEntryToVerify: String?

    unowned var pinReceiver: PinHandlerToReceiverProtocol?


    var sendEventHandler: ((Event) -> Void)?

    func handleFlowUpdate(_ flow: PinFlow, _ error: NSError?, receiver: PinHandlerToReceiverProtocol) {
        self.flow = flow
        self.pinReceiver = receiver

        if let error = error {
            notifyOnError(error)
            return
        }

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

            sendNotification(event: PinNotification.open, flow: flow, error: nil)
        }
    }

    func notifyOnError(_ error: NSError) {
        sendNotification(event: PinNotification.showError, flow: flow, error: error)

        if(mode == .registrationConfirm) {
            mode = .registration
        }
    }

    func sendNotification(event: PinNotification, flow: PinFlow?, error: NSError?) {
        switch (event){
            case .open:
                sendEvent(data: ["flow": flow?.rawValue ?? "", "action": PinNotification.open.rawValue])
            case .confirm:
                sendEvent(data: ["flow": flow?.rawValue ?? "", "action": PinNotification.confirm.rawValue])
            case .close:
                sendEvent(data: ["flow": flow?.rawValue ?? "", "action": PinNotification.close.rawValue])
            case .showError:
                sendEvent(data: ["flow": flow?.rawValue ?? "", "action": PinNotification.showError.rawValue, "errorMsg": error?.localizedDescription ?? ""])
        }
    }

    func handlePinAction(_ flow: (NSString), _ action: (NSString), _ pin: (NSString)) {
        switch action {
            case PinAction.provide.rawValue:
                processPin(pin: pin)
            case PinAction.cancel.rawValue:
                processCancelAction()
            default:
                sendEvent(data: ["flow": flow, "action": PinNotification.showError.rawValue, "errorMsg": "Unsupported pin action. Contact SDK maintainer."])
        }
    }

    func closeFlow() {
        if(flow != nil){
            mode = nil
            flow = nil
            sendNotification(event: PinNotification.close, flow: flow, error: nil)
        }
    }

    private func sendEvent(data: [String: Any]?) {
        let event = GenericEvent(name: OneginiBridgeEvents.pinNotification.rawValue, data: data)
        sendEventHandler?(event)
    }

    private func processPin(pin: NSString) {
        let pincode: String = pin as String
        switch mode {
          case .registration:
            handleRegistrationPin(pincode)
          case .registrationConfirm:
            handleConfirmRegistrationPin(pincode)
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

    private func handleRegistrationPin(_ pinCode: String) {
        pinEntryToVerify = pinCode
        mode = .registrationConfirm
        sendNotification(event: PinNotification.confirm, flow: nil, error: nil)
    }

    private func handleConfirmRegistrationPin(_ pincode: String) {
        if let pincodeConfirm = pinEntryToVerify, pincode == pincodeConfirm {
            pinReceiver?.handlePin(pin: pincode)
        } else {
            mode = .registration
            let error = NSError(domain: ONGPinValidationErrorDomain, code: 0, userInfo: [NSLocalizedDescriptionKey : "The confirmation PIN does not match."])
            notifyOnError(error)
        }
    }
}

extension PINEntryMode {
    init?(with flow: PinFlow) {
        switch flow {
            case PinFlow.authentication:
                self = .login
            case PinFlow.create:
                self = .registration
            default:
                self = .registration
        }
    }
}
