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
    if (project.hasProperty('onegini_artifactory_user') && project.hasProperty('onegini_artifactory_password')) {
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
        throw new InvalidUserDataException("You must configure the 'onegini_artifactory_user' and 'onegini_artifactory_password' properties in your project before you can " +
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

6. <a name="android-setup-config"/>Setup the Onegini config: Generate a 'OneginiConfigModel' and 'keystore.bks' with [SDK Configurator](https://github.com/Onegini/onegini-sdk-configurator#android). 
    
    Configurator will put `OneginiConfigModel` into `[RN_application_package_classpath.OneginiConfigModel]` (e.g. `com.exampleapp.OneginiConfigModel`) and the `keystore.bks` into '/res/raw'. 
    After configurator used - you have 2 options:
    - Keep it as it is.
    - If there is a need to move `OneginiConfigModel` to another place - it's **required** to specify custom classpath for OneginiSdk: [Supported Methods:](#supported-methods) setConfigModelClassName(className).
    
    More information [HERE](https://docs.onegini.com/msp/stable/android-sdk/topics/setting-up-the-project.html#verifying), section: Running the SDK Configurator.

7. <a name="android-setup-security-controller"/>Setup the SecurityController(<u>not required</u>).
    
    In order to change security options you should create your own instance SecurityController and handle it to OneginiSdk -  See the [Supported Methods:](#supported-methods) setSecurityControllerClassName(className). 
    Example SecurityController implementation you can find inside library source code("com.onegini.mobile.SecurityController").
    By default security options brought from `com.onegini.mobile.SecurityController`.
    
    More information [HERE](https://docs.onegini.com/msp/stable/android-sdk/reference/security-controls.html#examples), section: SecurityController.

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

6. Run `pod install`    

7. Add `SecurityController.h` and `SecurityController.m` as described [HERE](https://docs.onegini.com/msp/stable/ios-sdk/reference/security-controls.html)

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
    project(':react-native-sdk-beta').projectDir = new File(rootProject.projectDir,     '../node_modules/react-native-sdk-beta/android')
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

## Configuration
### Config structure
   {
      customProviders: [],
      enableMobileAuthenticationOtp: true,
      enableFingerprint: true
   }
   
   - customProviders - conteins the custom registration providers with app want to support. 
   - enableMobileAuthenticationOtp - true if you want to use the authentication by Otp. If true then the events MOBILE_AUTH_OTP_NOTIFICATION are triggered.
   - enableFingerprint - true if you want to use the fingerprint in-app as the Authentication method. If true then the events ONEGINI_FINGERPRINT_NOTIFICATION are triggered.

### CustomProviders structure
   [{id:"id1", isTwoStep: true}, ...]

   - id - this is identity provider id. if the id provider is supported, then the events ONEGINI_CUSTOM_REGISTRATION_NOTIFICATION are triggered.
   - isTwoStep:
      - true - possible actions initRegistration, initRegistration are sent by ONEGINI_CUSTOM_REGISTRATION_NOTIFICATION
      - false - possible actions finishRegistration are sent by ONEGINI_CUSTOM_REGISTRATION_NOTIFICATION
      

## Supported Methods

| Method                     | Description                                                                                                                                                                               |
| ------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`setConfigModelClassName(className)`**                  |  (Android only) Sets the path to OneginiConfigModel class(e.g. `com.exampleapp.OneginiConfigModel`). By default SDK looking for config at `[RN_application_package_classpath].OneginiConfigModel`. This has to be set **before** startClient(). More information [HERE](#android-setup-config)                                                |
| **`setSecurityControllerClassName(className)`**           |  (Android only) Sets the path to SecurityController class(e.g. `com.exampleapp.SecurityController`). By default controller brought from `com.onegini.mobile.SecurityController`. This has to be set **before** startClient(). More information [HERE](#android-setup-security-controller)
| **`startClient(config):Promise`**                         |  Method init the OriginiSDK. Config is optional. Example object is in "js/config.js". See structure [HERE](config-structure). If the config is not set the app uses the js/config.js as the default.                                                             |                                      |
| **`addEventListener(eventType, cb)`**                     |  Adds listener for certain event type(ONEGINI_PIN_NOTIFICATION, ONEGINI_CUSTOM_REGISTRATION_NOTIFICATION).        |
| **`removeEventListener(eventType, cb)`**                  |  Removes listener for certain event type(ONEGINI_PIN_NOTIFICATION, ONEGINI_CUSTOM_REGISTRATION_NOTIFICATION)       |
| **`getIdentityProviders()`**                              |  Returns the identity Providers with are registered int the lib.  |
| **`getAccessToken()`**                                    |  Returns the access token if exist. |
| **`enrollMobileAuthentication()`**                        |  The first enrollment step. **[iOS not supported, yet**] |
| **`submitAcceptMobileAuthOtp()`**                         |  User can return accept authentication request. **[iOS not supported, yet**] |
| **`submitDenyMobileAuthOtp()`**                           |  User can return deny authentication request. **[iOS not supported, yet**] |
| **`handleMobileAuthWithOtp()`**                           |  User can return otpCode. **[iOS not supported, yet**] |
| **`getAuthenticatedUserProfile()`**                       |  Returns user who is logged in. **[iOS not supported, yet**] |
| **`registerUser(identityProviderId):Promise`**            |  Starts the process of registration user. If success then the response contain the success = true if not then contain success = false. |
| **`deregisterUser(profileId):Promise`**                   |  Starts the process of deregistration user. If success then the response contain the success = true if not then contain success = false. |
| **`getRedirectUri():Promise`**                            |  Returns an object with the redirect Uri field. |
| **`getUserProfiles():Promise`**                           |  Returns all registered profiles id. |
| **`handleRegistrationCallback(uri)`**                     |  Pass a url for the registration process which obtained from browser redirect action. |
| **`cancelRegistration():Promise`**                        |  Interrupts process of registration. |
| **`submitCustomRegistrationReturnSuccess(identityProviderId, result)`**|  Triggers the ReturnSuccess method in callback from the custom registration process.  If the identityProviderId does not exist then an error occurs. **[iOS not supported, yet**|
| **`submitCustomRegistrationReturnError(identityProviderId, result)`** |  Triggers the ReturnError method in callback from the custom registration process.If the identityProviderId does not exist then an error occurs. **[iOS not supported, yet**|
| **`submitPinAction(flow, action, pin):Promise`**          |  Triggers the process of the pin. A callback can be return by event("ONEGINI_PIN_NOTIFICATION"). If flow is authentication then the submitAuthenticationPinAction method is triggered. If flow is create then the submitCreatePinAction method is triggered. If flow is change then the submitChangePinAction method is triggered.  |
| **`submitAuthenticationPinAction(action, pin)`**          |  (Android only) Triggers the process of the pin. A callback can be return by event("ONEGINI_PIN_NOTIFICATION"). Possible actions: provide, cancel. |
| **`submitChangePinAction(action, pin)`**                  |  (Android only) Triggers the process of the pin. A callback can be return by event("ONEGINI_PIN_NOTIFICATION"). Possible actions: provide, cancel, change. |
| **`submitCreatePinAction(action, pin):Promise`**          |  (Android only) Triggers the process of the pin. A callback can be return by event("ONEGINI_PIN_NOTIFICATION"). Possible actions: provide, cancel. |
| **`authenticateUser(profileId):Promise`**                 |  (Android only) Starts the process of authentication user.  |
| **`logout():Promise`**                                    |  Starts the process of logout user.  |
| **`getRegisteredAuthenticators():Promise`**               |  Returns all authenticators which are registered. One of the authenticators can be set as preferred authenticator.|
| **`getAllAuthenticators():Promise`**                      |  Returns all supported authenticators.  |
| **`setPreferredAuthenticator(profileId, idOneginiAuthenticator):Promise`** |  sets an authenticator that is used at the process of user authentication  **[iOS not supported, yet**] |
| **`registerFingerprintAuthenticator(profileId):Promise`**     |  starts the process of registration a fingerprint **[iOS not supported, yet**] |
| **`deregisterFingerprintAuthenticator(profileId):Promise`**   |  starts the process of deregistration a fingerprint **[iOS not supported, yet**] |
| **`isFingerprintAuthenticatorRegistered(profileId):Promise`** | **[iOS not supported, yet**] |
| **`submitFingerprintAcceptAuthenticationRequest()`**          | User can return  accept authentication request **[iOS not supported, yet**] |
| **`submitFingerprintDenyAuthenticationRequest()`**            | User can return  deny authentication request **[iOS not supported, yet**] |
| **`submitFingerprintFallbackToPin()`**                    | User can return  fallback to authentication by pin **[iOS not supported, yet**] |