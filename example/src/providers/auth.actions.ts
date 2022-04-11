export enum AuthActionTypes {
    AUTH_SET_AUTHORIZATION = '[Auth] Set Authorization',
}

interface IAuthAction<T extends AuthActionTypes, P> {
    type: T;
    payload: P;
}

export type AuthAction =
    | IAuthAction<AuthActionTypes.AUTH_SET_AUTHORIZATION, boolean>;
