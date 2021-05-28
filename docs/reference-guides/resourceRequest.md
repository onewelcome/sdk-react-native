# resourceRequest

Resources can be fetched using user authentication, implicit authentication or anonymous authentication. Refer to the Secure resource access topic guide for more information about the differences between these authentication methods.
```
resourceRequest(
    type: ResourceRequestType,
    details: ResourcesDetails,
  ): Promise<any>
```

| Property | Type | Description |
| ------ | ------ | ----------- |
| type   | #ResourceRequestType   | Request type 'User', 'ImplicitUser', 'Anonymous' |
| details   | #ResourcesDetails   | Request details e.g. method type ('GET') |

**Example**
```
const details = {
  path: 'user-id-decorated',
  method: 'GET'
}

OneginiSdk.resourceRequest('Anonymous', details)
  .then((data) => {
    console.log('Resources request succeed! ', data)
  })
  .catch(error => {
    console.log('Resources request failed!: ', error.message)
  })
```


**Success**
| Property | Example | Description |
| ------ | ------ |  ----------- |
| data   |   {"application_identifier": "RNExampleApp", "application_platform": "android", "application_version": "0.1.0"}  | Fetched data e.g. JSON |

**Error**
| Property | Example | Description |
| ------ | ------ |  ----------- |
| code   | 8000   | The error code |
| message   | "Onegini: Internal plugin error"   | Human readable error description |


