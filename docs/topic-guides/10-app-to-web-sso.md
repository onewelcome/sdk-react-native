# App To Web Single Sign On

## App To Web Single Sign On

App to Web Single Sign On (SSO) allows you to take a session from your mobile application and extend it to a browser on the same device. This is useful for giving a seamless experience to your users when they transition from the mobile application to the website where more functionality likely exists. This functionality can only be used when using the [Onegini CIM identity provider](https://docs-single-tenant.onegini.com/msp/stable/token-server/topics/general-app-config/identity-providers/identity-providers.html#configure-a-onegini-cim-identity-provider) as it is a unique feature of the [Onegini Consumer Identity Manager](https://docs-single-tenant.onegini.com/cim/idp/7.35.0/). This can be configured in the [Onegini Token Server Admin](https://docs-single-tenant.onegini.com/msp/stable/token-server/topics/general-app-config/identity-providers/identity-providers.html#configure-a-onegini-cim-identity-provider).

The Onegini React Native plugin allow you to specify a target URL where authentication is required. This URL must be configured in the [Action Token](https://docs-single-tenant.onegini.com/cim/idp/7.35.0/topic-guides/authentication/action-token-login.html) configuration of the Onegini Consumer Identity Manager. It will then verify that your mobile application's session is valid and establish a session with the Identity provider before redirecting the user to the target URL with them automatically logged in.

To use the functionality, call the `OneginiSdk.startSingleSignOn()` with the target URI and wait for the result. In case of a success, the `SingleSignOnData` data object will be returned.

**Example code to use app to web sso:**

```
const exampleUrl = "https://login-mobile.test.onegini.com/personal/dashboard"

OneginiSdk.startSingleSignOn(exampleUrl)
    .then((it) => {
      // open external browser
      Linking.openURL(it.url)
    })
    .catch((error) => {
      Alert.alert('Error', error)
    })
```
