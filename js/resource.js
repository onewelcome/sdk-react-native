import { useState, useEffect } from "react";
import OneginiSdk  from "./index";

const DEFAULT_RESOURCE_DETAILS = {
  path: 'test',
  method: 'GET',
  parameters: null,
  encoding: 'application/json',
  headers: null,
};

const fetchResource = async (setLoading, setError, setData, shouldAuthenticate, isImplicit, resourceDetails, profileId = null) => {
  try {
    if (shouldAuthenticate) {
      isImplicit
        ? await OneginiSdk.authenticateUserImplicitly(profileId)
        : await OneginiSdk.authenticateDeviceForResource(resourceDetails.path);
    }
    const data = await OneginiSdk.resourceRequest(isImplicit, resourceDetails);

    setData(data);
    setLoading(false);
  } catch (e) {
    setError(e);
    setLoading(false);
  }
};

const useResource = (resourceDetails = DEFAULT_RESOURCE_DETAILS, shouldAuthenticate = false) => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [data, setData] = useState(null);

  useEffect(() => {
    fetchResource(setLoading, setError, setData, shouldAuthenticate, false, resourceDetails);
  }, []);

  return [
    loading,
    data,
    error
  ]
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

  return [
    loading,
    data,
    error,
    profileId,
    setProfileId,
  ]
}

export { useResource, useImplicitResource, DEFAULT_RESOURCE_DETAILS }
