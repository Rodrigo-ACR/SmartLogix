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

            <button class="hamburger" @click="menuAbierto = !menuAbierto" :class="{ open: menuAbierto }">
                <span></span>
                <span></span>
                <span></span>
            </button>
        </div>

        <div class="mobile-menu" :class="{ open: menuAbierto }">
            <router-link to="/inicio" @click="menuAbierto = false">🏠 Inicio</router-link>
            <router-link to="/mis-pedidos" @click="menuAbierto = false">📦 Mis Pedidos</router-link>
            <router-link to="/perfil" @click="menuAbierto = false">👤 Mi Perfil</router-link>
            <div class="mobile-footer">
                <span class="user-name">{{ nombre }}</span>
                <button class="btn btn-outline btn-sm" @click="logout">Salir</button>
            </div>
        </div>
    </nav>
</template>

<script>
import "@/assets/styles/navbarcliente.css";
export default {
    data() {
        return {
            nombre: localStorage.getItem("nombre") || "Cliente",
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