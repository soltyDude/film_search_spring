// src/utils/api.js
export async function apiFetch(url, options = {}) {
    const token   = localStorage.getItem("token");
    const headers = {
        "Content-Type": "application/json",
        Authorization: token ? `Bearer ${token}` : "",
        ...options.headers,
    };

    const response = await fetch(url, { ...options, headers });

    if (!response.ok) {
        // пытаемся вытащить текст ошибки, даже если это не‑JSON
        const text = await response.text();
        throw new Error(text || `HTTP ${response.status}`);
    }

    /* ---------- тело может отсутствовать ---------- */
    // 1) 204 No Content         → сразу возвращаем null
    // 2) Пустой ответ (length 0) → null
    // 3) Иначе пытаемся JSON.parse, при ошибке отдаём сырой текст
    if (response.status === 204) return null;

    const txt = await response.text();
    if (!txt) return null;

    try {
        return JSON.parse(txt);
    } catch {
        return txt;               // например plain‑text сообщение
    }
}
