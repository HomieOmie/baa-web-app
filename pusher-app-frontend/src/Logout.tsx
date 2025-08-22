import { useAuth } from "./AuthContext";

export default function LogoutButton() {
    const { logout } = useAuth();

    return (
        <button onClick={logout} style={{ marginTop: "20px" }}>
            Logout
        </button>
    );
}