import {NativeModules} from 'react-native';

NativeModules.RNOneginiSdk = {
  startClient: jest.fn(),
  resourceRequest: jest.fn(() => Promise.resolve()),
  authenticateDeviceForResource: jest.fn(() => Promise.resolve()),
  authenticateUserImplicitly: jest.fn(() => Promise.resolve()),
};
