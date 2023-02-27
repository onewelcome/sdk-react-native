class PinAuthenticationEventEmitter {
    func onPinOpen(profileId: String) {
        let data = [
            "flow": PinFlow.authentication.rawValue,
            "action": PinAuthNotification.open.rawValue,
            "profileId": profileId
        ]
        BridgeConnector.shared?.sendBridgeEvent(eventName: .pinAuthNotification, data: data)
    }

    func onPinClose() {
        let data = [
            "flow": PinFlow.authentication.rawValue,
            "action": PinAuthNotification.close.rawValue
        ]
        BridgeConnector.shared?.sendBridgeEvent(eventName: .pinAuthNotification, data: data)
    }

    func onIncorrectPin(error: Error, remainingFailureCount: UInt) {
        let data = [
            "flow": PinFlow.authentication.rawValue,
            "action": PinAuthNotification.incorrectPin.rawValue,
            "errorType": error.code,
            "errorMsg": error.localizedDescription,
            "remainingFailureCount": remainingFailureCount
        ] as [String: Any]
        BridgeConnector.shared?.sendBridgeEvent(eventName: .pinAuthNotification, data: data)
    }
}
