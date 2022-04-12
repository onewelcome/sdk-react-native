import React, {createContext, useMemo, useReducer} from "react";
import {authReducer} from "./auth.reducer";

export interface AuthState {
    authorized: boolean;
    authenticated: { loading: boolean; profiles: { id: string; authenticated: boolean }[] | null };
}

const initialAuthState: AuthState = {
    authorized: false,
    authenticated: {
        loading: false,
        profiles: null,
    },
};

export const AuthContext = createContext<{ state: AuthState; dispatch: any }>({
    state: {...initialAuthState},
    dispatch: undefined,
});

interface AuthProviderProps {
    children: React.ReactNode;
}

export function AuthProvider({children}: AuthProviderProps) {
    const [state, dispatch] = useReducer(authReducer, initialAuthState, (state) => state);
    const value = useMemo(() => ({state, dispatch}), [state, dispatch]);
    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
