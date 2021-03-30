import { useState, useEffect, Dispatch, SetStateAction } from 'react';
import OneginiSdk  from "./index";

const DEFAULT_RESOURCE_DETAILS = {
  path: 'test',
  method: 'GET',
  parameters: {"custom-param1" : "p1", "custom-param2" : "p2"},
  encoding: 'application/json',
  headers: {"custom-header1" : "val1", "custom-header2" : "val2"},
};

const fetchResource = async (
  setLoading: Dispatch<SetStateAction<boolean>>,
  setError: Dispatch<SetStateAction<any>>,
  setData: Dispatch<SetStateAction<any>>,
  shouldAuthenticate: boolean,
  isImplicit: boolean,
  resourceDetails: any,
  profileId: string | null = null
) => {
  try {
    if (shouldAuthenticate && profileId) {
      isImplicit
        ? await OneginiSdk.authenticateUserImplicitly(profileId)
        : await OneginiSdk.authenticateDeviceForResource(resourceDetails.path);
    }
    const data = await OneginiSdk.resourceRequest(isImplicit, resourceDetails);

    console.log("FETCH DATA = ", data)

    setData(data);
    setLoading(false);
  } catch (e) {
    setError(e);
    setLoading(false);
  }
};

const useResource = (resourceDetails = DEFAULT_RESOURCE_DETAILS, shouldAuthenticate = false) => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [data, setData] = useState(null);

  useEffect(() => {
    fetchResource(setLoading, setError, setData, shouldAuthenticate, false, resourceDetails);
  }, []);

  return {
    loading,
    data,
    error
  }
};

const useImplicitResource = (resourceDetails = DEFAULT_RESOURCE_DETAILS, shouldAuthenticate = true) => {
  const [profileId, setProfileId] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [data, setData] = useState(null);

  useEffect(() => {
    if(profileId) {
      fetchResource(setLoading, setError, setData, shouldAuthenticate, true, resourceDetails, profileId);
    }
  }, [profileId]);

  return {
    loading,
    data,
    error,
    profileId,
    setProfileId,
  }
}

export { useResource, useImplicitResource, DEFAULT_RESOURCE_DETAILS }
