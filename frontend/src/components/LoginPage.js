import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState(null);

    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setError(null);

        try {
            const res = await fetch(
                "/user-service/auth/login",
                {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ email, password }),
                }
            );

            if (!res.ok) throw new Error(await res.text());

            const data = await res.json(); // { token, email, role }
            localStorage.setItem("token", data.token);
            localStorage.setItem("userId", data.userId);
            navigate("/home"); // Перенаправление на домашнюю страницу после логина
        } catch (err) {
            setError(err.message);
        }
    };

    return (
        <div style={{ padding: "2rem" }}>
            <h2>Login</h2>
            <form onSubmit={handleLogin}>
                <input
                    type="email"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                /><br /><br />
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                /><br /><br />
                <button type="submit">Login</button>
            </form>
            {error && <p style={{ color: "red" }}>{error}</p>}
            <br />
            <button onClick={() => navigate("/register")}>Create New Account</button>
        </div>
    );
}
