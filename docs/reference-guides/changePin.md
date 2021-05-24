
# changePin

Once authenticated, a user is able to change their PIN.

**TODO** How to handle response

`changePin(): Promise<void>`

**Example**
```
OneginiSdk.changePin()
  .then(() => {
    console.log('Change Pin action succeed!');
  })
  .catch(error => {
    console.log('Change Pin action failed!: ', error);
  });
```
