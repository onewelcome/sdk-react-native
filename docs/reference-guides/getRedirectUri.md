
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
    console.log('Redirect Uri failed!: ', error)
  });
```

**Returns**
| Property | Type | Description |
| ------ | ------ | ----------- |
| redirectUri   | RedirectUri   | Current redirect URI |
