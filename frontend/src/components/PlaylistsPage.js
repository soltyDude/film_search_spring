import React, { useEffect, useState } from "react";
import { Link }      from "react-router-dom";
import { apiFetch }  from "../utils/api";
import { getUserId } from "../utils/auth";

export default function PlaylistsPage() {
    const [playlists, setPlaylists] = useState([]);
    const [newName,   setNewName]   = useState("");
    const [loading,   setLoading]   = useState(false);
    const [error,     setError]     = useState(null);

    /* ---------- загрузка ---------- */
    const load = async () => {
        try {
            setError(null);
            const userId = getUserId();
            const data   = await apiFetch(`/playlist-service/playlists/user/${userId}`);
            setPlaylists(data);
        } catch (e) { setError(e.message); }
    };

    useEffect(() => { load(); }, []);

    /* ---------- создать ---------- */
    const create = async () => {
        if (!newName.trim()) return;
        try {
            setLoading(true);
            const userId = getUserId();
            await apiFetch(
                `/playlist-service/playlists?userId=${userId}&playlistName=${encodeURIComponent(newName.trim())}`,
                { method:"POST" }
            );
            setNewName("");
            await load();
        } catch (e) { alert(e.message); }
        finally     { setLoading(false); }
    };

    /* ---------- удалить ---------- */
    const del = async (id) => {
        if (!window.confirm("Удалить плейлист?")) return;
        try {
            await apiFetch(`/playlist-service/playlists/${id}`, { method:"DELETE" });
            setPlaylists(pl => pl.filter(p => p.id !== id));
        } catch (e) { alert(e.message); }
    };

    /* ---------- UI ---------- */
    return (
        <div style={{padding:"2rem"}}>
            <h2>Мои плейлисты</h2>

            {/* создание */}
            <div style={{marginBottom:"1.5rem"}}>
                <input
                    placeholder="Название плейлиста"
                    value={newName}
                    onChange={e=>setNewName(e.target.value)}
                />
                <button onClick={create} disabled={loading}>
                    {loading ? "Создаём…" : "Создать"}
                </button>
            </div>

            {error && <p style={{color:"red"}}>{error}</p>}

            {/* список */}
            {playlists.length === 0
                ? <p>У вас пока нет плейлистов.</p>
                : (
                    <ul style={{paddingLeft:0,listStyle:"none",maxWidth:400}}>
                        {playlists.map(pl => (
                            <li key={pl.id} style={{
                                border:"1px solid #ccc",padding:"1rem",marginBottom:"1rem",
                                display:"flex",justifyContent:"space-between",alignItems:"center"
                            }}>
                                <Link to={`/playlists/${pl.id}`}>{pl.name}</Link>
                                <button onClick={()=>del(pl.id)} style={{marginLeft:"1rem"}}>
                                    ×
                                </button>
                            </li>
                        ))}
                    </ul>
                )}
        </div>
    );
}
