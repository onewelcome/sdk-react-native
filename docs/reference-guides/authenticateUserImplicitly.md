
# authenticateUserImplicitly

You can use implicit authentication to authenticate users based on their client credentials. This means you can assume the user has successfully completed the registration process in the past. After authenticating implicitly, you can fetch resources which require implicit authentication. Implicit authentication requires no user interaction like asking for their PIN or Fingerprint.



`authenticateUserImplicitly(profileId: string): Promise<Types.AuthData>`
| Property | Description |
| ------ | ----------- |
| profileId   | The profile ID you previously stored during registration |

**Example**
```
OneginiSdk.authenticateUserImplicitly(profileId)
  .then(authData => {
    console.log('User implicitl authentication succeed!');
  })
  .catch(error => {
    console.log('User implicitl authentication failed!: ', error);
  });
```
