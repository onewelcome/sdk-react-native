
# authenticateDeviceForResource

The device can be authenticated for specific (or the default) scopes.

`authenticateDeviceForResource(resourcePath: string): Promise<any>`
| Property | Type | Description |
| ------ | ------ | ----------- |
| resourcePath   | string   | The profile ID you previously stored during registration |

**Example**
```
OneginiSdk.authenticateDeviceForResource(path)
  .then(() => {
    console.log('Device Authentication succeed!');
  })
  .catch(error => {
    console.log('Device Authentication failed!: ', error);
  });
```
