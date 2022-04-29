import {NativeModules, Platform} from 'react-native';

import OnewelcomeSdk, {DefaultConfig} from '../ts/index';

const {RNOnewelcomeSdk} = NativeModules;

//

beforeEach(() => {
  jest.useFakeTimers();
});

afterEach(() => {
  jest.runOnlyPendingTimers();
  jest.useRealTimers();
  jest.clearAllMocks();
});

describe('OnewelcomeSdk', () => {
  describe('startClient', () => {
    describe('on iOS', () => {
      it('should call native method with no parameters', () => {
        Platform.OS = 'ios';

        OnewelcomeSdk.startClient();

        expect(RNOnewelcomeSdk.startClient).toBeCalledTimes(1);
        expect(RNOnewelcomeSdk.startClient).toBeCalledWith();
      });
    });

    describe('on Android', () => {
      it('should call native method with default parameters', () => {
        Platform.OS = 'android';

        OnewelcomeSdk.startClient();

        expect(RNOnewelcomeSdk.startClient).toBeCalledTimes(1);
        expect(RNOnewelcomeSdk.startClient).toBeCalledWith(DefaultConfig);
      });
    });
  });

  // TODO: do the same with other overridden methods...
});
