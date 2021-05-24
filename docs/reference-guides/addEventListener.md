
# addEventListener

New listener for specified event can be added with this function.

`addEventListener(eventType: string, callback?: (event: any) => void): EmitterSubscription`

| Property | Type | Description |
| ------ | ------ | ----------- |
| eventType   | string   | Name of the event |
| callback   | (event: any) => void   | Callback function |

**Example**
```
const handleNotification = useCallback(
    (event: any) => {
        console.log('handle PIN notification event: ', event);

        switch (event.action) {
        // ..
        }
    },
    []
);
  
useEffect(() => {
    const listener = OneginiSdk.addEventListener(
      Events.SdkNotification.Pin,
      handleNotification,
    );
    
    return () => {
      listener.remove();
    };
}, [handleNotification]);
```
