# handleRegistrationCallback

Used to manually handle redirect URI during registration. Developer can change it here if needed. The easiest way to get this url is to use React Native [Linking](https://reactnative.dev/docs/linking)

`handleRegistrationCallback(uri: string)`
| Property | Type | Description |
| ------ | ------ | ----------- |
| uri   | string   | Registration URI |

**Example**
```
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
