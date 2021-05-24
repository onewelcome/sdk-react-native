
# registerUser

Before a user can authenticate using PIN or fingerprint, a user will need to register with your Token Server. Registration can be initiated using this method.

`registerUser(identityProviderId: string | null): Promise<Profile>`
| Property | Description |
| ------ | ----------- |
| identityProviderId   | Identity provider |
| scopes   | An array of scopes the user will register for (optional) |


**Example**
```
OneginiSdk.registerUser(providerId)
    .then(profile => {
        console.log('Registration success! ', profile);
    })
    .catch(err => {
        console.error('Registration failed: ', err)
    })
```

