// src/components/RecommendationsPage.js
import React, { useEffect, useState } from "react";
import { apiFetch }  from "../utils/api";
import { getUserId } from "../utils/auth";
import { Link }      from "react-router-dom";

export default function RecommendationsPage() {

    const [recs,    setRecs]    = useState([]);
    const [loading, setLoading] = useState(true);
    const [error,   setError]   = useState(null);

    useEffect(() => {
        (async () => {
            const userId = getUserId();
            if (!userId) { setError("Please sign in first"); return; }

            try {
                /* 👉 Endpoint из вашего контроллера */
                const data = await apiFetch(
                    `/review-service/recommendations/${userId}`
                );
                setRecs(data);
            } catch (e) { setError(e.message); }
            finally     { setLoading(false); }
        })();
    }, []);

    /* --- UI‑состояния --- */
    if (loading) return <p>Loading…</p>;
    if (error)   return <p style={{color:"red"}}>{error}</p>;

    if (recs.length === 0)
        return <p style={{padding:"2rem"}}>Нет рекомендаций — сначала поставьте рейтинги нескольким фильмам!</p>;

    /* --- отображаем --- */
    return (
        <div style={{padding:"1rem"}}>
            <h2>Recommended for you</h2>

            <div style={{
                display:"grid",
                gridTemplateColumns:"repeat(auto-fill,minmax(160px,1fr))",
                gap:"1rem"
            }}>
                {recs.map(r => (
                    <Link
                        key={r.filmId ?? r.id ?? JSON.stringify(r)}
                        to={`/films/${r.filmId ?? r.id}`}
                        style={{textAlign:"center",textDecoration:"none",color:"inherit"}}
                    >
                        <img
                            src={
                                r.posterUrl ??
                                r.poster_url ??
                                "https://via.placeholder.com/160x240"
                            }
                            alt={r.title}
                            style={{width:160,height:240,objectFit:"cover"}}
                        />
                        <p style={{margin:".5rem 0 0",fontWeight:600}}>
                            {r.title}
                        </p>
                    </Link>
                ))}
            </div>
        </div>
    );
}
