# submitAcceptAuthenticationRequest

Submit accept for Fingerprint Authorization request. Please refer to [useFingerprintFlow](useFingerprintFlow.md).

`submitFingerprintAcceptAuthenticationRequest(): Promise<void>`

**Example**
```
OneginiSdk.submitFingerprintAcceptAuthenticationRequest()
    .then(() => {
        console.log('Submit accept success!')
    })
    .catch(error => {
        console.error('Submit accept failed: ', error.message)
    })
```

**Error**
| Property | Example | Description |
| ------ | ------ |  ----------- |
| code   | 8000   | The error code |
| message   | "Onegini: Internal plugin error"   | Human readable error description |
