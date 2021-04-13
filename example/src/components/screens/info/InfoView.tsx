import React, { Dispatch, SetStateAction, useEffect, useState } from 'react';
import {StyleSheet, Text, View} from 'react-native';
import Button from '../../general/Button';
import ContentContainer from '../dashboard/components/ContentContainer';
import AppColors from '../../constants/AppColors';
import OneginiSdk, { DEFAULT_RESOURCE_DETAILS, useImplicitResource, useResource } from 'react-native-sdk-beta';

interface Props {
  onFinished?: () => void;
}

const handleImplicitData = async (setProfileError: Dispatch<SetStateAction<any>>, profileId: string | null, setProfileId: Dispatch<SetStateAction<any>>) => {
  try {
    const profiles = await OneginiSdk.getUserProfiles();

    if (profiles[0]) {
      if(profileId !== profiles[0]?.profileId) {
        setProfileId(profiles[0].profileId);
      }
    } else {
      setProfileError('No profiles registered.')
    }
  } catch (e) {
    setProfileError(e);
  }
};

//@todo resolve this with more types for resources
const getData = (data: any, key: string) => {
  if(data[key]) {
    return data[key];
  } else {
    return `No data for key: ${key}`;
  }
}

const InfoView: React.FC<Props> = (props) => {
  const [profileError, setProfileError] = useState(null);
  const implicitResource = useImplicitResource(
    {...DEFAULT_RESOURCE_DETAILS, path: 'user-id-decorated'}
  );
  const resource = useResource(
    {...DEFAULT_RESOURCE_DETAILS, path: 'application-details'},
    true
  );

  useEffect(() => {
    handleImplicitData(setProfileError, implicitResource.profileId, implicitResource.setProfileId);
  }, []);

  return (
    <ContentContainer containerStyle={styles.container}>
      <View style={styles.row}>
        <Text style={styles.label}>User Info</Text>
        {implicitResource.loading && <Text style={styles.info}>{'Loading...'}</Text>}
        {profileError && <Text style={styles.infoError}>{'Error:  ' + profileError}</Text>}
        {implicitResource.error && <Text style={styles.infoError}>{'Implicit Error:  ' + implicitResource.error}</Text>}
        {implicitResource.profileId && <Text style={styles.info}>{"Profile id:  " + implicitResource.profileId}</Text>}
        {implicitResource.data && <Text style={styles.info}>{`Implicit details: ${getData(implicitResource.data, 'decorated_user_id')}`}</Text>}
      </View>
      <View style={styles.row}>
        <Text style={styles.label}>Device details</Text>
        {resource.loading && <Text style={styles.info}>{'Loading...'}</Text>}
        {resource.error && <Text style={styles.infoError}>{'Resource Error:  ' + resource.error}</Text>}
        {resource.data && (
          <>
            <Text style={styles.info}>{`Application ID: ${getData(resource.data, 'application_identifier')}`}</Text>
            <Text style={styles.info}>{`Application Version: ${getData(resource.data, 'application_platform')}`}</Text>
            <Text style={styles.info}>{`Platform: ${getData(resource.data, 'application_version')}`}</Text>
          </>
        )}
      </View>
      <View style={styles.cancelButton}>
        <Button name={"CANCEL"} onPress={props.onFinished}/>
      </View>
    </ContentContainer>
  );
};

const styles = StyleSheet.create({
  container: {
    paddingHorizontal: '6%',
    paddingTop: '4%',
  },
  row: {
    paddingHorizontal: '10%',
    justifyContent: 'flex-start',
    alignItems: 'flex-start'
  },
  label: {
    justifyContent: 'center',
    color: AppColors.blue,
    fontSize: 24,
    fontWeight: '500',
  },
  info: {
    justifyContent: 'center',
    color: AppColors.black,
    fontSize: 12,
    fontWeight: '500',
    padding: 6,
  },
  infoError: {
    justifyContent: 'center',
    color: AppColors.red,
    fontSize: 12,
    fontWeight: '500',
    padding: 6,
  },
  cancelButton: {
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 40,
  },
});

export default InfoView;
