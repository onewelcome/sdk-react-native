# User registration

User registration is a fundamental part of the Onegini Mobile Security Platform. As developer you have a couple options to handle this process.


## 1) Start
To start the user registration using WebView or browser you have to call [registerUser](../reference-guides/registerUser.md) method. 

```ts
OneginiSdk.registerUser(providerId)
    .then(profile => {
        console.log('Registration success! ', profile)
    })
    .catch(err => {
        console.error('Registration failed: ', err)
    })
```

Resolve will be caled when the whole flow is successfully finished.

## 2) Listen for URL
Then listen for registration event with [addEventListener](../reference-guides/addEventListener.md). _(this step is not yet available)_

```ts
useEffect(() => {
    const listener = OneginiSdk.addEventListener(Events.SdkNotification.RegistrationProcessUrl, (event: any) => {
        // OneginiSdk.handleRegisteredProcessUrl....
    })

    return () => {
        listener.remove()
    }
}, [])
```

You have to also listen to PIN event [SdkNotification](../reference-guides/SdkNotification.md) because PIN may be required to finish registration process. Please refer to [usePinFlow](../reference-guides/usePinFlow.md).


## 3) Process URL

#### Register by internal browser

The easiest way is to use internal browser like WebView/Android or ASWebAuthenticationSession/iOS. Onegini React Native plugin provides the whole implementation with [handleRegisteredProcessUrl](../reference-guides/handleRegisteredProcessUrl.md).

```ts
OneginiSdk.handleRegisteredProcessUrl(event.url, BrowserType.Internal)
```
_(this method is not yet available)_

#### Register by default browser

If you want to use external browser (and have full control over flow and url) you have to pass [BrowserType](../reference-guides/BrowserType.md).External.

```ts
OneginiSdk.handleRegisteredProcessUrl(event.url, BrowserType.External)
```
_(this method is not yet available)_

Calling this method will launch a browser where you need to register. If registration is successful, the browser will return a link that can be caught (e.g. with React Native [Linking](https://reactnative.dev/docs/linking)).

## 4) Handle URL

Then you have to call [handleRegistrationCallback](../reference-guides/handleRegistrationCallback.md) to finalize registration.

```ts
useEffect(() => {
    const handleOpenURL = (event: any) => {
        if (event.url.substr(0, event.url.indexOf(':')) === linkUri) {
            OneginiSdk.handleRegistrationCallback(event.url)
        }
    }

    if (linkUri) {
        Linking.addListener('url', handleOpenURL)
    } else {
        getLinkUri()
    }

    return () => Linking.removeListener('url', handleOpenURL)
}, [linkUri])
```

## 5) Finish

If everything went properly [registerUser](../reference-guides/registerUser.md) will resolve with [Profile](../reference-guides/Profile.md) object.


## Choosing an identity provider

To select an identity provider which will be used during the registration process you need to pass its id in the place of the `identityProviderId` param. To choose an identity provider, first you need to get all available providers. Call the method [getRegisteredAuthenticators](../reference-guides/getRegisteredAuthenticators) to get a list with available providers id. If this parameter isn’t specified or if its value is `null` the default identity provider set on the **Token Server** will be used.
