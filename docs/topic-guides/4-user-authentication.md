# User authentication

## Introduction

The Flutter Plugin allows for user authentication with either a registered authenticator or with a pin. Both cases are based on the same method, however a different value for `registeredAuthenticatorId` is required.

#TODO: getUserProfiles
#TODO: Registering a user

#TODO: This should be in 5-user-authentication-with-biometric.md ? 
## Getting registered authenticators

To select an registered authenticator which will be used during the authentication process you need to pass its id in the place of `registeredAuthenticatorId` param. To choose a registered authenticator, first you need to get currently registered authenticators. To get those you need to call the method Onegini.instance.userClient.getRegisteredAuthenticators(context).

    var authenticators = await Onegini.instance.userClient.getRegisteredAuthenticators(context)

If there is no registered authenticator, an empty list will be returned.

## Authenticate a registered user

Once a user has been registered, it can be logged in using the `Onegini.instance.userClient.authenticateUser(BuildContext context, String? registeredAuthenticatorId)` method. This method takes the id of a registered authenticator and uses it to authenticate the user. The **id** param can also be `null` in which case the default authenticator (**PIN**) will be used.

    var userId = await Onegini.instance.userClient
        .authenticateUser(context, registeredAuthenticatorId)
        .catchError((error) {
            print("Authentication failed: " + error.message);
        });

    if (userId != null) {
        print("Authentication success!");
    }

The result of successful authentication is the string value of `userId`.
