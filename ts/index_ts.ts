// TODO: rename as index.ts (main index)
// TODO: separate modules?

import {
  NativeModules,
  Platform,
  NativeEventEmitter,
  DeviceEventEmitter,
} from 'react-native';
const {RNOneginiSdk} = NativeModules;

// helpers
const isIOS = Platform.OS === 'ios';
const isAndroid = Platform.OS === 'android';

//

namespace OneginiSdkTs {
  //
  // Start
  //

  export interface CustomProvider {
    id: string;
    isTwoStep: boolean;
  }

  export interface Config {
    enableFingerprint: boolean;
    securityControllerClassName: string;
    enableMobileAuthenticationOtp: boolean;
    configModelClassName: string | null;
    customProviders: [CustomProvider?];
  }

  const DefaultConfig: Config = {
    enableFingerprint: true,
    securityControllerClassName: 'com.rnexampleapp.SecurityController',
    enableMobileAuthenticationOtp: true,
    customProviders: [{id: '2-way-otp-api', isTwoStep: true}],
    configModelClassName: null,
  };

  // Promise returns error msg?
  export function startClient(
    sdkConfig: Config = DefaultConfig,
  ): Promise<string | null> {
    return isIOS
      ? RNOneginiSdk.startClient() // why there is no default config on iOS?
      : RNOneginiSdk.startClient(sdkConfig);
  }

  //
  //
  //
}

export default OneginiSdkTs;
