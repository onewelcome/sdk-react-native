'use strict';


module.exports = {
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
