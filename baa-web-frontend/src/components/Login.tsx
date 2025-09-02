import { useState } from "react";
import {
    Box,
    Card,
    CardContent,
    TextField,
    Button,
    Typography,
    ThemeProvider,
    createTheme,
} from "@mui/material";
import { useAuth } from "./AuthContext.tsx";

const theme = createTheme({
    palette: {
        primary: { main: "#1976d2" }, // blue
        secondary: { main: "#ff9800" }, // orange
        background: { default: "#f0f2f5" }, // light gray
    },
});

export default function Login() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const { login } = useAuth();

    const handleLogin = async () => {
        const res = await fetch(`${API_URL}/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ action: "login", username, password }),
        });
        const data = await res.json();
        if (res.ok && data.result && data.result.accessToken) {
            login({
                accessToken: data.result.accessToken,
                idToken: data.result.idToken,
                refreshToken: data.result.refreshToken,
            });
        } else {
            alert("Login failed: " + JSON.stringify(data));
        }
    };

    return (
        <ThemeProvider theme={theme}>
            <Box
                sx={{
                    height: "100vh",
                    width: "100vw",
                    bgcolor: "#000000",
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                }}
            >
                <Card sx={{ width: 400, p: 2, bgcolor: "#c218d9", borderRadius: "18px" }}>
                    <CardContent>
                        <Typography variant="h5" align="center" gutterBottom>
                            Login
                        </Typography>
                        <TextField fullWidth label="Username" margin="normal" value={username} onChange={(e) => setUsername(e.target.value)} />
                        <TextField fullWidth label="Password" type="password" margin="normal" value={password} onChange={(e) => setPassword(e.target.value)} />
                        <Button fullWidth variant="contained" color="primary" sx={{ mt: 2 }} onClick={handleLogin}>
                            Login
                        </Button>
                    </CardContent>
                </Card>
            </Box>
        </ThemeProvider>
    );
}
