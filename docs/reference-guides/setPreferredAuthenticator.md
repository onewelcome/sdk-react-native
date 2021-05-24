
# setPreferredAuthenticator

Used to get the preferred authenticator for the currently authenticated user. Does not require any arguments.

` setPreferredAuthenticator(profileId: string, idOneginiAuthenticator: string): Promise<void>`
| Property | Type |Description |
| ------ | ------ |----------- |
| profileId  | string | The profile ID you previously stored during registration |
| idOneginiAuthenticator  | string | The authenticator ID, which distinguishes between authenticators of type "Custom". (Only required for custom authenticators) |

**Example**
```
OneginiSdk.setPreferredAuthenticator(profileId, authenticatorId)
  .then(()) => {
    console.log('setPreferredAuthenticator succeed!');
  })
  .catch(error => {
    console.log('setPreferredAuthenticator failed!: ', error);
  });
```
