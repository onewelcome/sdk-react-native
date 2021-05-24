
# handleMobileAuthWithOtp

Used to register a new authenticator for the currently authenticated user.

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
    console.log('Handle Mobile Auth with Otp failed!: ', error)
  });
```
