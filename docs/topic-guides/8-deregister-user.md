# Deregister user

## Deregistering a user

Deregistering a user implies the removal of all of their data (including access and refresh tokens) from the device. It also includes a request to the Token Server to revoke all tokens associated with the user. The client credentials will remain stored on the device.

The Onegini React Native plugin exposes the [deregisterUser](../reference-guides/deregisterUser.md) function to properly deregister a user, as described above.

**Example code to deregister a user:**

```
OneginiSdk.deregisterUser(profileId)
    .then(() => console.log('Deregister succeed!'))
    .catch(error => console.error('Deregister failed: ', error.message))
```

Note that any existing user can be deregistered. They do not necessarily have to be logged in.
