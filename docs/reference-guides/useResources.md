
# useResources

 For easiest PIN flow implementation.

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
      ...DefaultResourcesDetails,
      path: 'user-id-decorated',
    },
    true,
    profileId,
  )
```
