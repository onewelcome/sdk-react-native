protocol MainConnectorFactory {
    var startClientConnector: StartClientConnector { get }
    var pinConnector: PinConnector { get }

    func registerUserConnector(with pinConnector: PinConnector) -> RegisterUserConnector
}

class DefaultMainConnectorFactory: MainConnectorFactory {
    var startClientConnector: StartClientConnector {
        return DefaultStartClientConnector()
    }

    var pinConnector: PinConnector {
        return DefaultPinConnector()
    }

    func registerUserConnector(with pinConnector: PinConnector) -> RegisterUserConnector {
        return DefaultRegisterUserConnector(pinConnector: pinConnector)
    }
}
