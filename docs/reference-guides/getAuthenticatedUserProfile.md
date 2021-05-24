
# getAuthenticatedUserProfile

Returns authenticated user profile.

`getAuthenticatedUserProfile(): Promise<Types.Profile>`

**Example**
```
OneginiSdk.getAuthenticatedUserProfile()
  .then((profile: UserProfile) => {
    console.log('Authenticated Profile succeed! ', profile);
  })
  .catch(error => {
    console.log('Authenticated Profile failed!: ', error);
  });
```

**Returns**
| Property | Type | Description |
| ------ | ------ | ----------- |
| authenticatedProfile   | Profile   | Currently authenticated user profile |
