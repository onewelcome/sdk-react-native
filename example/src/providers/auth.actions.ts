export enum AuthActionTypes {
    AUTH_SET_AUTHORIZATION = '[Auth] Set Authorization',
    AUTH_LOAD_PROFILE_IDS = '[Auth] Load Profile Ids',
    AUTH_SET_PROFILE_IDS = '[Auth] Set Profile Ids',
}

interface IAuthAction<T extends AuthActionTypes, P> {
    type: T;
    payload: P;
}

export type AuthAction =
    | IAuthAction<AuthActionTypes.AUTH_SET_AUTHORIZATION, boolean>
    | IAuthAction<AuthActionTypes.AUTH_SET_PROFILE_IDS, string[]>
    | IAuthAction<AuthActionTypes.AUTH_LOAD_PROFILE_IDS, null>;
