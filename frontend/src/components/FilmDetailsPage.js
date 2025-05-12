// src/components/FilmDetailsPage.js
import React, { useEffect, useState } from "react";
import { useParams }              from "react-router-dom";
import { apiFetch }               from "../utils/api";
import { getUserId }              from "../utils/auth";

export default function FilmDetailsPage() {
    const { id } = useParams();

    /* ---------- state ---------- */
    const [film, setFilm]             = useState(null);
    const [reviews, setReviews]       = useState([]);
    const [error,   setError]         = useState(null);

    const [showReviewModal,    setShowReviewModal]    = useState(false);
    const [showPlaylistModal,  setShowPlaylistModal]  = useState(false);

    const [ratingInput,  setRatingInput]  = useState("");
    const [commentInput, setCommentInput] = useState("");

    const [isViewed,  setIsViewed]  = useState(false);
    const [loading,   setLoading]   = useState(false);

    /* плейлисты */
    const [playlists,      setPlaylists]      = useState([]);
    const [loadingPl,      setLoadingPl]      = useState(false);

    /* ---------- helpers ---------- */
    const loadFilm = async () => {
        const d = await apiFetch(`/film-service/films/${id}/details`);
        setFilm({
            title:      d.title,
            year:       d.release_date ? new Date(d.release_date).getFullYear() : "",
            poster:     d.poster_url,
            overview:   d.overview,
            apiRating:  d.api_rating?.toFixed(1),
            userRating: d.rating != null ? d.rating.toFixed(1) : null,
            genres:     d.genres ?? []
        });
    };

    const loadReviews = () =>
        apiFetch(`/review-service/reviews/film/${id}`).then(setReviews);

    useEffect(() => {
        (async () => {
            try {
                await Promise.all([loadFilm(), loadReviews()]);
                /* проверяем «просмотренные» */
                const userId = getUserId();
                if (userId) {
                    const viewed = await apiFetch(`/review-service/viewed/user/${userId}`);
                    setIsViewed(viewed.some(v => v.filmId === Number(id)));
                }
            } catch (e) { setError(e.message); }
        })();
    }, [id]);

    /* ---------- просмотренные ---------- */
    const addViewed = async () => {
        try {
            setLoading(true);
            const userId = getUserId();
            await apiFetch(`/review-service/viewed?userId=${userId}&filmId=${id}`, { method:"POST" });
            setIsViewed(true);
            alert("Фильм добавлен в просмотренные");
        } catch (e) { alert(e.message); }
        finally     { setLoading(false); }
    };

    /* ---------- отзыв ---------- */
    const submitReview = async () => {
        try {
            setLoading(true);
            const userId = getUserId();
            /* авто‑добавление в просмотренные */
            if (!isViewed) {
                await apiFetch(`/review-service/viewed?userId=${userId}&filmId=${id}`, { method:"POST" });
                setIsViewed(true);
            }
            await apiFetch("/review-service/reviews", {
                method:"POST",
                body: JSON.stringify({
                    userId:     Number(userId),
                    filmId:     Number(id),
                    rating:     ratingInput ? Number(ratingInput) : null,
                    reviewText: commentInput.trim()
                })
            });
            await Promise.all([loadFilm(), loadReviews()]);
            alert("Отзыв сохранён!");
            setShowReviewModal(false);
            setRatingInput(""); setCommentInput("");
        } catch (e) { alert(e.message); }
        finally     { setLoading(false); }
    };

    /* ---------- плейлисты ---------- */
    const openPlaylistModal = async () => {
        try {
            setLoadingPl(true);
            const userId = getUserId();
            const pls = await apiFetch(`/playlist-service/playlists/user/${userId}`);
            setPlaylists(pls);
            setShowPlaylistModal(true);
        } catch (e) { alert(e.message); }
        finally     { setLoadingPl(false); }
    };

    const addToPlaylist = async (playlistId) => {
        try {
            await apiFetch(
                `/playlist-service/playlists/${playlistId}/films?filmId=${id}`,
                { method:"POST" }
            );
            alert("Фильм добавлен в плейлист!");
            setShowPlaylistModal(false);
        } catch (e) {
            alert("Не удалось добавить: " + e.message);
        }
    };

    /* ---------- render ---------- */
    if (error)   return <p style={{color:"red"}}>{error}</p>;
    if (!film)   return <p>Загрузка…</p>;

    return (
        <div style={{padding:"2rem"}}>
            <h2>{film.title} {film.year && `(${film.year})`}</h2>
            <img src={film.poster ?? "https://via.placeholder.com/200x300"}
                 alt={film.title} style={{width:200,height:300,objectFit:"cover"}}/>

            <p><b>TMDB‑рейтинг:</b> {film.apiRating}</p>
            <p><b>Рейтинг пользователей:</b> {film.userRating ?? "N/A"}</p>
            <p><b>Описание:</b> {film.overview}</p>

            {reviews.length > 0 && (
                <>
                    <h3>Отзывы</h3>
                    <ul style={{paddingLeft:18}}>
                        {reviews.map(rv => (
                            <li key={rv.id}>
                                <b>★{rv.rating ?? "—"}</b>{' '}
                                {rv.reviewText}
                            </li>
                        ))}
                    </ul>
                </>
            )}

            {/* ---- кнопки ---- */}
            {!isViewed && (
                <button disabled={loading} onClick={addViewed}
                        style={{marginRight:"1rem"}}>
                    {loading ? "Добавляем…" : "Добавить в просмотренные"}
                </button>
            )}

            {isViewed && (
                <button onClick={() => setShowReviewModal(true)}
                        style={{marginRight:"1rem"}}>
                    Оставить отзыв
                </button>
            )}

            {/* кнопка плейлистов — теперь ВСЕГДА доступна */}
            <button onClick={openPlaylistModal}>
                {loadingPl ? "Загрузка…" : "Добавить в плейлист"}
            </button>

            {/* ---- модальное окно отзыва ---- */}
            {showReviewModal && (
                <Modal onClose={()=>setShowReviewModal(false)}>
                    <h3>Отзыв</h3>
                    <input type="number" min="0" max="10" step="0.1"
                           placeholder="Оценка"
                           value={ratingInput}
                           onChange={e=>setRatingInput(e.target.value)}
                           style={{width:"100%",marginBottom:"1rem"}}/>
                    <textarea placeholder="Комментарий"
                              value={commentInput}
                              onChange={e=>setCommentInput(e.target.value)}
                              style={{width:"100%",marginBottom:"1rem"}}/>
                    <button disabled={loading} onClick={submitReview}>
                        {loading ? "Сохраняем…" : "Сохранить"}
                    </button>
                </Modal>
            )}

            {/* ---- модальное окно плейлистов ---- */}
            {showPlaylistModal && (
                <Modal onClose={()=>setShowPlaylistModal(false)}>
                    <h3>Выберите плейлист</h3>
                    {playlists.length === 0 ? (
                        <p>У вас нет плейлистов</p>
                    ) : (
                        <ul style={{listStyle:"none",padding:0}}>
                            {playlists.map(pl => (
                                <li key={pl.id} style={{marginBottom:"0.5rem"}}>
                                    <button style={{width:"100%"}}
                                            onClick={()=>addToPlaylist(pl.id)}>
                                        {pl.name}
                                    </button>
                                </li>
                            ))}
                        </ul>
                    )}
                </Modal>
            )}
        </div>
    );
}

/* «голая» компонентка‑обёртка для модалок */
function Modal({ children, onClose }) {
    return (
        <div style={{
            position:"fixed",inset:0,background:"rgba(0,0,0,.5)",
            display:"flex",alignItems:"center",justifyContent:"center",zIndex:1000
        }}>
            <div style={{background:"#fff",padding:"2rem",borderRadius:8,minWidth:280}}>
                {children}
                <div style={{textAlign:"right",marginTop:"1rem"}}>
                    <button onClick={onClose}>Закрыть</button>
                </div>
            </div>
        </div>
    );
}
