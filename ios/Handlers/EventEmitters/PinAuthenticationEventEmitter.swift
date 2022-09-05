class PinAuthenticationEventEmitter: NSObject {
    func onPinOpen(profileId: String) {
        let data = [
            "flow": PinFlow.Authentication.rawValue,
            "action": PinNotification.open.rawValue,
            "profileId": profileId,
        ]
        BridgeConnector.shared?.sendBridgeEvent(eventName: OneWelcomeBridgeEvents.pinNotification, data: data)
    }
    
    func onPinClose() {
        let data = [
            "flow": PinFlow.Authentication.rawValue,
            "action": PinNotification.close.rawValue,
        ]
        BridgeConnector.shared?.sendBridgeEvent(eventName: OneWelcomeBridgeEvents.pinNotification, data: data)
    }
    
    func onPinError(error: NSError) {
        let data = [
            "flow": PinFlow.Authentication.rawValue,
            "action": PinNotification.showError.rawValue,
            "errorType": error.code,
            "errorMsg": error.localizedDescription
        ] as [String: Any]
        BridgeConnector.shared?.sendBridgeEvent(eventName: OneWelcomeBridgeEvents.pinNotification, data: data)
    }

    // TODO: We probably want to rewrite this at some point. 
    func onWrongPin(error: NSError, remainingFailureCount: UInt) {
        let data = [
            "flow": PinFlow.Authentication.rawValue,
            "action": PinNotification.showError.rawValue,
            "errorType": error.code,
            "errorMsg": error.localizedDescription,
            "userInfo": [
                "remainingFailureCount": remainingFailureCount
            ]
        ] as [String: Any]
        BridgeConnector.shared?.sendBridgeEvent(eventName: OneWelcomeBridgeEvents.pinNotification, data: data)
    }
}
