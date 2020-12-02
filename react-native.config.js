'use strict';


module.exports = {
  reactNativePath: '../node_modules/react-native',
  project: {
    android: {
      sourceDir: './example/android',
    },
  },
  dependencies: {
    'react-native-sdk-beta' : {
      platforms : {
        android : null,
        ios     : null,
      },
    },
    '@react-native-async-storage/async-storage' : {
      platforms : {
        ios     : null,
      },
    }
  }
}
