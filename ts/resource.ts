import {useState, useEffect, Dispatch, SetStateAction} from 'react';
import OneginiSdk from './index';
import {ResourceRequestType, ResourcesDetails} from './data-types';

const DefaultResourcesDetails: ResourcesDetails = {
  path: 'test',
  method: 'GET',
  parameters: {'custom-param1': 'p1', 'custom-param2': 'p2'},
  encoding: 'application/json',
  headers: {'custom-header1': 'val1', 'custom-header2': 'val2'},
};

//

const fetchResource = async (
  setLoading: Dispatch<SetStateAction<boolean>>,
  setError: Dispatch<SetStateAction<any>>,
  setData: Dispatch<SetStateAction<any>>,
  shouldAuthenticate: boolean,
  type: ResourceRequestType,
  resourceDetails: ResourcesDetails,
  scopes?: string[],
  profileId: string | null = null,
) => {
  // when type is ResourceRequestType.Implicit we require profileId
  if (type === ResourceRequestType.Implicit && !profileId) {
    return;
  }

  try {
    if (shouldAuthenticate) {
      if (type === ResourceRequestType.Implicit && profileId) {
        await OneginiSdk.authenticateUserImplicitly(profileId, scopes);
      } else if (type === ResourceRequestType.Anonymous) {
        await OneginiSdk.authenticateDeviceForResource(scopes);
      }
    }

    const data = await OneginiSdk.resourceRequest(type, resourceDetails);

    setData(data);
    setLoading(false);
  } catch (e) {
    console.error('fetchResource error = ', e);

    setError(e);
    setLoading(false);
  }
};

//

function useResources(
  type: ResourceRequestType,
  details: ResourcesDetails,
  shouldAuthenticate: boolean,
  scopes?: string[],
  profileId?: string | null,
) {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [data, setData] = useState(null);

  // get initial details - to prevent from rerender
  const [currentDetails, setCurrentDetails] = useState(details);

  // if details has changed
  useEffect(() => {
    if (JSON.stringify(details) !== JSON.stringify(currentDetails)) {
      setCurrentDetails(details);
    }
  }, [details, currentDetails]);

  useEffect(() => {
    fetchResource(
      setLoading,
      setError,
      setData,
      shouldAuthenticate,
      type,
      currentDetails,
      scopes,
      profileId,
    );
  }, [type, shouldAuthenticate, profileId, currentDetails]);

  return {
    loading,
    data,
    error,
  };
}

export {useResources, DefaultResourcesDetails};
