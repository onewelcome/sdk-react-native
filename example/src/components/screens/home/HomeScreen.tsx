import React, {useEffect, useState} from 'react';
import SplashScreen from '../splash/SplashScreen';
import DashboardScreen from '../dashboard/DashboardScreen';
import AuthScreen from '../auth/AuthScreen';


const HomeScreen: React.FC<{}> = () => {
  const [isAuthorized, setAuthorized] = useState(false);
  const [isBuilt, setBuilt] = useState(false);
  const [isSdkError, setSdkError] = useState(false);

  return isBuilt || isSdkError ? (
    isAuthorized ? (
      <DashboardScreen onLogout={() => setAuthorized(false)} />
    ) : (
      <AuthScreen onAuthorized={() => setAuthorized(true)} />
    )
  ) : (
    <SplashScreen onSdkStarted={() => setBuilt(true)} onSdkError={() => setSdkError(true)} />
  );
};

export default HomeScreen;
