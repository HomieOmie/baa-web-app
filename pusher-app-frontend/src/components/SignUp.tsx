import { useState } from "react";
import {
    Box,
    Card,
    CardContent,
    TextField,
    Button,
    Typography,
    MenuItem,
} from "@mui/material";
import { useAuth } from "./AuthContext.tsx";


export default function SignupPage() {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [phone_number, setPhoneNumber] = useState('')
    const [birthday, setBirthday] = useState('')
    const [sex, setSex] = useState('')



    const [message, setMessage] = useState("");
    const { accessToken, idToken, refreshToken } = useAuth();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!accessToken || !idToken || !refreshToken) {
            setMessage("Please log in first");
            return;
        }

        try {
            const response = await fetch(`${API_URL}/signup`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${accessToken}`,
                },
                body: JSON.stringify({
                    action: "signup",
                    username: username,
                    email: email,
                    phone_number: phone_number,
                    birthday: birthday,
                    sex: sex
                }),
            });

            const data = await response.json();
            setMessage(data.result || data.error);
        } catch (err) {
            console.error(err);
            setMessage("Failed to sign up. Check console for details.");
        }
    };

    if (!accessToken || !idToken || !refreshToken) return <div>Please log in first</div>;

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
                        Create User
                    </Typography>

                    <TextField
                        fullWidth
                        label="Username"
                        name="username"
                        value={username}
                        onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                            setUsername(event.target.value);
                        }}
                        margin="normal"
                        required
                    />
                    <TextField
                        fullWidth
                        label="Email"
                        name="email"
                        type="email"
                        value={email}
                        onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                            setEmail(event.target.value);
                        }}
                        margin="normal"
                        required
                    />
                    <TextField
                        fullWidth
                        label="Phone Number (+15551234567)"
                        name="phone_number"
                        value={phone_number}
                        onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                            setPhoneNumber(event.target.value);
                        }}
                        margin="normal"
                        required
                    />
                    <TextField
                        fullWidth
                        label="Birthday"
                        name="birthday"
                        type="date"
                        value={birthday}
                        onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                            setBirthday(event.target.value);
                        }}
                        margin="normal"
                        InputLabelProps={{ shrink: true }}
                        required
                    />
                    <TextField
                        fullWidth
                        select
                        label="Sex"
                        name="sex"
                        value={sex}
                        onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                            setSex(event.target.value);
                        }}
                        margin="normal"
                        required
                    >
                        <MenuItem value="">Select Sex</MenuItem>
                        <MenuItem value="male">Male</MenuItem>
                        <MenuItem value="female">Female</MenuItem>
                    </TextField>

                    {message && (
                        <Typography variant="body2" color="error" sx={{ mt: 1 }}>
                            {message}
                        </Typography>
                    )}

                    <Button
                        fullWidth
                        variant="contained"
                        color="primary"
                        sx={{ mt: 2 }}
                        onClick={handleSubmit}
                    >
                        Sign Up
                    </Button>
                </CardContent>
            </Card>
        </Box>
    );
}
