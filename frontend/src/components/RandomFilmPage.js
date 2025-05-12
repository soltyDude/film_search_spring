// src/components/RandomFilmPage.js
import React, { useEffect, useState } from "react";
import { apiFetch }  from "../utils/api";
import { Link }      from "react-router-dom";

export default function RandomFilmPage() {

    const [film,   setFilm]   = useState(null);
    const [load,   setLoad]   = useState(true);
    const [error,  setError]  = useState(null);

    /* один запрос в функцию, чтобы переиспользовать при нажатии «Next» */
    const fetchRandom = async () => {
        try {
            setLoad(true);
            const data = await apiFetch("/film-service/films/random");
            setFilm({
                id:         data.id,
                title:      data.title,
                year:       data.releaseDate?.substring(0,4),
                posterUrl:  data.posterUrl,
                rating:     data.rating ?? data.apiRating ?? null
            });
        } catch (e) { setError(e.message); }
        finally     { setLoad(false); }
    };

    /* первый вызов */
    useEffect(() => { fetchRandom(); }, []);

    /* ------- UI‑состояния ------- */
    if (load)  return <p style={{padding:"2rem"}}>Loading…</p>;
    if (error) return <p style={{color:"red",padding:"2rem"}}>{error}</p>;

    /* ------- рендер ------- */
    return (
        <div style={{padding:"2rem",textAlign:"center"}}>
            <h2>Random pick</h2>

            <Link to={`/films/${film.id}`} style={{textDecoration:"none",color:"inherit"}}>
                <img
                    src={film.posterUrl ?? "https://via.placeholder.com/200x300"}
                    alt={film.title}
                    style={{width:200,height:300,objectFit:"cover",borderRadius:6}}
                />
                <h3 style={{marginTop:"1rem"}}>{film.title} {film.year && `(${film.year})`}</h3>
                {film.rating && <p>★ {film.rating.toFixed(1)}</p>}
            </Link>

            <button onClick={fetchRandom} style={{marginTop:"1.5rem"}}>
                Next random
            </button>
        </div>
    );
}
