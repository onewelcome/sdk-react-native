import OneginiSdkTs from "react-native-sdk-beta/ts/index_ts";

const enrollMobileAuthentication = (setSuccessful, setError) => {
  OneginiSdkTs.enrollMobileAuthentication()
        .then(() => {
            setSuccessful("Mobile Authentication enabled")
        })
        .catch((error) => {
            setError(error.message)
        })
};

const handleMobileAuthWithOtp = (otpCode, setSuccessful, setError) => {
  OneginiSdkTs.handleMobileAuthWithOtp(otpCode)
        .then(() => {
            setSuccessful?.("Authentication successful")
        })
        .catch((error) => {
            setError?.(error.message)
        })
};

export { enrollMobileAuthentication,handleMobileAuthWithOtp };