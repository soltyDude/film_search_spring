// src/components/FilmsPage.js
import React, { useEffect, useState } from "react";
import { apiFetch }  from "../utils/api";
import { Link }      from "react-router-dom";

export default function FilmsPage() {

    /* ---------- state ---------- */
    const [films,       setFilms]       = useState([]);
    const [loading,     setLoading]     = useState(true);
    const [error,       setError]       = useState(null);

    const [query,       setQuery]       = useState("");  // значение инпута
    const [searching,   setSearching]   = useState(false);

    /* ---------- initial load ---------- */
    useEffect(() => {
        (async () => {
            try {
                const data = await apiFetch("/film-service/films");
                setFilms(data);
            } catch (e) { setError(e.message); }
            finally     { setLoading(false); }
        })();
    }, []);

    /* ---------- поиск ---------- */
    const handleSearch = async (e) => {
        e.preventDefault();
        if (!query.trim()) return;

        try {
            setSearching(true);
            setError(null);

            /* /search/simple отдаёт List<Film>, без пагинации — удобно для фронта */
            const data = await apiFetch(
                `/film-service/films/search/simple?query=${encodeURIComponent(query.trim())}`
            );
            setFilms(data);
        } catch (e) { setError(e.message); }
        finally     { setSearching(false); }
    };

    /* ---------- render ---------- */
    if (loading) return <p>Loading…</p>;
    if (error)   return <p style={{color:"red"}}>{error}</p>;

    return (
        <div style={{padding:"1rem"}}>
            <h2>Films</h2>

            {/* ---- строка поиска ---- */}
            <form onSubmit={handleSearch} style={{marginBottom:"1rem"}}>
                <input
                    type="text"
                    placeholder="Search by title…"
                    value={query}
                    onChange={e=>setQuery(e.target.value)}
                    style={{padding:4,width:260,maxWidth:"80%"}}
                />
                <button type="submit" disabled={searching}
                        style={{marginLeft:8}}>
                    {searching ? "Searching…" : "Search"}
                </button>
            </form>

            <div style={{
                display:"grid",
                gridTemplateColumns:"repeat(auto-fill,minmax(160px,1fr))",
                gap:"1rem"
            }}>
                {films.map(f => (
                    <Link key={f.id} to={`/films/${f.id}`}
                          style={{textAlign:"center",textDecoration:"none",color:"inherit"}}>
                        <img
                            src={f.posterUrl ?? "https://via.placeholder.com/160x240"}
                            alt={f.title}
                            style={{width:160,height:240,objectFit:"cover"}}
                        />
                        <p style={{margin:".5rem 0 0",fontWeight:600}}>{f.title}</p>
                        <small>
                            {f.year ?? ""}{f.rating ? ` • ★${f.rating}` : ""}
                        </small>
                    </Link>
                ))}
            </div>
        </div>
    );
}
