<template>
    <nav class="navbar">
        <div class="navbar-inner container">
            <div class="navbar-brand" @click="$router.push('/admin')">
                <span>⚡</span> SmartLogix <span class="admin-tag">Admin</span>
            </div>

            <!-- Links desktop -->
            <div class="navbar-links">
                <router-link to="/admin">Dashboard</router-link>
                <router-link to="/admin/productos">Productos</router-link>
                <router-link to="/admin/pedidos">Pedidos</router-link>
                <router-link to="/admin/envios">Envíos</router-link>
                <router-link to="/admin/clientes">Clientes</router-link>
            </div>

            <div class="navbar-user">
                <span class="toggle-desktop">
                    <ThemeToggle />
                </span>
                <span class="user-name">{{ nombre }}</span>
                <button class="btn-logout" @click="logout" title="Cerrar sesión">
                    <Icons name="logout" :size="20" color="currentColor" />
                </button>
            </div>

            <!-- Botón hamburguesa (solo móvil) -->
            <button class="hamburger" @click="menuAbierto = !menuAbierto" :class="{ open: menuAbierto }">
                <span></span>
                <span></span>
                <span></span>
            </button>
        </div>

        <!-- Menú móvil -->
        <div class="mobile-menu" :class="{ open: menuAbierto }">
            <router-link to="/admin" @click="menuAbierto = false">🏠 Dashboard</router-link>
            <router-link to="/admin/productos" @click="menuAbierto = false">📦 Productos</router-link>
            <router-link to="/admin/pedidos" @click="menuAbierto = false">🛒 Pedidos</router-link>
            <router-link to="/admin/envios" @click="menuAbierto = false">🚚 Envíos</router-link>
            <router-link to="/admin/clientes" @click="menuAbierto = false">👥 Clientes</router-link>
            <div class="mobile-footer">
                <div class="mobile-footer-row">
                    <ThemeToggle />
                    <span style="font-size:0.82rem;color:var(--text-muted)">Tema</span>
                </div>
                <div class="mobile-footer-row">
                    <span class="user-name">{{ nombre }}</span>
                    <button class="btn-logout" @click="logout" title="Cerrar sesión">
                        <Icons name="logout" :size="18" color="currentColor" />
                    </button>
                </div>
            </div>
        </div>
    </nav>
</template>

<script>
import ThemeToggle from "./ThemeToggle.vue";
import Icons from "./Icons.vue";
import "@/assets/styles/navbaradmin.css";
export default {
    components: { ThemeToggle, Icons },
    data() {
        return {
            nombre: localStorage.getItem("nombre") || "Admin",
            menuAbierto: false
        };
    },
    methods: {
        logout() {
            localStorage.clear();
            this.$router.push("/login");
        }
    }
}
</script>