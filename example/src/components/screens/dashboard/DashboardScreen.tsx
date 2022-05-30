import React, {useState} from 'react';
import {StyleSheet, View, Text, Image, Alert, ScrollView} from 'react-native';
import {Assets} from '../../../../assets';
import BackButton from '../../general/BackButton';
import SettingsActionsView from './components/SettingsActionsView';
import DashboardActionsView from './components/DashboardActionsView';
import ChangeAuthView from './components/ChangeAuthView';
import OtpCodeView from './components/OtpCodeView';
import OneWelcomeSdk, {Events} from 'onewelcome-react-native-sdk';
import DevicesView from '../devices/DevicesView';

interface Props {
  onLogout: () => void;
}

const DashboardScreen: React.FC<Props> = (props) => {
  const [contentView, setContentView] = useState(
    CONTENT_VIEW.DASHBOARD_ACTIONS,
  );

  function onShowAccessToken() {
    OneWelcomeSdk.getAccessToken()
      .then((token) => Alert.alert('Access Token', token))
      .catch(() => Alert.alert('Error!', 'Could not get AccessToken!'));
  }

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        {contentView === CONTENT_VIEW.DASHBOARD_ACTIONS ? (
          <Image style={styles.headerIcon} source={Assets.oneginiIcon} />
        ) : (
          <BackButton
            onPress={() => backButtonHandler(contentView, setContentView)}
          />
        )}
        <Text style={styles.headerTitle}>
          {TITLE_BY_CONTENT_VIEW.get(contentView)}
        </Text>
      </View>
      {renderContent(
        contentView,
        setContentView,
        props.onLogout,
        onShowAccessToken,
      )}
    </ScrollView>
  );
};

//
enum CONTENT_VIEW {
  DASHBOARD_ACTIONS = 'DASHBOARD_ACTIONS',
  SETTINGS_ACTIONS = 'SETTINGS_ACTIONS',
  CHANGE_AUTH = 'CHANGE_AUTH',
  OTP_CODE = 'OTP_CODE',
  DEVICES = 'DEVICES',
}

const TITLE_BY_CONTENT_VIEW: Map<CONTENT_VIEW, string> = new Map([
  [CONTENT_VIEW.DASHBOARD_ACTIONS, 'Example'],
  [CONTENT_VIEW.SETTINGS_ACTIONS, 'Settings'],
  [CONTENT_VIEW.CHANGE_AUTH, 'Change Auth'],
  [CONTENT_VIEW.DEVICES, 'Devices'],
]);

const backButtonHandler = (
  currentContentView: CONTENT_VIEW,
  setContentView: (contentView: CONTENT_VIEW) => void,
) => {
  switch (currentContentView) {
    case CONTENT_VIEW.SETTINGS_ACTIONS:
      setContentView(CONTENT_VIEW.DASHBOARD_ACTIONS);
      break;
    case CONTENT_VIEW.CHANGE_AUTH:
      setContentView(CONTENT_VIEW.SETTINGS_ACTIONS);
      break;
    case CONTENT_VIEW.OTP_CODE:
      setContentView(CONTENT_VIEW.DASHBOARD_ACTIONS);
      break;
    case CONTENT_VIEW.DEVICES:
      setContentView(CONTENT_VIEW.DASHBOARD_ACTIONS);
      break;
    default:
      console.log('Unsupported CONTENT_VIEW for [BackButton]');
      break;
  }
};

const renderContent = (
  currentContentView: CONTENT_VIEW,
  setContentView: (contentView: CONTENT_VIEW) => void,
  onLogout: () => void,
  onShowAccessToken?: () => void,
) => {
  switch (currentContentView) {
    case CONTENT_VIEW.DASHBOARD_ACTIONS:
      return (
        <DashboardActionsView
          onLogout={onLogout}
          onSettingsPressed={() =>
            setContentView(CONTENT_VIEW.SETTINGS_ACTIONS)
          }
          onMobileAuthWithOTPPressed={() =>
            setContentView(CONTENT_VIEW.OTP_CODE)
          }
          onYourDevicesPressed={() => setContentView(CONTENT_VIEW.DEVICES)}
          onAccessTokenPressed={onShowAccessToken}
        />
      );
    case CONTENT_VIEW.SETTINGS_ACTIONS:
      return (
        <SettingsActionsView
          onChangeAuthPressed={() => setContentView(CONTENT_VIEW.CHANGE_AUTH)}
          onChangePinPressed={() =>
            OneWelcomeSdk.submitPinAction(
              Events.PinFlow.Change,
              Events.PinAction.Cancel, // ONEGINI_PIN_ACTIONS.CHANGE - why it was CHANGE here? there is no such action
              null,
            )
          }
        />
      );
    case CONTENT_VIEW.CHANGE_AUTH:
      return <ChangeAuthView
                onLogout={onLogout}
             />;
    case CONTENT_VIEW.OTP_CODE:
      return <OtpCodeView />;
    case CONTENT_VIEW.DEVICES:
      return <DevicesView />;
    default:
      console.log('Unsupported CONTENT_VIEW for [renderContent]');
      break;
  }
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f8f8f8',
  },
  header: {
    position: 'absolute',
    height: '8%',
    top: 0,
    right: 0,
    left: 0,
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#5f48dd',
  },
  headerTitle: {
    color: '#ffffff',
    fontSize: 22,
    fontWeight: '400',
  },
  headerIcon: {
    position: 'absolute',
    left: '1%',
    height: '80%',
    resizeMode: 'contain',
  },
});

export default DashboardScreen;
