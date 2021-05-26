# denyMobileAuthConfirmation

When a user is enrolled for mobile authentication, they are able to receive and respond to mobile authentication requests. 

`denyMobileAuthConfirmation(): Promise<void>`

**Example**
```
OneginiSdk.denyMobileAuthConfirmation()
    .then(profile => {
        console.log('Accept mobile auth success! ', profile)
    })
    .catch(error => {
        console.error('Accept mobile auth failed: ', error.message)
    })
```

**Error**
| Property | Example | Description |
| ------ | ------ |  ----------- |
| code   | 8000   | The error code |
| message   | "Onegini: Internal plugin error"   | Human readable error description |
