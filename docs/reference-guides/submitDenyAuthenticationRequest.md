# submitFingerprintDenyAuthenticationRequest

Submit deny for Fingerprint Authorization request. Please refer to [useFingerprintFlow]()

`submitFingerprintDenyAuthenticationRequest(): Promise<void>`

**Example**
```
OneginiSdk.submitFingerprintDenyAuthenticationRequest()
    .then(() => {
        console.log('Submit deny success!')
    })
    .catch(error => {
        console.error('Submit deny failed: ', error.message)
    })
```

**Error**
| Property | Example | Description |
| ------ | ------ |  ----------- |
| code   | 8000   | The error code |
| message   | "Onegini: Internal plugin error"   | Human readable error description |
