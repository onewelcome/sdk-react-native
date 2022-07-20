import {useCallback} from "react";
import AsyncStorage from '@react-native-async-storage/async-storage';

const getKey = (profileId: string) => `@${profileId}_pinLength`

export const useProfileStorage = () => {

  const setPinProfile = useCallback(async (profileId: string, pinLength: number) => {
      try {
        await AsyncStorage.setItem(getKey(profileId), pinLength.toString());
      } catch (e){
      }
  }, []);

  const getPinProfile = useCallback(async (profileId: string): Promise<number> => {
      try {
        const value = await AsyncStorage.getItem(getKey(profileId));
        return value ? Number(value) : 5;
      } catch (e) {
        return 5;
      }
  }, []);

  return {setPinProfile, getPinProfile};
}