# getRedirectUri

Returns current redirect URI.

`getRedirectUri(): Promise<RedirectUri>`

**Example**
```
OneginiSdk.getRedirectUri()
  .then((uri: RedirectUri) => {
    console.log('Redirect Uri succeed! ', uri)
  })
  .catch(error => {
    console.log('Redirect Uri failed!: ', error.message)
  })
```

**Returns**
| Property | Type | Description |
| ------ | ------ | ----------- |
| redirectUri   | #RedirectUri   | Current redirect URI |

**Error**
| Property | Example | Description |
| ------ | ------ |  ----------- |
| code   | 8000   | The error code |
| message   | "Onegini: Internal plugin error"   | Human readable error description |
