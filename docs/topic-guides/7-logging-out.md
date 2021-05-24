# Logging out

## Logout a user

In the Onegini React Native plugin, a user is treated as logged in as long as the user has an access token for the operation to be executed. Therefore, to logout the user, the access token needs to be removed. This can be done by calling the `OneginiSdk.logout()` function. The plugin will remove the access token on the client, and also send a request to the Token Server to invalidate the token server side. 

**Example code to logout a user:**

```
OneginiSdk
    .logout()
    .then(() => console.log('Logout succeed!'))
    .catch(err => console.error('Logout failed: ', err))
```

If a refresh token is stored on the device, it will persist after the logout action. It can then be used to login again later.
