# submitFallbackToPin

If Fingerprint authorization failed - you can force user to fallback to PIN. Please also refer to #useFingerprintFlow

`submitFallbackToPin(): Promise<void>`

**Example**
```
OneginiSdk.submitFallbackToPin()
    .then(() => {
        console.log('Fallback success! ')
    })
    .catch(error => {
        console.error('Fallback failed: ', error.message)
    })
```

**Error**
| Property | Example | Description |
| ------ | ------ |  ----------- |
| code   | 8003   | The error code |
| message   | "Onegini: The fingerprint is not enabled. Please check your configuration"   | Human readable error description |
