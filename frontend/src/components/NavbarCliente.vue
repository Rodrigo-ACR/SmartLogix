<template>
    <nav class="navbar">
        <div class="navbar-inner container">
            <div class="navbar-brand" @click="$router.push('/inicio')">
                <span>⚡</span> SmartLogix
            </div>
            <div class="navbar-links">
                <router-link to="/inicio">Inicio</router-link>
                <router-link to="/mis-pedidos">Mis Pedidos</router-link>
                <router-link to="/perfil">Mi Perfil</router-link>
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
        return { nombre: localStorage.getItem("nombre") || "Cliente" };
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
    font-size: 1.3rem;
    font-weight: 800;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 8px;
    color: var(--text-primary);
}

.navbar-links {
    display: flex;
    gap: 28px;
}

.navbar-links a {
    color: var(--text-secondary);
    font-size: 0.9rem;
    font-weight: 500;
    transition: var(--transition);
}

.navbar-links a:hover,
.navbar-links a.router-link-active {
    color: var(--accent);
}

.navbar-user {
    display: flex;
    align-items: center;
    gap: 12px;
}

.user-name {
    color: var(--text-secondary);
    font-size: 0.9rem;
}
</style>