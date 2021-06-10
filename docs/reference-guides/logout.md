# logout

For security reasons, it is always advisable to explicitly logout a user. The Onegini React Native Plugin exposes the following function to do so.


`logout(): Promise<void>`

**Example**
```
OneginiSdk.logout()
  .then(() => {
    console.log('Logout succeed!')
  })
  .catch(error => {
    console.log('Logout failed!: ', error.message)
  })
```

**Error**
| Property | Example | Description |
| ------ | ------ |  ----------- |
| code   | 8000   | The error code |
| message   | "Onegini: Internal plugin error"   | Human readable error description |
