import React, {useContext, useEffect, useState} from 'react';
import SplashScreen from '../splash/SplashScreen';
import DashboardScreen from '../dashboard/DashboardScreen';
import AuthScreen from '../auth/AuthScreen';
import {AuthContext} from "../../../providers/auth.provider";
import {AuthActionTypes} from "../../../providers/auth.actions";


const HomeScreen: React.FC<{}> = () => {
  const {
    state: {
      authorized: isAuthorized,
    },
    dispatch,
  } = useContext(AuthContext);
  const [isBuilt, setBuilt] = useState(false);
  const [isSdkError, setSdkError] = useState(false);

  return isBuilt || isSdkError ? (
    isAuthorized ? (
      <DashboardScreen onLogout={() => dispatch({type: AuthActionTypes.AUTH_SET_AUTHORIZATION, payload: false})} />
    ) : (
      <AuthScreen onAuthorized={() => dispatch({type: AuthActionTypes.AUTH_SET_AUTHORIZATION, payload: true})} />
    )
  ) : (
    <SplashScreen onSdkStarted={() => setBuilt(true)} onSdkError={() => setSdkError(true)} />
  );
};

export default HomeScreen;
