import React, {useState, useEffect} from 'react';
import {Modal} from 'react-native';
import {
  ONEGINI_PIN_NOTIFICATIONS,
  ONEGINI_PIN_FLOW,
} from 'react-native-sdk-beta';
import PinView from './PinView';
import PinManager from '../../managers/PinManager';
import ObjectIDHelper from '../../helpers/ObjectIdHelper';

const PinModal = (props) => {
  const [id, setId] = useState(ObjectIDHelper.getNewID('PinModal'));
  const [visible, setVisible] = useState(false);
  const [flow, setFlow] = useState(ONEGINI_PIN_FLOW.CREATE);

  useEffect(() => {
    PinManager.addEventListener(id, (event) => {
      switch (event.action) {
        case ONEGINI_PIN_NOTIFICATIONS.OPEN:
          console.log('PinModal OPEN');
          setFlow(event.flow);
          setVisible(true);
          break;
      }
    });

    return () => {
      PinManager.removeEventListener(id);
    };
  }, [id]);

  return (
    <Modal
      transparent={false}
      animationType="fade"
      visible={visible}
      onRequestClose={() => setVisible(false)}>
      <PinView flow={flow} setVisible={setVisible} />
    </Modal>
  );
};

export default PinModal;
