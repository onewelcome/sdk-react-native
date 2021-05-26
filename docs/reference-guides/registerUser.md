# registerUser

Before a user can authenticate using PIN or fingerprint, a user will need to register with your Token Server. Registration can be initiated using this method.

It may be required to handle registration with some additional action (PIN/Fingerprint) handling. Please refer to #usePinFlow and #addEventListener

`registerUser(identityProviderId: string | null, scopes: string[]): Promise<Profile>`
| Property | Type | Description |
| ------ | ------ | ----------- |
| identityProviderId | string | Identity provider |
| scopes | string[] | An array of scopes the user will register for (optional) |


**Example**
```
OneginiSdk.registerUser(providerId)
    .then(profile => {
        console.log('Registration success! ', profile)
    })
    .catch(error => {
        console.error('Registration failed: ', error.message)
    })
```


**Success**
| Property | Example | Description |
| ------ | ------ |  ----------- |
| Profile   |  #Profile  | New registered profile |

**Error**
| Property | Example | Description |
| ------ | ------ |  ----------- |
| code   | 8000   | The error code |
| message   | "Onegini: Internal plugin error"   | Human readable error description |

