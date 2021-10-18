# getAllAuthenticators

Used to get an array of authenticators available for a specific user. Requires an object containing a profileId.

`getAllAuthenticators(profileId: string): Promise<Authenticator[]>`
| Property | Type |Description |
| ------ | ------ |----------- |
| profileId  | string | The profile ID you previously stored during registration |


**Example**
```
OneginiSdk.getAllAuthenticators(profileId)
  .then(authenticators => {
    console.log('getAllAuthenticators succeed: ', authenticators)
  })
  .catch(error => {
    console.log('getAllAuthenticators failed!: ', error.message)
  })
```

**Success**
| Property | Type |Description |
| ------ | ------ |----------- |
| authenticators  | [Authenticator](Authenticator.md)[] | List of all authenticators |

**Error**
| Property | Example | Description |
| ------ | ------ |  ----------- |
| code   | 8000   | The error code |
| message   | "Onegini: Internal plugin error"   | Human readable error description |
