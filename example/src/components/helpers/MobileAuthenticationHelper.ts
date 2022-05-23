import OneWelcomeSdk from 'onewelcome-react-native-sdk';

const enrollMobileAuthentication = (
  setSuccessful: (msg: string) => void,
  setError: (msg: string) => void,
) => {
  OneWelcomeSdk.enrollMobileAuthentication()
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
  OneWelcomeSdk.handleMobileAuthWithOtp(otpCode)
    .then(() => {
      setSuccessful?.('Authentication successful');
    })
    .catch((error) => {
      setError?.(error.message);
    });
};

export {enrollMobileAuthentication, handleMobileAuthWithOtp};
