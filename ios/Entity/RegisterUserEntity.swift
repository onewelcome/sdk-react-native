class RegisterUserEntity: BrowserViewControllerEntityProtocol, PinViewControllerEntityProtocol {
    var registrationUserURL: URL?
    var browserRegistrationChallenge: ONGBrowserRegistrationChallenge?
    var createPinChallenge: ONGCreatePinChallenge?
    var pin: String?
    var pinError: SdkError?
    var pinLength: Int?
    var redirectURL: URL?
    
    var responseCode: String?
    var challengeCode: String?
    var errorMessage: String?
    var cancelled: Bool = false
}
