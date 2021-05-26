# enrollMobileAuthentication

Authenticated users can enroll for mobile authentication, allowing them to execute mobile authentication with OTP. See the mobile authentication topic guide for more information on mobile authentication in general.

`enrollMobileAuthentication(): Promise<void>`

**Example**
```
OneginiSdk.enrollMobileAuthentication()
    .then(() => {
        console.log('Enroll success! ', profile)
    })
    .catch(error => {
        console.error('Enroll failed: ', error.message)
    })
```

**Error**
| Property | Example | Description |
| ------ | ------ |  ----------- |
| code   | 8000   | The error code |
| message   | "Onegini: Internal plugin error"   | Human readable error description |
