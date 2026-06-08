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
import "@/assets/styles/NavbarAdmin.css";
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
