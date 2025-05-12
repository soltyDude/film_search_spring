// src/utils/auth.js
export function getToken() {
    return localStorage.getItem("token");
}

export function getUserId() {
    return localStorage.getItem("userId");
}
