import React from 'react';

//
// is it just a wrapper for OneginiSdk addEventListener/removeEventListener ??
// Not used anymore
//

/*
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
        Events.SdkNotification.CustomRegistration,
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
      OneginiSdk.removeEventListener(Events.SdkNotification.CustomRegistration);
    }
  };
}

export default new RegistrationManager();
*/
