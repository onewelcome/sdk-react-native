import React, {useState} from 'react';
import SplashScreen from '../splash/SplashScreen';
import DashboardScreen from '../dashboard/DashboardScreen';
import AuthScreen from '../auth/AuthScreen';

const HomeScreen = ({props, navigation}) => {
  const [isAuthorized, setAuthorized] = useState(false);
  const [isBuilt, setBuilt] = useState(false);

  return isBuilt ? (
    isAuthorized ? (
      <DashboardScreen onLogout={() => setAuthorized(false)} />
    ) : (
      <AuthScreen onAuthorized={() => setAuthorized(true)} />
    )
  ) : (
    <SplashScreen onSdkStarted={() => setBuilt(true)} />
  );
};

export default HomeScreen;
