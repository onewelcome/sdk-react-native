
# registerAuthenticator

Used to register a new authenticator for the currently authenticated user.

`registerAuthenticator(profileId: string, type: string): Promise<void>`
| Property | Type | Description |
| ------ | ------ | ----------- |
| profileId   | string   | The profile ID you previously stored during registration |
| type   | string   | The authenticator type e.g. "PIN" |

**Example**
```
OneginiSdk.registerAuthenticator(profileId, 'PIN')
  .then(() => {
    console.log('Register Authenticator succeed!');
  })
  .catch(error => {
    console.log('Register Authenticator failed!: ', error);
  });
```
