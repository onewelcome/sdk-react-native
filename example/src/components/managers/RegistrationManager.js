import OneginiSdk, {ONEGINI_SDK_EVENTS} from 'react-native-sdk-beta';

class RegistrationManager {
  listeners = [];

  observer = (cb) => {
    console.log('RegistrationManager observer ' + cb);
    this.listeners.forEach((element) => {
      console.log('RegistrationManager observer ' + cb);
      element(cb);
    });
  };

  addEventListener = (id, cb) => {
    console.log('RegistrationManager addEventListener id ' + id);
    if (this.listeners.length === 0) {
      OneginiSdk.addEventListener(
        ONEGINI_SDK_EVENTS.ONEGINI_CUSTOM_REGISTRATION_NOTIFICATION,
        this.observer,
      );
    }

    if (this.listeners[id]) {
      this.removeEventListener(id);
    }

    this.listeners.push(cb);
  };

  removeEventListener = (id) => {
    console.log('RegistrationManager removeEventListener id ' + id);

    console.log(this.listeners[id]);
    this.listeners[id] = null;

    console.log(this.listeners);

    this.listeners.forEach((element) => {
      console.log(element);
    });
    if (this.listeners.length === 0) {
      OneginiSdk.removeEventListener(
        ONEGINI_SDK_EVENTS.ONEGINI_CUSTOM_REGISTRATION_NOTIFICATION,
      );
    }
  };
}

export default new RegistrationManager();
