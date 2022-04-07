import React, {useEffect, useState} from 'react';
import SplashScreen from '../splash/SplashScreen';
import DashboardScreen from '../dashboard/DashboardScreen';
import AuthScreen from '../auth/AuthScreen';
import OneginiSdk from "onegini-react-native-sdk";


const HomeScreen: React.FC<{}> = () => {
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
