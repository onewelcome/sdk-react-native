import AuthenticationServices

typealias RegisterUserPresenterProtocol = RegisterUserInteractorToPresenterProtocol & RegisterUserViewToPresenterProtocol & PinViewToPresenterProtocol

protocol RegisterUserInteractorToPresenterProtocol: AnyObject {
    func presentBrowserUserRegistrationView(registerUserEntity: RegisterUserEntity)
    func presentCreatePinView(registerUserEntity: RegisterUserEntity)
    func registerUserActionSuccess(authenticatedUserProfile: ONGUserProfile)
    func registerUserActionFailed(_ error: SdkError)
    func registerUserActionCancelled()
}

protocol RegisterUserViewToPresenterProtocol: AnyObject {
    var registerViewController: RegisterPresenterToViewProtocol? { get set }
    func signUp(_ identityProvider: ONGIdentityProvider?)
    func handleRedirectURL()
}

class RegisterUserPresenter: RegisterUserPresenterProtocol {
    var registerUserInteractor: RegisterUserInteractorProtocol
    var browserConntroller: BrowserViewControllerProtocol?
    weak var registerViewController: RegisterPresenterToViewProtocol?
    var pinViewController: ChangePinPresenterToViewProtocol?

    init(registerUserInteractor: RegisterUserInteractorProtocol) {
        self.registerUserInteractor = registerUserInteractor
    }

    func presentBrowserUserRegistrationView(registerUserEntity: RegisterUserEntity) {
      
        guard let registrationUserURL = registerUserEntity.registrationUserURL else { return }
      
        if(browserConntroller != nil) {
            browserConntroller?.handleUrl(url: registrationUserURL)
        } else {
            if #available(iOS 12.0, *) {
                browserConntroller = BrowserViewController(registerUserEntity: registerUserEntity, registerUserViewToPresenterProtocol: self)
                browserConntroller?.handleUrl(url: registrationUserURL)
            } else {
              // Fallback on earlier versions
            }
        }
    }

    func presentCreatePinView(registerUserEntity: RegisterUserEntity) {
        if(pinViewController == nil) {
            pinViewController = ChangePinHandler(entity: registerUserEntity, viewToPresenterProtocol: self)
        }
      
        if let error = registerUserEntity.pinError {
            pinViewController?.notifyOnError(error)
        } else {
            pinViewController?.openViewWithMode(.registration)
        }
    }
  
    func registerUserActionSuccess(authenticatedUserProfile: ONGUserProfile) {
        registerViewController?.onRegisterSuccess(authenticatedUserProfile)
        pinViewController?.closeView()
    }

    func registerUserActionFailed(_ error: SdkError) {
        registerViewController?.onRegisterFailed(error)
    }

    func registerUserActionCancelled() {
        registerViewController?.onRegisterCancelled()
        pinViewController?.closeView()
    }
}

extension RegisterUserPresenter {
    func signUp(_ identityProvider: ONGIdentityProvider? = nil) {
        registerUserInteractor.startUserRegistration(identityProvider: identityProvider)
    }

    func handleRedirectURL() {
        registerUserInteractor.handleRedirectURL()
    }
}

extension RegisterUserPresenter {
    func handlePin() {
        registerUserInteractor.handleCreatedPin()
    }
}
