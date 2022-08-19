# onewelcome-react-native-sdk

## Getting started

`npm install @onewelcome/react-native-sdk --save`

OR

`yarn add @onewelcome/react-native-sdk`

## SDK Configuration

1. Get access to https://repo.onegini.com/artifactory/onegini-sdk
2. Use https://github.com/Onegini/onegini-sdk-configurator on your application (instructions can be found there)

## App Configuration

#### Android:

1. Modify `android/app/build.gradle`:


   1.2 Add to `android` -> `defaultConfig` section:

   ```
   minSdkVersion 23
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

3. Add to `android/build.gradle`[buildscript]:

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

6. Setup the config: Generate a 'OneginiConfigModel' and 'keystore.bks' with [SDK Configurator](https://github.com/Onegini/onegini-sdk-configurator#android).

   Configurator will put `OneginiConfigModel` into `[RN_application_package_classpath.OneginiConfigModel]` (e.g. `com.exampleapp.OneginiConfigModel`) and the `keystore.bks` into '/res/raw'.
   After configurator used - you have 2 options:

   - Keep it as it is.
   - If there is a need to move `OneginiConfigModel` to another place - it's **required** to specify custom classpath for OneWelcomeSdk: [Supported Methods:](#supported-methods) setConfigModelClassName(className).

   More information [HERE](https://docs.onegini.com/msp/stable/android-sdk/topics/setting-up-the-project#verifying), section: Running the SDK Configurator.

7. Setup the SecurityController(not required).

   In order to change security options you should create your own instance SecurityController and handle it to OneWelcomeSdk - See the [Supported Methods:](#supported-methods) setSecurityControllerClassName(className).
   Example SecurityController implementation you can find inside library source code("com.onegini.mobile.SecurityController").
   By default security options brought from `com.onegini.mobile.SecurityController`.

   More information [HERE](https://docs.onegini.com/msp/stable/android-sdk/reference/security-controls#examples), section: SecurityController.

#### iOS:

1. The OneWelcome SDK is uploaded to the OneWelcome Artifactory repository. In order to let CocoaPods use an Artifactory repository you need to install a specific plugin.
   ```
   gem install cocoapods-art
   ```
2. The OneWelcome SDK repository is not a public repository. You must provide credentials in order to access the repo. Create a file named .netrc in your Home folder (~/) and add the following contents to it:

   ```
   machine repo.onegini.com
   login <username>
   password <password>
   ```

   Replace the `<username>` and `<password>` with the credentials that you use to login to support.onegini.com.

3. The OneWelcome CocoaPods repository must be added to your local machine using the following command:

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

8. **Optional** In order to support FaceID or ToucID add next to `ios/<project-name>/info.plist:
   ```
   <key>NSFaceIDUsageDescription</key>
   <string>Application needs access to support authentication with Face/Touch ID</string>
   ```
   **!!!NOTE**: Biometrics will not work in iOS simulator, only on the real devices

## Linking Native Code

### RN >= 60.0

`cd ios && pod install`

### RN < 60.0

##### Auto linking

`react-native link onewelcome-react-native-sdk`

##### Manual installation

#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `onewelcome-react-native-sdk` and add `RNOneginiSdk.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNOneginiSdk.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`

- Add `import com.onegini.mobile.sdk.reactnative.RNOneginiSdkPackage;` to the imports at the top of the file
- Add `new RNOneginiSdkPackage()` to the list returned by the `getPackages()` method

2. Append the following lines to `android/settings.gradle`:
   ```
   include ':onewelcome-react-native-sdk'
   project(':onewelcome-react-native-sdk').projectDir = new File(rootProject.projectDir,     '../node_modules/onewelcome-react-native-sdk/android')
   ```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
   ```
     compile project(':onewelcome-react-native-sdk')
   ```

# How to run Example App

- `yarn` or `npm install`
- **iOS**: `yarn ios` or `npm run ios`
- **Android**: `yarn android` or `npm run android`


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

| Methods                     | Description                                                                                                                                                                               |
| ------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`setConfigModelClassName(className)`**                  |  (Android only) Sets the path to OneginiConfigModel class(e.g. `com.exampleapp.OneginiConfigModel`). By default SDK looking for config at `[RN_application_package_classpath].OneginiConfigModel`. This has to be set **before** startClient(). More information [HERE](#android-setup-config)                                                |
| **`setSecurityControllerClassName(className)`**           |  (Android only) Sets the path to SecurityController class(e.g. `com.exampleapp.SecurityController`). By default controller brought from `com.onegini.mobile.SecurityController`. This has to be set **before** startClient(). More information [HERE](#android-setup-security-controller)
| **`startClient(config):Promise`**                         |  Method init the OriginiSDK. Config is optional. Example object is in "js/config.js". See structure [HERE](#config-structure). If the config is not set the app uses the "js/config.js" as the default.                                                             |                                      |
| **`addEventListener(eventType, cb)`**                     |  Adds listener for certain event type(ONEGINI_PIN_NOTIFICATION, ONEGINI_CUSTOM_REGISTRATION_NOTIFICATION).        |
| **`removeEventListener(eventType, cb)`**                  |  Removes listener for certain event type(ONEGINI_PIN_NOTIFICATION, ONEGINI_CUSTOM_REGISTRATION_NOTIFICATION)       |
|                                                           |
| **=== Data getters ===**                                  |
| **`getIdentityProviders()`**                              |  Returns the identity Providers with are registered int the lib.  |
| **`getAccessToken()`**                                    |  Returns the access token if exist. |
| **`getRedirectUri():Promise`**                            |  Returns an object with the redirect Uri field. |
| **`getUserProfiles():Promise`**                           |  Returns all registered profiles id. |
| **`getAuthenticatedUserProfile()`**                       |  Returns user who is logged in. |
|                                                           |
| **=== Resource getters ===**                              |
| **`getAppDetailsResource()`**                             |  Returns an object with app details(fetched from the server).  |
| **`getDeviceListResource()`**                             |  Returns an array with device objects witch registered by this user(fetched from the server).  |
|                                                           |
| **=== User register/deregister ===**                      |
| **`registerUser(identityProviderId):Promise`**            |  Starts the process of registration user. If success then the response contain the success = true if not then contain success = false. |
| **`deregisterUser(profileId):Promise`**                   |  Starts the process of deregistration user. If success then the response contain the success = true if not then contain success = false. |
| **`handleRegistrationCallback(uri)`**                     |  Pass a url for the registration process which obtained from browser redirect action. |
| **`cancelRegistration():Promise`**                        |  Interrupts process of registration. |
|                                                           |
| **=== Authentication ===**                                |
| **`authenticateUser(profileId):Promise`**                 |  Starts the process of authentication user.  |
| **`logout():Promise`**                                    |  Starts the process of logout user.  |
| **`getAllAuthenticators():Promise`**                      |  Returns all supported authenticators.  |
| **`getRegisteredAuthenticators():Promise`**               |  Returns all authenticators which are registered. One of the authenticators can be set as preferred authenticator.|
| **`setPreferredAuthenticator(profileId, idOneginiAuthenticator):Promise`** |  Sets an authenticator that is used at the process of user authentication |
|                                                           |
| **=== PIN ===**                                           |
| **`submitPinAction(flow, action, pin):Promise`**          |  Triggers the process of the pin. A callback can be return by event("ONEGINI_PIN_NOTIFICATION"). |
| **`changePin():Promise`**                                 |  Starts the process of changin PIN for currently authenticated user.  |
|                                                           |
| **=== Fingerprint ===**                                   |
| **`registerFingerprintAuthenticator(profileId):Promise`**     | Starts the process of registration a fingerprint |
| **`deregisterFingerprintAuthenticator(profileId):Promise`**   | Starts the process of deregistration a fingerprint |
| **`isFingerprintAuthenticatorRegistered(profileId):Promise`** | Returns boolean value which defines weather fingerprint authenticator already registered |
| **`submitFingerprintAcceptAuthenticationRequest():Promise`**  | (Android only) User can return  accept authentication request |
| **`submitFingerprintDenyAuthenticationRequest():Promise`**    | (Android only) User can return  deny authentication request |
| **`submitFingerprintFallbackToPin():Promise`**                | (Android only) User can return  fallback to authentication by pin |
|                                                           |
| **=== OTP ===**                                           |
| **`enrollMobileAuthentication()`**                        |  The first enrollment step. |
| **`acceptMobileAuthConfirmation()`**                      |  User can return accept authentication request. |
| **`denyMobileAuthConfirmation()`**                        |  User can return deny authentication request. |
| **`handleMobileAuthWithOtp()`**                           |  User can return otpCode. |
| **`submitCustomRegistrationAction(action, identityProviderId, token)`**|  Triggers the process of the custom registration. Where **action** = CUSTOM_REGISTRATION_ACTIONS and **token** = obtained from the server. If the identityProviderId does not exist then an error occurs. |
|                                                           |
| **=== App2Web ===**                                       |
| **`startSingleSignOn()`**                                 |  Redirects user to Web app with loggin in user. |
  
## Local Development of SDK
  
The SDK can be developed locally using the [example app](https://github.com/onewelcome/example-app-react-native). 
React Native does not support symlinking with dependencies in the node_modules folder because of the metro bundler see [issue](https://github.com/facebook/metro/issues/1).

In order to develop the sdk you can link the project in the metro.config.js which can be done in the following way. Make sure to update the `packagePath`.

Make sure to close out of your metro instance and rerun your application. 


```
const packagePath = '<absolute path to folder containing the sdk>/sdk-react-native';
module.exports = {
  projectRoot: __dirname,
  watchFolders: [__dirname, packagePath],
  transformer: {
    getTransformOptions: async () => ({
      transform: {
        experimentalImportSupport: false,
        inlineRequires: true,
      },
    }),
  },
  resolver: {
    nodeModulesPaths: [packagePath],
  },
};
```

