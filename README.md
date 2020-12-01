# react-native-sdk-beta

## Getting started

`npm install react-native-sdk-beta --save`

OR

`yarn add react-native-sdk-beta`

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
	dependencies {
        	classpath("com.android.tools.build:gradle:4.1.1")
        	classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
    	}

	```

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
		throw new InvalidUserDataException("You must configure the 'onegini.artifactory_user' and 'onegini.artifactory_password' properties in your project before you can " +
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
#### iOS: 

1. The Onegini SDK is uploaded to the Onegini Artifactory repository. In order to let CocoaPods use an Artifactory repository you need to install a specific plugin.
	```
	gem install cocoapods-art
	```
2. The Onegini SDK repository is not a public repository. You must provide credentials in order to access the repo. Create a file named .netrc in your Home folder (~/) and add the following contents to it:
	```
	machine repo.onegini.com
	login <username>
	password <password>
	```
	Replace the <username> and <password> with the credentials that you use to login to support.onegini.com.

3. The Onegini CocoaPods repository must be added to your local machine using the following command:
	```
	pod repo-art add onegini https://repo.onegini.com/artifactory/api/pods/cocoapods-public
	```

4. In order to update the Repository you must manually perform an update:
	```
	pod repo-art add onegini https://repo.onegini.com/artifactory/api/pods/cocoapods-public
	```

5. Add next to `ios/Podfile`(before app target):
	```
	plugin 'cocoapods-art', :sources => [
	'onegini'
	]
	```
6. Add `SecurityController.h` and `SecurityController.m` as described [HERE](https://docs.onegini.com/msp/stable/ios-sdk/reference/security-controls.html)

## Linking Native Code

### RN >= 60.0

`cd ios && pod install`

### RN < 60.0

##### Auto linking

`react-native link react-native-sdk-beta`

##### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-sdk-beta` and add `RNOneginiSdk.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNOneginiSdk.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.onegini.mobile.RNOneginiSdkPackage;` to the imports at the top of the file
  - Add `new RNOneginiSdkPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-sdk-beta'
  	project(':react-native-sdk-beta').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-sdk-beta/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-sdk-beta')
  	```

# Functional scope
## Done on the Android:
### Milestone 1:
	- Start
  	- Security Controls and Configuration of the SDK
  	- User registration
   	   - Browser
### Milestone 2:
  	- User registration
    	   - Custom
  	- User deregistration
### Milestone 3:
  	- User authentication with PIN
  	- Fetch user access token
  	- Logout
### Milestone 6:
  	- Change PIN

# Usage
- import OneginiSdk from 'react-native-sdk-beta';

## Supported Methods

    startClient():Promise

    addEventListener(eventType)

    removeEventListener(eventType)

    getIdentityProviders():Promise

    getAccessToken():Promise

    enrollMobileAuthentication():Promise

    registerUser(response)

    deregisterUser(profileId):Promise

    getRedirectUri(response)

    getUserProfiles():Promise

    setConfigModelClassName(className);

    setSecurityControllerClassName(className);

    handleRegistrationCallback(uri);

    cancelRegistration();

    submitCustomRegistrationReturnSuccess(identityProviderId, result);

    submitCustomRegistrationReturnError(identityProviderId, result);

    submitPinAction(flow, action, pin);

    submitCreatePinAction(action, pin)

    submitChangePinAction(action, pin);

    submitAuthenticationPinAction(action, pin)

    authenticateUser(profileId):Promise

    logout():Promise
  