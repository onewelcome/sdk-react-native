The React Native plugin will be released to the [npmjs.com](https://www.npmjs.com/org/onewelcome) repository. In order to publish to the npm repository, you will need to have an account that is registered to the organisation. 
The plugin will need to be published following the instructions on [creating-and-publishing-an-organization-scoped-package](https://docs.npmjs.com/creating-and-publishing-an-organization-scoped-package).


#### Typescript code
The plugin contains typescript functions which wrap the native functions exposed to RN.

The typescript code has to be built and types.d.ts files will have to be created for this before publishing. These need to be published with the plugin in order for type completion to work.
This is done by react-native-builder-bob, it can be run by calling `yarn prepack` and is run automatically when publishing with npm and also when publishing locally with yalc.

#### Native code
The native code for iOS/Android is published as plain source code. The users will have to build this code themselves. This should work when they follow the documentation on [configuration](https://developer.onewelcome.com/react-native/sdk/configuration).

The onegini-sdk has to be added manually by following the [documentation](https://developer.onewelcome.com/react-native/sdk/configuration).

#### Testing locally
Before publishing we need to verify that everything works before we publish to the public repository. We can do this by using a npm library [yalc](https://www.npmjs.com/package/yalc) which allows us to mimic the behavior of [npmjs.com](https://www.npmjs.com/org/onewelcome) locally. 

The plugin should be installable in a clean react-native project following our documentation. 


#### Release Steps
-  Create branch with all updated code and corrrect name for the release
-  Test build on local example app using [yalc](https://www.npmjs.com/package/yalc)
-  Update the version number in the package.json
-  Create a release note in https://app.developerhub.io/react-native-sdk/
-  If required, create new upgrade instructions in public docs on DeveloperHub.
-  Rename the DeveloperHub version from "dev" to proper name. Don't publish the new version yet (it would trigger RSS feed now).
-  Create a new GIT tag pointing to the new version; push the tag.
-  Publish the plugin to [npmjs.com](https://www.npmjs.com/org/onewelcome)
   -  `npm publish`
- Test published version.
- Update the package.json version within to next SNAPSHOT; commit and push.
- Merge the release branch into the original branch (master or hotfix branch).
- Publish the version on the DeveloperHub (which will also trigger a RSS feed.)
- Create new documentation version on DeveloperHub named "dev". You should create it by cloning the previous version. Make sure "dev" is not published.
