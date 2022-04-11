import {AuthState} from "./auth.provider";
import {AuthAction, AuthActionTypes} from "./auth.actions";

export const authReducer = (state: AuthState, action: AuthAction): AuthState => {
    switch (action?.type) {
        case AuthActionTypes.AUTH_SET_AUTHORIZATION: {
            console.log(action);
            return {
                ...state,
                authorized: action.payload
            };
        }

        default:
            return state;
    }
}