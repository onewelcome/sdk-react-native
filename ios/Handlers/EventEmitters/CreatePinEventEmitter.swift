class CreatePinEventEmitter {
    func onPinOpen(profileId: String, pinLength: UInt) {
        let data = [
            "flow": PinFlow.create.rawValue,
            "action": PinNotification.open.rawValue,
            "profileId": profileId,
            "pinLength": pinLength,
        ] as [String: Any]
        BridgeConnector.shared?.sendBridgeEvent(eventName: .pinNotification, data: data)
    }
    
    func onPinClose() {
        let data = [
            "flow": PinFlow.create.rawValue,
            "action": PinNotification.close.rawValue,
        ]
        BridgeConnector.shared?.sendBridgeEvent(eventName: .pinNotification, data: data)
    }
    
    func onPinNotAllowed(error: Error) {
        let data = [
            "flow": PinFlow.create.rawValue,
            "action": PinNotification.pinNotAllowed.rawValue,
            "errorMsg": error.localizedDescription,
            "errorType": error.code,
        ] as [String: Any]
        BridgeConnector.shared?.sendBridgeEvent(eventName: .pinNotification, data: data)
    }
    
}
