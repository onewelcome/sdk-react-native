import {NativeModules} from 'react-native';

NativeModules.RNOneWelcomeSdk = {
  startClient: jest.fn(),
  resourceRequest: jest.fn(() => Promise.resolve()),
  authenticateDeviceForResource: jest.fn(() => Promise.resolve()),
  authenticateUserImplicitly: jest.fn(() => Promise.resolve()),
};
