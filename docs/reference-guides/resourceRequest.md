
# resourceRequest

Resources can be fetched using user authentication, implicit authentication or anonymous authentication. Refer to the Secure resource access topic guide for more information about the differences between these authentication methods.
```
resourceRequest(
    type: ResourceRequestType,
    details: ResourcesDetails,
  ): Promise<any>;
```

| Property | Type | Description |
| ------ | ------ | ----------- |
| type   | ResourceRequestType   | Request type e.g. 'Anonymous' |
| details   | ResourcesDetails   | Request details e.g. method type ('GET') |

**Example**
```
OneginiSdk.resourceRequest(type, details)
  .then((data) => {
    console.log('Resources request succeed! ', data);
  })
  .catch(error => {
    console.log('Resources request failed!: ', error);
  });
```
