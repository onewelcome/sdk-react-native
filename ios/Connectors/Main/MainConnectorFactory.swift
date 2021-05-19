protocol MainConnectorFactory {
    var startClientConnector: StartClientConnector { get }
    var registerUserConnector: RegisterUserConnector { get }
}

class DefaultMainConnectorFactory: MainConnectorFactory {
    var startClientConnector: StartClientConnector {
        return DefaultStartClientConnector()
    }
    
    var registerUserConnector: RegisterUserConnector {
        return DefaultRegisterUserConnector()
    }
    
}
