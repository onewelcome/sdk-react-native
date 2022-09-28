class PinAuthenticationEventEmitter {
    func onPinOpen(profileId: String) {
        let data = [
            "flow": PinFlow.authentication.rawValue,
            "action": PinNotification.open.rawValue,
            "profileId": profileId,
        ]
        BridgeConnector.shared?.sendBridgeEvent(eventName: OneWelcomeBridgeEvents.pinNotification, data: data)
    }
    
    func onPinClose() {
        let data = [
            "flow": PinFlow.authentication.rawValue,
            "action": PinNotification.close.rawValue,
        ]
        BridgeConnector.shared?.sendBridgeEvent(eventName: OneWelcomeBridgeEvents.pinNotification, data: data)
    }
 
    func onIncorrectPin(error: Error, remainingFailureCount: UInt) {
        let data = [
            "flow": PinFlow.authentication.rawValue,
            "action": PinNotification.incorrectPin.rawValue,
            "errorType": error.code,
            "errorMsg": error.localizedDescription,
            "remainingFailureCount": remainingFailureCount,
        ] as [String: Any]
        BridgeConnector.shared?.sendBridgeEvent(eventName: OneWelcomeBridgeEvents.pinNotification, data: data)
    }
}
