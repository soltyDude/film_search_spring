import React, { useState } from "react";
import { useNavigate } from "react-router-dom"; // <-- import added

export default function RegisterPage() {
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const navigate = useNavigate(); // <-- navigate defined

    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(false);

    const handleRegister = async (e) => {
        e.preventDefault();
        setError(null);
        setSuccess(false);

        try {
            const res = await fetch("/user-service/users/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username, email, password }),
            });

            if (!res.ok) {
                const msg = await res.text();
                throw new Error(msg || "Registration failed");
            }

            await res.json();
            setSuccess(true);
            setUsername("");
            setEmail("");
            setPassword("");

            setTimeout(() => navigate("/"), 1000);
        } catch (err) {
            setError(err.message);
        }
    };

    return (
        <div className="max-w-md mx-auto mt-20 p-6 border rounded-xl shadow-xl">
            <h2 className="text-2xl font-bold mb-4">Create Account</h2>

            <form onSubmit={handleRegister}>
                <input
                    className="w-full p-2 border mb-4"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                />
                <input
                    className="w-full p-2 border mb-4"
                    type="email"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                />
                <input
                    className="w-full p-2 border mb-4"
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />
                <button
                    type="submit"
                    className="w-full bg-blue-500 text-white py-2 rounded"
                >
                    Register
                </button>

                {error && <p className="text-red-500 mt-2">{error}</p>}
                {success && <p className="text-green-500 mt-2">Account created successfully!</p>}
            </form>
        </div>
    );
}
