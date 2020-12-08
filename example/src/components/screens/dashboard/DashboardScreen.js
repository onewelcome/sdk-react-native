import React, {useState} from 'react';
import {StyleSheet, View, Text, Image} from 'react-native';
import PropTypes from 'prop-types';
import {Assets} from '../../../../assets';
import BackButton from '../../general/BackButton';
import SettingsActionsView from './components/SettingsActionsView';
import DashboardActionsView from './components/DashboardActionsView';
import ChangeAuthView from './components/ChangeAuthView';
import OneginiSdk, {
  ONEGINI_PIN_FLOW,
  ONEGINI_PIN_ACTIONS,
} from 'react-native-sdk-beta';

const CONTENT_VIEW = {
  DASHBOARD_ACTIONS: 'DASHBOARD_ACTIONS',
  SETTINGS_ACTIONS: 'SETTINGS_ACTIONS',
  CHANGE_AUTH: 'CHANGE_AUTH',
};

const TITLE_BY_CONTENT_VIEW = {
  [CONTENT_VIEW.DASHBOARD_ACTIONS]: 'Example App',
  [CONTENT_VIEW.SETTINGS_ACTIONS]: 'Settings',
  [CONTENT_VIEW.CHANGE_AUTH]: 'Change Auth',
};

const backButtonHandler = (currentContentView, setContentView) => {
  switch (currentContentView) {
    case CONTENT_VIEW.SETTINGS_ACTIONS:
      setContentView(CONTENT_VIEW.DASHBOARD_ACTIONS);
      break;
    case CONTENT_VIEW.CHANGE_AUTH:
      setContentView(CONTENT_VIEW.SETTINGS_ACTIONS);
      break;
    default:
      console.log('Unsupported CONTENT_VIEW for [BackButton]');
      break;
  }
};

const renderContent = (currentContentView, setContentView, onLogout) => {
  switch (currentContentView) {
    case CONTENT_VIEW.DASHBOARD_ACTIONS:
      return (
        <DashboardActionsView
          onLogout={onLogout}
          onSettingsPressed={() =>
            setContentView(CONTENT_VIEW.SETTINGS_ACTIONS)
          }
        />
      );
    case CONTENT_VIEW.SETTINGS_ACTIONS:
      return (
        <SettingsActionsView
          onChangeAuthPressed={() => setContentView(CONTENT_VIEW.CHANGE_AUTH)}
          onChangePinPressed={() =>
            OneginiSdk.submitPinAction(
              ONEGINI_PIN_FLOW.CHANGE,
              ONEGINI_PIN_ACTIONS.CHANGE,
              null,
            )
          }
        />
      );
    case CONTENT_VIEW.CHANGE_AUTH:
      return <ChangeAuthView />;
    default:
      console.log('Unsupported CONTENT_VIEW for [renderContent]');
      break;
  }
};

const DashboardScreen = (props) => {
  const [contentView, setContentView] = useState(
    CONTENT_VIEW.DASHBOARD_ACTIONS,
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        {contentView === CONTENT_VIEW.DASHBOARD_ACTIONS ? (
          <Image style={styles.headerIcon} source={Assets.oneginiIcon} />
        ) : (
          <BackButton
            onPress={() => backButtonHandler(contentView, setContentView)}
          />
        )}
        <Text style={styles.headerTitle}>
          {TITLE_BY_CONTENT_VIEW[contentView]}
        </Text>
      </View>
      {renderContent(contentView, setContentView, props.onLogout)}
    </View>
  );
};

DashboardScreen.propTypes = {
  onLogout: PropTypes.func.isRequired,
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f8f8f8',
    paddingHorizontal: '4%',
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
