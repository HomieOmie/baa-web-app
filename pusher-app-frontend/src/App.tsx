import React from "react";
import { BrowserRouter as Router, Routes, Route, Link, Navigate } from "react-router-dom";
import { Box, AppBar, Toolbar, Button, Typography, ThemeProvider, createTheme } from "@mui/material";
import { useAuth, AuthProvider } from "./components/AuthContext.tsx";
import Login from "./components/Login.tsx";
import SignUp from "./components/SignUp.tsx";
import {ConfirmSignup} from "./components/ConfirmSignUp.tsx";

// Error Boundary to catch runtime errors
class ErrorBoundary extends React.Component<{ children: React.ReactNode }, { hasError: boolean }> {
    constructor(props: any) {
        super(props);
        this.state = { hasError: false };
    }

    static getDerivedStateFromError() {
        return { hasError: true };
    }

    render() {
        if (this.state.hasError) return <h1>Something went wrong.</h1>;
        return this.props.children;
    }
}

// Theme
const theme = createTheme({
    palette: {
        primary: { main: "#1D2180" },
        secondary: { main: "#000000" },
        background: { default: "#f0f2f5" },
    },
    // typography: {
    //     fontFamily: "'sdc', Arial, sans-serif",
    //     allVariants: {
    //         wordSpacing: "-4px"
    //     }
    // }
});

// Navbar for authenticated users
function Navbar() {
    const { logout } = useAuth();
    return (
        <AppBar color="secondary" position="static">
            <Toolbar>
                <Typography variant="h6" sx={{ flexGrow: 1 }}>
                    SDC Buggy Pushers
                </Typography>
                <Button color="inherit" component={Link} to="/signup">
                    Sign Up
                </Button>
                <Button color="inherit" onClick={logout}>
                    Logout
                </Button>
            </Toolbar>
        </AppBar>
    );
}

// Protected routes for authenticated users
function AuthenticatedApp() {
    return (
        <Box>
            <Navbar />
            <Box>
                <Routes>
                    <Route path="/signup" element={<SignUp />} />
                    <Route path="/confirm-signup" element={<ConfirmSignup />} />
                    <Route path="*" element={<Navigate to="/signup" replace />} />
                </Routes>
            </Box>
        </Box>
    );
}

// Main app component
function AppContent() {
    const { accessToken } = useAuth();

    if (!accessToken) {
        return <Login />;
    }

    return <AuthenticatedApp />;
}

export default function App() {
    return (
        <ErrorBoundary>
            <AuthProvider>
                <ThemeProvider theme={theme}>
                    <Router>
                        <AppContent />
                    </Router>
                </ThemeProvider>
            </AuthProvider>
        </ErrorBoundary>
    );
}
