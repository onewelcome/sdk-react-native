import OnewelcomeSdk from 'onewelcome-react-native-sdk';

const enrollMobileAuthentication = (
  setSuccessful: (msg: string) => void,
  setError: (msg: string) => void,
) => {
  OnewelcomeSdk.enrollMobileAuthentication()
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
  OnewelcomeSdk.handleMobileAuthWithOtp(otpCode)
    .then(() => {
      setSuccessful?.('Authentication successful');
    })
    .catch((error) => {
      setError?.(error.message);
    });
};

export {enrollMobileAuthentication, handleMobileAuthWithOtp};
