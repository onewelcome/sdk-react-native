import OneginiSdk from 'react-native-sdk-beta';

const enrollMobileAuthentication = (
  setSuccessful: (msg: string) => void,
  setError: (msg: string) => void,
) => {
  OneginiSdk.enrollMobileAuthentication()
    .then(() => {
      setSuccessful('Mobile Authentication enabled');
    })
    .catch((error) => {
      setError(error.message);
    });
};

const handleMobileAuthWithOtp = (
  otpCode: string,
  setSuccessful: (msg: string) => void,
  setError: (msg: string) => void,
) => {
  OneginiSdk.handleMobileAuthWithOtp(otpCode)
    .then(() => {
      setSuccessful?.('Authentication successful');
    })
    .catch((error) => {
      setError?.(error.message);
    });
};

export {enrollMobileAuthentication, handleMobileAuthWithOtp};
