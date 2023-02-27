class CreatePinEventEmitter {
    func onPinOpen(profileId: String, pinLength: UInt) {
        let data = [
            "flow": PinFlow.create.rawValue,
            "action": PinCreateNotification.open.rawValue,
            "profileId": profileId,
            "pinLength": pinLength
        ] as [String: Any]
        BridgeConnector.shared?.sendBridgeEvent(eventName: .pinCreateNotification, data: data)
    }

    func onPinClose() {
        let data = [
            "flow": PinFlow.create.rawValue,
            "action": PinCreateNotification.close.rawValue
        ]
        BridgeConnector.shared?.sendBridgeEvent(eventName: .pinCreateNotification, data: data)
    }

    func onPinNotAllowed(error: Error) {
        let data = [
            "flow": PinFlow.create.rawValue,
            "action": PinCreateNotification.pinNotAllowed.rawValue,
            "errorMsg": error.localizedDescription,
            "errorType": error.code
        ] as [String: Any]
        BridgeConnector.shared?.sendBridgeEvent(eventName: .pinCreateNotification, data: data)
    }

}
