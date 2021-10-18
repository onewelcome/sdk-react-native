# useFingerprintFlow

 For easiest Fingerprint/FaceID flow implementation.

`useFingerprintFlow()`

| Returns | Type | Description |
| ------ | ------ | ----------- |
| active   | boolean   |  Defines wheather Fingerprint recognication process active or not |
| stage   | [FingerprintStage](FingerprintStage.md)   |  Current stage of Fingerprint flow |
| fallbackToPin   | () => void   |  Helper function to trigger fallback to PIN flow |
| cancelFlow   | () => void   |  Helper function to trigger cancelling Fingerprint flow |

**Example**
```
const {active, stage, fallbackToPin, cancelFlow} = useFingerprintFlow()
```
