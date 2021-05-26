# handleMobileAuthWithOtp

When a user is enrolled for mobile authentication, they are able to receive and respond to mobile authentication requests

`handleMobileAuthWithOtp(otpCode: string): Promise<void>`
| Property | Type | Description |
| ------ | ------ | ----------- |
| otpCode   | string   | One TIme Password code |

**Example**
```
OneginiSdk.handleMobileAuthWithOtp('base64 encoded OTP')
  .then(() => {
    console.log('Handle Mobile Auth with Otp succeed!')
  })
  .catch(error => {
    console.log('Handle Mobile Auth with Otp failed!: ', error.message)
  })
```

**Error**
| Property | Example | Description |
| ------ | ------ |  ----------- |
| code   | 8000   | The error code |
| message   | "Onegini: Internal plugin error"   | Human readable error description |
