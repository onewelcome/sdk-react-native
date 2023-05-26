# onewelcome-react-native-sdk

This is a wrapper for the OneWelcome native [Android](https://developer.onewelcome.com/android/sdk) and [iOS](https://developer.onewelcome.com/ios/sdk) SDK's to work with ReactNative. For more information please refer to our [documentation](https://developer.onewelcome.com/react-native/sdk/introduction)
For additional reference, see our [example application](https://github.com/onewelcome/example-app-react-native).


## Prerequisites

Refer to our [Requirements overview](https://developer.onewelcome.com/react-native/sdk/configuration#requirements)

## SDK Configuration

For setting up the SDK in your application, please refer to our [configuration docs](https://developer.onewelcome.com/react-native/sdk/configuration).


## Local Development of SDK
  
The [SDK](https://github.com/onewelcome/sdk-react-native) can be developed locally using the [example app](https://github.com/onewelcome/example-app-react-native). 
React Native does not support symlinking with dependencies in the node_modules folder because of the metro bundler see [issue](https://github.com/facebook/metro/issues/1).

In order to develop the sdk you can use an npm package called [yalc](https://www.npmjs.com/package/yalc)

This package allows us to set up a local npm repository to publish our SDK to.
To get started, install yalc following their instructions.

In the SDK folder run: `yalc publish`

In the example app folder run `yalc add @onewelcome/react-native-sdk`

After making changes to the SDK you can run `yalc publish --push`. This will automatically update the dependency in the example app aswell. 

For iOS there is an alternative. In your Podfile above `config = use_native_modules!` you can add the following code and run `pod install`. Note that this will only work for iOS files and not for the Typescript files.
```
pod 'onewelcome-react-native-sdk', :path => '../../onewelcome-react-native-sdk'
```



