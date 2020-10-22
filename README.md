
# react-native-onegini-sdk

## Getting started

`$ npm install react-native-onegini-sdk --save`

### Mostly automatic installation

`$ react-native link react-native-onegini-sdk`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-onegini-sdk` and add `RNOneginiSdk.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNOneginiSdk.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.onegini.mobile.RNOneginiSdkPackage;` to the imports at the top of the file
  - Add `new RNOneginiSdkPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-onegini-sdk'
  	project(':react-native-onegini-sdk').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-onegini-sdk/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-onegini-sdk')
  	```


## Usage
```javascript
import RNOneginiSdk from 'react-native-onegini-sdk';

// TODO: What to do with the module?
RNOneginiSdk;
```
  