# authenticateDeviceForResource

The device can be authenticated for specific (or the default) scopes.

`authenticateDeviceForResource(resourcePath: string): Promise<any>`
| Property | Type | Description |
| ------ | ------ | ----------- |
| resourcePath   | string   | Resource path the device will authenticate for |

**Example**
```
OneginiSdk.authenticateDeviceForResource(path)
  .then(() => {
    console.log('Device Authentication succeed!')
  })
  .catch(error => {
    console.log('Device Authentication failed!: ', error.message)
  })
```

**Error**
| Property | Example | Description |
| ------ | ------ |  ----------- |
| code   | 8000   | The error code |
| message   | "Onegini: Internal plugin error"   | Human readable error description |

