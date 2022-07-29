'use strict';


module.exports = {
  project: {
    android: {
      sourceDir: './example/android',
    },
  },
  dependencies: {
    'onewelcome-react-native-sdk' : {
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
