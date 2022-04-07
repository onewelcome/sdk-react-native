import {NativeModules, Platform} from 'react-native';

import OneginiSdk, {
  DefaultConfig,
  useResources,
  Types,
} from '../ts/index';

import {renderHook} from '@testing-library/react-hooks/native';

const {RNOneginiSdk} = NativeModules;

// //

beforeEach(() => {
  jest.useFakeTimers();
});

afterEach(() => {
  jest.runOnlyPendingTimers();
  jest.useRealTimers();
  jest.clearAllMocks();
});

describe('useResources', () => {
  describe('when called without authorization', () => {
    const testRender = () =>
      renderHook(() =>
          useResources(
              Types.ResourceRequestType.Anonymous,
              {
                method: 'GET',
                parameters: {'custom-param1': 'p1', 'custom-param2': 'p2'},
                encoding: 'application/json',
                headers: {'custom-header1': 'val1', 'custom-header2': 'val2'},
                path: 'test'
              },
              false,
              ['read'],
          ),
      );

    it('should return proper loading state', async () => {
      const {result, waitForNextUpdate} = testRender();

      // loading at start
      expect(result.current.loading).toBe(true);

      // let it fetch data
      await waitForNextUpdate();

      // after fetch loading is done
      expect(result.current.loading).toBe(false);
    });

    it('should call native resourceRequest with params', async () => {
      const {waitForNextUpdate} = testRender();

      await waitForNextUpdate();

      expect(RNOneginiSdk.resourceRequest).toBeCalledTimes(1);
      expect(RNOneginiSdk.resourceRequest).toBeCalledWith(
        Types.ResourceRequestType.Anonymous,
        {
          method: 'GET',
          parameters: {'custom-param1': 'p1', 'custom-param2': 'p2'},
          encoding: 'application/json',
          headers: {'custom-header1': 'val1', 'custom-header2': 'val2'},
          path: 'test'
        },
      );
    });

    it('should not call native authorization methods', async () => {
      const {waitForNextUpdate} = testRender();

      await waitForNextUpdate();

      expect(RNOneginiSdk.authenticateUserImplicitly).not.toBeCalled();
      expect(RNOneginiSdk.authenticateDeviceForResource).not.toBeCalled();
    });
  });

  // TODO: other scenarios...
});
