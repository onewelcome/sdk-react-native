
# handleRegistrationCallback

Used to manually handle redirect URI during registration. Developer can change it here if needed.

`handleRegistrationCallback(uri: string): Promise<void>`
| Property | Type | Description |
| ------ | ------ | ----------- |
| uri   | string   | Registration URI |

**Example**
```
useEffect(() => {
    const handleOpenURL = (event: any) => {
      if (event.url.substr(0, event.url.indexOf(':')) === linkUri) {
        OneginiSdk.handleRegistrationCallback(event.url);
      }
    };
    
    const getLinkUri = async () => {
      let uri = await AsyncStorage.getItem('@redirectUri');
      setLinkUri(uri);
    };
    
    if (linkUri) {
      Linking.addListener('url', handleOpenURL);
    } else {
      getLinkUri();
    }
    
    return () => Linking.removeListener('url', handleOpenURL);
}, [linkUri]);
```
