class CreatePinEventEmitter {
    func onPinOpen(profileId: String, pinLength: UInt) {
        let data = [
            "flow": PinFlow.create.rawValue,
            "action": PinNotification.open.rawValue,
            "profileId": profileId,
            "data": pinLength //TODO: Change this to pinLength
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
    
    func onPinError(error: Error) {
        let data = [
            "flow": PinFlow.create.rawValue,
            "action": PinNotification.showError.rawValue,
            "errorMsg": error.localizedDescription
        ]
        BridgeConnector.shared?.sendBridgeEvent(eventName: .pinNotification, data: data)
    }
}
