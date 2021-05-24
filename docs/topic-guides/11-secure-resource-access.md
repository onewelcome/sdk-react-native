# Secure resource access

There are three types of authentication tokens your app can use to securely access resources like account information, transactions, etc. from a back-end or resource server. A user or device needs to be authenticated before a resource can be fetched with the corresponding authentication token. Different resources might require using different methods for authentication. The following authentication token types can be used:

1\. **User authentication (default)**: Requires the user to be [fully authenticated](../reference-guides/authenticateUser.md), meaning to be authenticated with an authenticator (PIN or Fingerprint for example).

2\. **Implicit authentication**: Requires the user to be [authenticated](../reference-guides/authenticateUser.md), meaning the user has registered with the device before, this does not require interaction with an authenticator like PIN or Fingerprint.

3\. **Anonymous authentication**: Requires the device to be registered and authenticated with the Token Server, but no user has to be authenticated in any way. The Onegini Flutter plugin take care of it.

The Onegini Flutter plugin exposes the `getResource`, `getResourceImplicity` and `getResourceAnonymous` functions to perform these types of resource calls within `Onegini.instance.resourcesMethods`. The plugin ensures the confidentiality and authenticity of the payload. The application itself is responsible for the structure and/or processing of the payload.


**TODO**: useResources
**TODO**: await OneginiSdk.resourceRequest(type, resourceDetails)
**TODO**: authenticateDeviceForResource

## Using fetch with user authentication

In order to successfully request a resource for a specific user, the client credentials must be valid and the user must have a valid access token. In other words, the user must be logged in before a resource call can be made on their behalf. This type of resource request should be used to fetch sensitive data that requires user authentication, like account details and transaction history. After [authentication](./4-user-authentication.md), a resource can be fetched as follows:

    var response = await Onegini.instance.resourcesMethods.getResource(path);

## Using fetch with implicit authentication

Before fetching an implicit resource, the user must be [authenticated](../reference-guides/authenticateUser.md).

    var response = await Onegini.instance.resourcesMethods.getResourceImplicity(path, scope: scope);

## Using fetch with anonymous authentication

A device can use its OAuth credentials to authenticate itself with the Token Server, and obtain an access token. An anonymous resource call can be used in cases where a user does not need to be logged in or even registered in order to use certain functionality, or access some resource.

    var response = await Onegini.instance.resourcesMethods.getResourceAnonymous(path, scope: scope);

