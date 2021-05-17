protocol MainConnectorFactory {
    var startClientConnector: StartClientConnector { get }
}

class DefaultMainConnectorFactory: MainConnectorFactory {
    var startClientConnector: StartClientConnector {
        return DefaultStartClientConnector()
    }
}
