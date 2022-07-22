import {NativeModules} from 'react-native';
import mockAsyncStorage from '@react-native-async-storage/async-storage/jest/async-storage-mock';

jest.mock('@react-native-async-storage/async-storage', () => mockAsyncStorage);

NativeModules.RNOneginiSdk = {
  startClient: jest.fn(),
  resourceRequest: jest.fn(() => Promise.resolve()),
  authenticateDeviceForResource: jest.fn(() => Promise.resolve()),
  authenticateUserImplicitly: jest.fn(() => Promise.resolve()),
};
