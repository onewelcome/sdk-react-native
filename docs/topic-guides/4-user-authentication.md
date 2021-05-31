# User authentication

The OAuth 2.0 protocol begins with registration. The [registerUser](../reference-guides/registerUser.md) function can be used to register a user. This function can take an array of scopes that authentication is requested for as argument. If no scopes are requested, the default scopes of the application will be used.

When registering a user, the React Native Plugin redirects the user to the authentication endpoint on the Token Server via the browser. Once the client credentials have been validated, and an authorization grant has been issued, the user will be redirected to the app. Based on this authorization grant, the client will request an access token for the specified set of scopes. If the grant includes a refresh token, the user will need to create a PIN.

## Get registered profiles

While it is possible to keep track of registered users oneself, the React Native Plugin also provides a function to retrieve all registered profiles [getUserProfiles](../reference-guides/getUserProfiles.md).
```js
OneginiSdk.getUserProfiles()
  .then(profiles => {
    console.log('User profiles registered on this device: ', profiles)
  })
  .catch(error => {
    console.error('Error:', error.message)
  })
```


## Authenticate a registered user

Once a user has been registered, it can be logged in using the [authenticateUser](../reference-guides/authenticateUser.md) method. This method takes the profileId of already registered user.

```js
OneginiSdk.authenticateUser(profileId)
  .then(authData => {
    console.log('User authentication succeed!')
  })
  .catch(error => {
    console.log('User authentication failed!: ', error.message)
  })
```
