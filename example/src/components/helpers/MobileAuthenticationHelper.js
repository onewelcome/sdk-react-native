import OneginiSdk from 'react-native-sdk-beta';

const enrollMobileAuthentication = (setSuccessful, setError) => {
    OneginiSdk.enrollMobileAuthentication()
        .then(() => {
            setSuccessful("Mobile Authentication enabled")
        })
        .catch((error) => {
            setError(error.message)
        })
};

const handleMobileAuthWithOtp = (otpCode, setSuccessful, setError) => {
    OneginiSdk.handleMobileAuthWithOtp(otpCode)
        .then(() => {
            setSuccessful?.("Authentication successful")
        })
        .catch((error) => {
            setError?.(error.message)
        })
};

export { enrollMobileAuthentication,handleMobileAuthWithOtp };