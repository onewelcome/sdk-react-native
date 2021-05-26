# useResources

 For easier resources fetching.

```
function useResources(
  type: ResourceRequestType,
  details: ResourcesDetails,
  shouldAuthenticate: boolean,
  profileId?: string | null,
) 
```

| Returns | Type | Description |
| ------ | ------ | ----------- |
| loading   | boolean   |  Are resources begin loaded |
| error   | string   |  Error |
| data   | JSON   |  Data response. |

**Example**
```
const implicitResource = useResources(
    Types.ResourceRequestType.Implicit,
    {
      path: 'user-id-decorated',
      method: 'GET'
    },
    true,
    profileId,
  )
```

**Returns**
| Property | Example | Description |
| ------ | ------ |  ----------- |
| loading   | true   |  Are resources begin loaded |
| data   |   {"application_identifier": "RNExampleApp", "application_platform": "android", "application_version": "0.1.0"}  | Fetched data e.g. JSON |
| error   | "Onegini: Internal plugin error"   | Human readable error description |



