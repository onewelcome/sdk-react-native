typealias ChangePinPresenterProtocol = ChangePinInteractorToPresenterProtocol

protocol ChangePinInteractorToPresenterProtocol: AnyObject {
    func startChangePinFlow()
    func presentCreatePinView(changePinEntity: ChangePinEntity)
    func presentProfileView()
    func changePinActionFailed(_ error: SdkError)
    func popToWelcomeViewWithError(_ error: SdkError)
}

class ChangePinPresenter: ChangePinInteractorToPresenterProtocol {
    let changePinInteractor: ChangePinInteractorProtocol
    var pinViewController: ChangePinPresenterToViewProtocol?

    init(changePinInteractor: ChangePinInteractorProtocol) {
        self.changePinInteractor = changePinInteractor
    }

    func startChangePinFlow() {
        //@todo will need this at MVP phase
        //changePinInteractor.changePin()
    }

    func presentCreatePinView(changePinEntity: ChangePinEntity) {
        if(pinViewController == nil) {
          pinViewController = ChangePinHandler(entity: changePinEntity, viewToPresenterProtocol: self)
        }
      
        if let error = changePinEntity.pinError {
          pinViewController?.notifyOnError(error)
        } else {
          pinViewController?.openViewWithMode(.registration)
        }
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
}

extension ChangePinPresenter: PinViewToPresenterProtocol {
    func handlePin() {
        changePinInteractor.handlePin()
    }
}
