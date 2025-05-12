import React, { useEffect, useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { apiFetch }  from "../utils/api";

export default function PlaylistDetailsPage() {
    const { pid } = useParams();                  // playlistId из URL
    const nav     = useNavigate();

    const [playlist, setPlaylist] = useState(null);
    const [films,    setFilms]    = useState([]); // подробности каждого фильма
    const [error,    setError]    = useState(null);
    const [loading,  setLoading]  = useState(true);

    useEffect(() => {
        (async () => {
            try {
                setLoading(true);
                /* 1. детали плейлиста */
                const pl = await apiFetch(`/playlist-service/playlists/${pid}`);
                setPlaylist(pl);

                /* 2. подтянуть данные по фильмам */
                const filmIds = pl.playlistFilms.map(pf => pf.filmId);
                const all    = await Promise.all(
                    filmIds.map(id =>
                        apiFetch(`/film-service/films/${id}/details`).then(d => ({...d, id}))
                    )
                );
                setFilms(all);
            } catch (e) { setError(e.message); }
            finally     { setLoading(false); }
        })();
    }, [pid]);

    if (loading) return <p>Загрузка…</p>;
    if (error)   return <p style={{color:"red"}}>{error}</p>;
    if (!playlist) return <p>Плейлист не найден</p>;

    return (
        <div style={{padding:"2rem"}}>
            <button onClick={()=>nav(-1)}>← Назад</button>
            <h2 style={{marginTop:"1rem"}}>{playlist.name}</h2>

            {films.length === 0
                ? <p>В плейлисте пока нет фильмов.</p>
                : (
                    <div style={{
                        display:"grid",
                        gridTemplateColumns:"repeat(auto-fill,minmax(160px,1fr))",
                        gap:"1rem"
                    }}>
                        {films.map(f => (
                            <Link key={f.id} to={`/films/${f.id}`} style={{textAlign:"center"}}>
                                <img
                                    src={f.poster_url ?? "https://via.placeholder.com/160x240"}
                                    alt={f.title} style={{width:160,height:240,objectFit:"cover"}}
                                />
                                <p style={{margin:"0.5rem 0 0"}}>{f.title}</p>
                                <small>{f.release_date?.substring(0,4)}</small>
                            </Link>
                        ))}
                    </div>
                )}
        </div>
    );
}
