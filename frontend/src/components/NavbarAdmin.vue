<template>
    <nav class="navbar">
        <div class="navbar-inner container">
            <div class="navbar-brand" @click="$router.push('/admin')">
                <span>⚡</span> SmartLogix <span class="admin-tag">Admin</span>
            </div>
            <div class="navbar-links">
                <router-link to="/admin">Dashboard</router-link>
                <router-link to="/admin/productos">Productos</router-link>
                <router-link to="/admin/pedidos">Pedidos</router-link>
                <router-link to="/admin/envios">Envíos</router-link>
                <router-link to="/admin/clientes">Clientes</router-link>
            </div>
            <div class="navbar-user">
                <span class="user-name">{{ nombre }}</span>
                <button class="btn btn-outline btn-sm" @click="logout">Salir</button>
            </div>
        </div>
    </nav>
</template>

<script>
export default {
    data() {
        return { nombre: localStorage.getItem("nombre") || "Admin" };
    },
    methods: {
        logout() {
            localStorage.clear();
            this.$router.push("/login");
        }
    }
}
</script>

<style scoped>
.navbar {
    background: rgba(10, 10, 15, 0.9);
    backdrop-filter: blur(12px);
    border-bottom: 1px solid var(--border);
    position: sticky;
    top: 0;
    z-index: 50;
}

.navbar-inner {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 64px;
    gap: 32px;
}

.navbar-brand {
    font-family: 'Syne', sans-serif;
    font-size: 1.2rem;
    font-weight: 800;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 8px;
    color: var(--text-primary);
    white-space: nowrap;
}

.admin-tag {
    background: var(--accent);
    color: white;
    font-size: 0.7rem;
    padding: 2px 8px;
    border-radius: 999px;
    font-weight: 600;
}

.navbar-links {
    display: flex;
    gap: 24px;
    flex: 1;
    justify-content: center;
}

.navbar-links a {
    color: var(--text-secondary);
    font-size: 0.9rem;
    font-weight: 500;
    transition: var(--transition);
    white-space: nowrap;
}

.navbar-links a:hover,
.navbar-links a.router-link-active {
    color: var(--accent);
}

.navbar-user {
    display: flex;
    align-items: center;
    gap: 12px;
    white-space: nowrap;
}

.user-name {
    color: var(--text-secondary);
    font-size: 0.9rem;
}
</style>