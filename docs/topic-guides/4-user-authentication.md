# User authentication

## Introduction

The React Native Plugin allows for user authentication with either a registered authenticator or with a pin. Both cases are based on the same method, however a different value for `registeredAuthenticatorId` is required.


#TODO: Registering a user


#TODO: getUserProfiles


## Authenticate a registered user

Once a user has been registered, it can be logged in using the `Onegini.instance.userClient.authenticateUser(BuildContext context, String? registeredAuthenticatorId)` method. This method takes the id of a registered authenticator and uses it to authenticate the user. The **id** param can also be `null` in which case the default authenticator (**PIN**) will be used.

```
OneginiSdk.authenticateUser(profileId)
  .then(authData => {
    console.log('User authentication succeed!');
  })
  .catch(error => {
    console.log('User authentication failed!: ', error);
  });
```
