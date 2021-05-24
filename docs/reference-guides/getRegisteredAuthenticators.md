
# getRegisteredAuthenticators

Used to get an array of authenticators registered for a specific user. Requires an object containing a profileId.

`getRegisteredAuthenticators(profileId: string): Promise<Authenticator[]>`
| Property | Type |Description |
| ------ | ------ |----------- |
| profileId  | string | The profile ID you previously stored during registration |


**Example**
```
OneginiSdk.getRegisteredAuthenticators(profileId)
  .then(authenticators => {
    console.log('getRegisteredAuthenticators succeed: ', authenticators);
  })
  .catch(error => {
    console.log('getRegisteredAuthenticators failed!: ', error);
  });
```
**Returns**
| Property | Type |Description |
| ------ | ------ |----------- |
| authenticators  | Authenticator[] | List of registered authenticators |
