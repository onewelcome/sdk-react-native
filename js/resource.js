import { useState, useEffect } from "react";
import OneWelcomeSDK  from "./index";

const DEFAULT_RESOURCE_DETAILS = {
  path: 'test',
  method: 'GET',
  parameters: {"custom-param1" : "p1", "custom-param2" : "p2"},
  encoding: 'application/json',
  headers: {"custom-header1" : "val1", "custom-header2" : "val2"},
};

const fetchResource = async (setLoading, setError, setData, shouldAuthenticate, isImplicit, resourceDetails, scopes, profileId = null) => {
  try {
    if (shouldAuthenticate) {
      isImplicit
        ? await OneWelcomeSDK.authenticateUserImplicitly(profileId, scopes)
        : await OneWelcomeSDK.authenticateDeviceForResource(scopes);
    }
    const data = await OneWelcomeSDK.resourceRequest(isImplicit, resourceDetails);

    console.log("FETCH DATA = ", data)
    
    setData(data);
    setLoading(false);
  } catch (e) {
    setError(e);
    setLoading(false);
  }
};

const useResource = (resourceDetails = DEFAULT_RESOURCE_DETAILS, shouldAuthenticate = false, scopes = null) => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [data, setData] = useState(null);

  useEffect(() => {
    fetchResource(setLoading, setError, setData, shouldAuthenticate, false, resourceDetails, scopes);
  }, []);

  return [
    loading,
    data,
    error
  ]
};

const useImplicitResource = (resourceDetails = DEFAULT_RESOURCE_DETAILS, shouldAuthenticate = true, scopes = null) => {
  const [profileId, setProfileId] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [data, setData] = useState(null);

  useEffect(() => {
    if(profileId) {
      fetchResource(setLoading, setError, setData, shouldAuthenticate, true, resourceDetails, scopes, profileId);
    }
  }, [profileId]);

  return [
    loading,
    data,
    error,
    profileId,
    setProfileId,
  ]
}

export { useResource, useImplicitResource, DEFAULT_RESOURCE_DETAILS }
