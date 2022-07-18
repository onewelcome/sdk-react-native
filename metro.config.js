/**
 * Metro configuration for React Native
 * https://github.com/facebook/react-native
 *
 * @format
 */

const path = require('path');
const rnProjectRoot =  path.join(__dirname, '/example');

module.exports = {
  projectRoot: rnProjectRoot,
  watchFolders: [rnProjectRoot, __dirname],
  transformer: {
    getTransformOptions: async () => ({
      transform: {
        experimentalImportSupport: false,
        inlineRequires: false,
      },
    }),
  },
};
