import React from "react";
import { useNavigate } from "react-router-dom";

export default function HomePage() {
    const navigate = useNavigate();

    return (
        <div style={{padding: "2rem"}}>
            <h2>Welcome to the Movie App!</h2>
            <p>Here you can find movies, create playlists, add favorite movies, and rate films.</p>

            <button onClick={() => navigate("/films")}>Go to Films</button>
            <button onClick={() => navigate("/viewed")}>Go to Viewed Films</button>
            <button onClick={() => navigate("/my-playlists")}>My Playlists</button>
            <button onClick={() => navigate("/recommendations")}>Recommendations</button>
            <button onClick={() => navigate("/random")}>Random film</button>
        </div>
    );
}
