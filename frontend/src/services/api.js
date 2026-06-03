const BASE_URL = "http://localhost:8085";

export const getToken = () => localStorage.getItem("token");
export const getRol = () => localStorage.getItem("rol");
export const getNombre = () => localStorage.getItem("nombre");
export const getId = () => localStorage.getItem("id");

export const getHeaders = () => ({
    "Content-Type": "application/json",
    Authorization: "Bearer " + getToken()
});

// AUTH
export async function login(correo, password) {
    const res = await fetch(`${BASE_URL}/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ correo, password })
    });
    return res.json();
}

export async function register(data) {
    const res = await fetch(`${BASE_URL}/api/productos/usuarios/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    });
    return res.json();
}

// PRODUCTOS
export async function getProductos() {
    const res = await fetch(`${BASE_URL}/api/productos`, { headers: getHeaders() });
    return res.json();
}

export async function crearProducto(data) {
    const res = await fetch(`${BASE_URL}/api/productos`, {
        method: "POST", headers: getHeaders(), body: JSON.stringify(data)
    });
    return res.json();
}

export async function editarProducto(id, data) {
    const res = await fetch(`${BASE_URL}/api/productos/${id}`, {
        method: "PUT", headers: getHeaders(), body: JSON.stringify(data)
    });
    return res.json();
}

export async function eliminarProducto(id) {
    await fetch(`${BASE_URL}/api/productos/${id}`, {
        method: "DELETE", headers: getHeaders()
    });
}

// PEDIDOS
export async function getPedidos() {
    const res = await fetch(`${BASE_URL}/api/productos/pedidos`, { headers: getHeaders() });
    return res.json();
}

export async function crearPedido(data) {
    const res = await fetch(`${BASE_URL}/api/productos/pedidos`, {
        method: "POST", headers: getHeaders(), body: JSON.stringify(data)
    });
    return res.json();
}

export async function cambiarEstadoPedido(id, estado) {
    const res = await fetch(`${BASE_URL}/api/productos/pedidos/${id}/estado?estado=${estado}`, {
        method: "PATCH", headers: getHeaders()
    });
    return res.json();
}

// ENVIOS
export async function getEnvios() {
    const res = await fetch(`${BASE_URL}/api/productos/envios`, { headers: getHeaders() });
    return res.json();
}

export async function crearEnvio(data) {
    const res = await fetch(`${BASE_URL}/api/productos/envios`, {
        method: "POST", headers: getHeaders(), body: JSON.stringify(data)
    });
    return res.json();
}

export async function cambiarEstadoEnvio(id, estado) {
    const res = await fetch(`${BASE_URL}/api/productos/envios/${id}/estado?estado=${estado}`, {
        method: "PATCH", headers: getHeaders()
    });
    return res.json();
}

// USUARIOS
export async function getUsuarios() {
    const res = await fetch(`${BASE_URL}/api/productos/usuarios`, { headers: getHeaders() });
    return res.json();
}

// Solo para admin
export async function actualizarUsuario(id, data) {
    const res = await fetch(`${BASE_URL}/api/productos/usuarios/${id}`, {
        method: "PUT",
        headers: getHeaders(),
        body: JSON.stringify(data)
    });
    return res.json();
}