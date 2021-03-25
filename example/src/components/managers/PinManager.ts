// Not used?

import React from 'react';

/*

class PinManager {
  listeners = [];

  observer = (cb) => {
    console.log('PinManager observer ' + cb);
    this.listeners.forEach((element) => {
      console.log('PinManager observer ' + cb);
      element(cb);
    });
  };

  addEventListener = (id, cb) => {
    console.log('PinManager addEventListener id ' + id);
    if (this.listeners.length === 0) {
      OneginiSdk.addEventListener(
        ONEGINI_SDK_EVENTS.ONEGINI_PIN_NOTIFICATION,
        this.observer,
      );
    }

    if (this.listeners[id]) {
      this.removeEventListener(id);
    }

    this.listeners.push(cb);
  };

  removeEventListener = (id) => {
    console.log('PinManager removeEventListener id ' + id);

    console.log(this.listeners[id]);
    this.listeners[id] = null;

    console.log(this.listeners);

    this.listeners.forEach((element) => {
      console.log(element);
    });
    if (this.listeners.length === 0) {
      OneginiSdk.removeEventListener(OneginiSdk.ONEGINI_PIN_NOTIFICATIONS);
    }
  };
}

export default new PinManager();
*/
