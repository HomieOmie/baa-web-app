import { createContext, useContext, useState, useEffect, type ReactNode } from "react";

interface AuthContextType {
    accessToken: string | null;
    idToken: string | null;
    refreshToken: string | null;
    login: (tokens: { accessToken: string; idToken: string; refreshToken: string }) => void;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [accessToken, setAccessToken] = useState<string | null>(null);
    const [idToken, setIdToken] = useState<string | null>(null);
    const [refreshToken, setRefreshToken] = useState<string | null>(null);

    // 🔑 Load tokens from localStorage on mount
    useEffect(() => {
        const stored = localStorage.getItem("authTokens");
        if (stored) {
            try {
                const tokens = JSON.parse(stored);
                setAccessToken(tokens.accessToken);
                setIdToken(tokens.idToken);
                setRefreshToken(tokens.refreshToken);
            } catch (e) {
                console.error("Failed to parse stored tokens", e);
            }
        }
    }, []);

    const login = (tokens: { accessToken: string; idToken: string; refreshToken: string }) => {
        setAccessToken(tokens.accessToken);
        setIdToken(tokens.idToken);
        setRefreshToken(tokens.refreshToken);
        localStorage.setItem("authTokens", JSON.stringify(tokens));
    };

    const logout = () => {
        setAccessToken(null);
        setIdToken(null);
        setRefreshToken(null);
        localStorage.removeItem("authTokens");
    };

    return (
        <AuthContext.Provider value={{ accessToken, idToken, refreshToken, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) throw new Error("useAuth must be used within AuthProvider");
    return context;
};
