import {AuthState} from "./auth.provider";
import {AuthAction, AuthActionTypes} from "./auth.actions";

export const authReducer = (state: AuthState, action: AuthAction): AuthState => {
    switch (action?.type) {
        case AuthActionTypes.AUTH_SET_AUTHORIZATION: {
            return {
                ...state,
                authorized: action.payload
            };
        }
        case AuthActionTypes.AUTH_LOAD_PROFILE_IDS: {
            return {
                ...state,
                authenticated: {
                    ...state.authenticated,
                    loading: true
                }
            };
        }
        case AuthActionTypes.AUTH_SET_PROFILE_IDS: {
            return {
                ...state,
                authenticated: {
                    loading: false,
                    profiles: action.payload.map(id => ({authenticated: false, id}))
                }
            };
        }

        default:
            return state;
    }
}