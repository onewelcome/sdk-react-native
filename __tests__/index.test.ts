import {NativeModules, Platform} from 'react-native';

import OneginiSdk, {DefaultConfig} from '../ts/index';

const {RNOneginiSdk} = NativeModules;

//

beforeEach(() => {
  jest.useFakeTimers();
});

afterEach(() => {
  jest.runOnlyPendingTimers();
  jest.useRealTimers();
  jest.clearAllMocks();
});

describe('OneginiSdk', () => {
  describe('startClient', () => {
    describe('on iOS', () => {
      it('should call native method with no parameters', () => {
        Platform.OS = 'ios';

        OneginiSdk.startClient();

        expect(RNOneginiSdk.startClient).toBeCalledTimes(1);
        expect(RNOneginiSdk.startClient).toBeCalledWith();
      });
    });

    describe('on Android', () => {
      it('should call native method with default parameters', () => {
        Platform.OS = 'android';

        OneginiSdk.startClient();

        expect(RNOneginiSdk.startClient).toBeCalledTimes(1);
        expect(RNOneginiSdk.startClient).toBeCalledWith(DefaultConfig);
      });
    });
  });

  // TODO: do the same with other overridden methods...
});
