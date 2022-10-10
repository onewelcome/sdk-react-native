class RegistrationEventEmitter {
    func onSendUrl(_ url: URL) {
        let data = [
            "url": url.absoluteString,
        ]
        BridgeConnector.shared?.sendBridgeEvent(eventName: .registrationNotification, data: data)
    }
}
