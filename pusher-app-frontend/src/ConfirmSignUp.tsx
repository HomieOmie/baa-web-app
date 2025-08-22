import React, { useState } from "react";
import {
    Box,
    Card,
    CardContent,
    TextField,
    Button,
    Typography,
} from "@mui/material";


export const ConfirmSignup: React.FC = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");

    const handleConfirm = async (e: React.FormEvent) => {
        e.preventDefault();

        try {
            const response = await fetch(`${API_URL}/confirmSignup`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    action: "confirmSignup",
                    username,
                    password,
                }),
            });

            const data = await response.json();
            setMessage(data.result || data.error || "Unknown response");
        } catch (err) {
            console.error(err);
            setMessage("Failed to confirm signup");
        }
    };

    return (
        <Box
            sx={{
                height: "100vh",
                width: "100vw",
                bgcolor: "#f0f2f5",
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
            }}
        >
            <Card sx={{ width: 400, p: 2 }}>
                <CardContent>
                    <Typography variant="h5" align="center" gutterBottom>
                        Confirm Your Account
                    </Typography>

                    <TextField
                        fullWidth
                        label="Username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        margin="normal"
                        required
                    />

                    <TextField
                        fullWidth
                        label="New Password"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        margin="normal"
                        required
                    />

                    {message && (
                        <Typography variant="body2" color="error" sx={{ mt: 1 }}>
                            {message}
                        </Typography>
                    )}

                    <Button
                        fullWidth
                        variant="contained"
                        color="#1D2180"
                        sx={{ mt: 2 }}
                        onClick={handleConfirm}
                    >
                        Confirm Signup
                    </Button>
                </CardContent>
            </Card>
        </Box>
    );
};
