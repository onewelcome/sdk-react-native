The React Native plugin will be released to the [npmjs.com](https://www.npmjs.com/org/onewelcome) repository. In order to publish to the npm repository, you will need to have an account that is registered to the organisation. 
The plugin will need to be published following the instructions on [creating-and-publishing-an-organization-scoped-package](https://docs.npmjs.com/creating-and-publishing-an-organization-scoped-package).


#### Typescript code
The plugin contains typescript functions which wrap the native functions exposed to RN.

The typescript code will have to be built and types.d.ts files will have to be created for this before publishing. These need to be published with the plugin in order for type completion to work.

#### Native code
The native code for iOS/Android will be added as plain source code. The users will then have to build this code themselves. This should work when they follow the documentation on [configuration](https://developer.onewelcome.com/react-native/sdk/configuration).

The onegini-sdk should be installed automatically for android and has to be added manually for iOS following the [documentation](https://developer.onewelcome.com/react-native/sdk/configuration).


## Publish


#### Testing locally
Before publishing we need to verify that everything works before we publish to the public repository. We can do this by using a npm library [yalc](https://www.npmjs.com/package/yalc) which allows us to mimic the behavior of [npmjs.com](https://www.npmjs.com/org/onewelcome) locally. 

The plugin should be installable in a clean react-native project following our documentation. 


#### Steps

1. Create branch with all updated code and corrrect name for the release
2. Build the javascript with yarn build (make sure you have .js and .d.ts files)
3. Test build on local example app using [yalc](https://www.npmjs.com/package/yalc)
4. Publish the plugin to [npmjs.com](https://www.npmjs.com/org/onewelcome)
5. Test published version.
