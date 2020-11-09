protocol ChangePinInteractorProtocol: AnyObject {
    func changePin()
    func handlePin()
}

class ChangePinInteractor: NSObject {
    weak var changePinPresenter: ChangePinInteractorToPresenterProtocol?
    var changePinEntity = ChangePinEntity()

    fileprivate func mapErrorFromPinChallenge(_ challenge: ONGPinChallenge) {
        if let error = challenge.error {
            changePinEntity.pinError = ErrorMapper().mapError(error, pinChallenge: challenge)
        } else {
            changePinEntity.pinError = nil
        }
    }

    fileprivate func mapErrorFromCreatePinChallenge(_ challenge: ONGCreatePinChallenge) {
        if let error = challenge.error {
            changePinEntity.pinError = ErrorMapper().mapError(error)
        } else {
            changePinEntity.pinError = nil
        }
    }

    func handlePin() {
        if changePinEntity.createPinChallenge != nil {
            handleCreatePin()
        }
    }

    func handleCreatePin() {
        guard let pinChallenge = changePinEntity.createPinChallenge else { return }
        if let pin = changePinEntity.pin {
            pinChallenge.sender.respond(withCreatedPin: pin, challenge: pinChallenge)
        } else {
            pinChallenge.sender.cancel(pinChallenge)
        }
    }
}

extension ChangePinInteractor: ChangePinInteractorProtocol {
    func changePin() {
        ONGUserClient.sharedInstance().changePin(self)
    }
}

extension ChangePinInteractor: ONGChangePinDelegate {
    func userClient(_ userClient: ONGUserClient, didReceive challenge: ONGPinChallenge) {
//        changePinEntity.loginPinChallenge = challenge
//        changePinEntity.pinLength = 5
//        mapErrorFromPinChallenge(challenge)
//        changePinPresenter?.presentLoginPinView(changePinEntity: changePinEntity)
      
      //@todo will need this in the future
    }
  
    func userClient(_: ONGUserClient, didReceive challenge: ONGCreatePinChallenge) {
        changePinEntity.createPinChallenge = challenge
        changePinEntity.pinLength = Int(challenge.pinLength)
        mapErrorFromCreatePinChallenge(challenge)
        changePinPresenter?.presentCreatePinView(changePinEntity: changePinEntity)
    }

    func userClient(_: ONGUserClient, didFailToChangePinForUser _: ONGUserProfile, error: Error) {
        changePinEntity.createPinChallenge = nil
        let mappedError = ErrorMapper().mapError(error)
        if error.code == ONGGenericError.actionCancelled.rawValue {
            changePinPresenter?.presentProfileView()
        } else if error.code == ONGGenericError.userDeregistered.rawValue {
            changePinPresenter?.popToWelcomeViewWithError(mappedError)
        } else {
            changePinPresenter?.changePinActionFailed(mappedError)
        }
    }

    func userClient(_: ONGUserClient, didChangePinForUser _: ONGUserProfile) {
        changePinEntity.createPinChallenge = nil
        changePinPresenter?.presentProfileView()
    }
}
