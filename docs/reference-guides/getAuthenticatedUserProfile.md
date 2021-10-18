# getAuthenticatedUserProfile

This method returns the currently authenticated user (profile). The method fails when no user is authenticated.


`getAuthenticatedUserProfile(): Promise<Profile>`

**Example**
```
OneginiSdk.getAuthenticatedUserProfile()
  .then((profile: UserProfile) => {
    console.log('Authenticated Profile succeed! ', profile)
  })
  .catch(error => {
    console.log('Authenticated Profile failed!: ', error.message)
  })
```

**Success**
| Property | Type | Description |
| ------ | ------ | ----------- |
| authenticatedProfile   | [Profile](Profile.md)   | Currently authenticated user profile |
