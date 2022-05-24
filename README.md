# onewelcome-react-native-sdk

## Getting started

`npm install onewelcome-react-native-sdk --save`

OR

`yarn add onewelcome-react-native-sdk`

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

6. <a name="android-setup-config"/>Setup the config: Generate a 'OneginiConfigModel' and 'keystore.bks' with [SDK Configurator](https://github.com/Onegini/onegini-sdk-configurator#android).

   Configurator will put `OneginiConfigModel` into `[RN_application_package_classpath.OneginiConfigModel]` (e.g. `com.exampleapp.OneginiConfigModel`) and the `keystore.bks` into '/res/raw'.
   After configurator used - you have 2 options:

   - Keep it as it is.
   - If there is a need to move `OneginiConfigModel` to another place - it's **required** to specify custom classpath for OneWelcomeSdk: [Supported Methods:](#supported-methods) setConfigModelClassName(className).

   More information [HERE](https://docs.onegini.com/msp/stable/android-sdk/topics/setting-up-the-project#verifying), section: Running the SDK Configurator.

7. <a name="android-setup-security-controller"/>Setup the SecurityController(<u>not required</u>).

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

   Replace the <username> and <password> with the credentials that you use to login to support.onegini.com.

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

1. Open up `android/app/src/main/java/[...]/MainActivity.java`

- Add `import com.onegini.mobile.RNOneginiSdkPackage;` to the imports at the top of the file
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

# Known RN issues

These are the issues that are not connected to OneWelcome React Native SDK but you may encounter them during integration.

## Xcode 12.5 with Flipper

### Discussion

https://github.com/facebook/flipper/issues/2215

### Solution

In `ios/Podfile` change `use_flipper!` into `use_flipper!({ 'Flipper-Folly' => '2.5.3', 'Flipper' => '0.87.0', 'Flipper-RSocket' => '1.3.1' })`

## Undefined symbols for architecture

### Discussion

https://github.com/facebookarchive/react-native-fbsdk/issues/794

### Solution

Open Xcode project (.xcworkspace) and add empty Swift file (NotUsed.swift). When prompt for creating Create Bridging Header - accept.

## Podfile

### Discussion

### Solution

In iOS/Pofile add at the top
`add source 'https://github.com/CocoaPods/Specs.git'`

## Codegen / Invalid regular expression

### Discussion

https://github.com/facebook/react-native/issues/31180

### Solution

`yarn add --dev react-native-codegen`
