protocol ChangePinConnectorToViewProtocol: AnyObject {
    func onPinProvided(pin: (NSString))
    func onCancel()
}

protocol ChangePinPresenterToViewProtocol: AnyObject {
    func openViewWithMode(_ mode: PINEntryMode)
    func notifyOnError(_ error: SdkError)
    func closeView()
}

protocol PinViewControllerEntityProtocol {
    var pin: String? { get set }
    var pinError: SdkError? { get }
    var pinLength: Int? { get }
}

protocol PinViewToPresenterProtocol: class {
    func handlePin()
}

enum PINEntryMode {
    case login
    case registration
    case registrationConfirm
}

class ChangePinHandler {
    var mode: PINEntryMode?
    var pinEntryToVerify = Array<String>()
    var entity: PinViewControllerEntityProtocol
    unowned let viewToPresenterProtocol: PinViewToPresenterProtocol

    init(entity: PinViewControllerEntityProtocol, viewToPresenterProtocol: PinViewToPresenterProtocol) {
        self.entity = entity
        self.viewToPresenterProtocol = viewToPresenterProtocol
        BridgeConnector.shared?.toChangePinConnector.pinView = self
    }

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
                //@todo will need this at MVP stage
                //entity.pin = pincode
                //viewToPresenterProtocol.handlePin()
              break
          case .none:
              self.notifyOnError(SdkError(title: "Pin validation error", errorDescription: "Unexpected PIN mode.", recoverySuggestion: "Open and close modal."))
              break
        }
    }
  
    private func processCancelAction() {
        mode = nil
        entity.pin = nil
        viewToPresenterProtocol.handlePin()
    }
  
    private func handleRegistrationPin(_ pinEntry: Array<String>) {
        pinEntryToVerify = pinEntry
        mode = .registrationConfirm
        sendConnectorNotification(PinNotification.confirm, mode, nil)
    }
    
    private func handleConfirmRegistrationPin(_ pinEntry: Array<String>, _ pincode: String) {
        let pincodeConfirm = pinEntryToVerify.joined()
        if pincode == pincodeConfirm {
            entity.pin = pincode
            viewToPresenterProtocol.handlePin()
        } else {
            mode = .registration
            self.notifyOnError(SdkError(title: "Pin validation error", errorDescription: "The confirmation PIN does not match.", recoverySuggestion: "Try a different one."))
        }
    }
  
  private func sendConnectorNotification(_ event: PinNotification, _ mode: PINEntryMode?, _ error: SdkError?) {
    BridgeConnector.shared?.toChangePinConnector.sendNotification(event: event, mode: mode, error: error)
  }
}

extension ChangePinHandler : ChangePinPresenterToViewProtocol {
    func openViewWithMode(_ mode: PINEntryMode) {
        if(mode == .registration) {
            self.mode = mode;
            sendConnectorNotification(PinNotification.open, mode, nil)
        }
    }
    
    func notifyOnError(_ error: SdkError) {
        sendConnectorNotification(PinNotification.showError, mode, error)
      
        if(mode == PINEntryMode.registrationConfirm) {
            entity.pin = nil
            mode = .registration
        }
    }
    
    func closeView() {
        sendConnectorNotification(PinNotification.close, mode, nil)
    }
 }

extension ChangePinHandler : ChangePinConnectorToViewProtocol {
  func onPinProvided(pin: (NSString)) {
    let characters: String = pin as String
    let pinArray: Array<String> = Array(arrayLiteral: characters)
    
    processPin(pinEntry: pinArray)
  }
  
  func onCancel() {
      processCancelAction()
  }
}
