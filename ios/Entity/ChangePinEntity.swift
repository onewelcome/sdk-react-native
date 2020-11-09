class ChangePinEntity: PinViewControllerEntityProtocol {
    var pin: String?
    var pinError: SdkError?
    var pinLength: Int?
    var createPinChallenge: ONGCreatePinChallenge?
}
