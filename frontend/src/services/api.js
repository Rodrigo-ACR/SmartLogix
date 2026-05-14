const BASE_URL = "http://localhost:8085";

export function getToken() {
    return localStorage.getItem("token");
}

export function getHeaders() {
    return {
        "Content-Type": "application/json",
        Authorization: "Bearer " + getToken()
    };
}

export async function login(user) {
    const res = await fetch(`${BASE_URL}/auth/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(user)
    });

    return await res.json();
}

export async function getProductos() {
    const res = await fetch(`${BASE_URL}/api/productos`, {
        headers: getHeaders()
    });
    return await res.json();
}

export async function getPedidos() {
    const res = await fetch(`${BASE_URL}/api/productos/pedidos`, {
        headers: getHeaders()
    });
    return await res.json();
}

export async function getEnvios() {
    const res = await fetch(`${BASE_URL}/api/productos/envios`, {
        headers: getHeaders()
    });
    return await res.json();
}

export async function editarProducto(producto) {
    const res = await fetch(
        `${BASE_URL}/api/productos/${producto.id}`,
        {
            method: "PUT",
            headers: getHeaders(),
            body: JSON.stringify(producto)
        }
    );

    return await res.json();
}

export async function eliminarProducto(id) {
    await fetch(`${BASE_URL}/api/productos/${id}`, {
        method: "DELETE",
        headers: getHeaders()
    });
}