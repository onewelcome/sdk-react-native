# react-native-onegini-sdk

## Getting started

`npm install react-native-onegini-sdk --save`

OR

`yarn add react-native-onegini-sdk`

## SDK Configuration

1. Get access to https://repo.onegini.com/artifactory/onegini-sdk
2. Use https://github.com/Onegini/onegini-sdk-configurator on your application (instructions can be found there)

## App Configuration

#### Android: 

1. Modify `android/app/build.gradle`:

	1.1. Add to `android` section:

  	```
    lintOptions {
        abortOnError false
    }
  	```

	1.2 Add to `android` -> `defaultConfig` section:
	
	```
	minSdkVersion 19
	multiDexEnabled true
	```

	1.3 Add to `dependencies` section:

	```
	implementation 'androidx.multidex:multidex:2.0.1'
	```

2. Add to `android/app/proguard-rules.pro`:
   	```
    -keep class com.onegini.mobile.SecurityController { *; }
  	```

3. Add to `android/build.gradle`[allprojects.repositories]: 

	```
	mavenCentral()
	if (project.hasProperty('onegini.artifactory_user') && project.hasProperty('onegini.artifactory_password')) {
		maven {
			/*
			Before the release please change the url below to: https://repo.onegini.com/artifactory/onegini-sdk
			Please change it back to https://repo.onegini.com/artifactory/public after the release
			*/
			url "https://repo.onegini.com/artifactory/onegini-sdk"
			credentials {
				username "${onegini_artifactory_user}"
				password "${onegini_artifactory_password}"
			}
		}
	} else {
		throw new InvalidUserDataException("You must configure the 'artifactory_user' and 'artifactory_password' properties in your project before you can " +
				"build it.")
	}
	```
4. Set **onegini_artifactory_user** and **onegini_artifactory_password** at `android/gradle.properties` or globaly for gradle

5. Modify `android/app/src/main/AndroidManifest.xml`. Add `<intent-filter>` to your .MainActivity for listening browser redirects. !!! scheme="reactnativeexample" should be changed to your(will be provided by onegini-sdk-configurator) schema:
	```
	<intent-filter>
		<action android:name="android.intent.action.VIEW" />

		<category android:name="android.intent.category.DEFAULT"/>
		<category android:name="android.intent.category.BROWSABLE"/>

		<data android:scheme="reactnativeexample"/>
	</intent-filter>
	```
		

## Linking Native Code

### RN >= 60.0

`cd ios && pod install`

### RN < 60.0

##### Auto linking

`react-native link react-native-onegini-sdk`

##### Manual installation


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
  