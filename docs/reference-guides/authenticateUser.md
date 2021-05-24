
# authenticateUser

Your users can authenticate themselves using any authenticator registered to them. Authentication is done via an AuthenticationHandler. If a user is already authenticated when calling this function, they will be logged out and have to authenticate again.

`authenticateUser(profileId: string): Promise<Types.AuthData>`
| Property | Description |
| ------ | ----------- |
| profileId   | The profile ID you previously stored during registration |

**Example**
```
OneginiSdk.authenticateUser(profileId)
  .then(authData => {
    console.log('User authentication succeed!');
  })
  .catch(error => {
    console.log('User authentication failed!: ', error);
  });
```
