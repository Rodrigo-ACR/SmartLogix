<template>
    <div>
        <NavbarCliente />
        <div class="container page">

            <div class="perfil-hero">
                <div class="perfil-avatar-wrap">
                    <div class="perfil-avatar">{{ iniciales }}</div>
                    <div class="perfil-avatar-ring"></div>
                </div>
                <div class="perfil-hero-info">
                    <h1>{{ nombre }}</h1>
                    <span class="badge badge-accent">{{ rol }}</span>
                    <p class="text-muted" style="margin-top:8px">Miembro de SmartLogix</p>
                </div>
            </div>

            <div class="perfil-grid">

                <div class="card perfil-seccion">
                    <h3>📋 Información de cuenta</h3>
                    <div class="info-lista">
                        <div class="info-item">
                            <span>Nombre completo</span>
                            <strong>{{ nombre }}</strong>
                        </div>
                        <div class="info-item">
                            <span>Rol</span>
                            <strong>{{ rol }}</strong>
                        </div>
                        <div class="info-item">
                            <span>ID de usuario</span>
                            <strong>#{{ id }}</strong>
                        </div>
                    </div>
                </div>

                <div class="card perfil-seccion">
                    <h3>📍 Mis direcciones</h3>

                    <div class="direcciones-lista">
                        <div v-for="(dir, i) in direcciones" :key="i" class="direccion-item">
                            <span class="dir-icon">🏠</span>
                            <span class="dir-texto">{{ dir }}</span>
                            <button class="btn-remove-dir" @click="eliminarDireccion(i)">✕</button>
                        </div>
                        <div v-if="direcciones.length === 0" class="empty-dir">
                            No tienes direcciones guardadas
                        </div>
                    </div>

                    <div v-if="agregando" class="nueva-dir-form">
                        <input v-model="nuevaDireccion" type="text" placeholder="Ej: Av. Siempre Viva 123, Santiago"
                            @keyup.enter="guardarDireccion" />
                        <div class="nueva-dir-btns">
                            <button class="btn btn-outline btn-sm" @click="agregando = false">Cancelar</button>
                            <button class="btn btn-primary btn-sm" @click="guardarDireccion" :disabled="guardando">
                                {{ guardando ? 'Guardando...' : 'Guardar' }}
                            </button>
                        </div>
                    </div>

                    <button v-else class="btn btn-outline w-full" style="margin-top:16px" @click="agregando = true">
                        + Agregar dirección
                    </button>

                    <p v-if="mensaje" class="mensaje-ok">✅ {{ mensaje }}</p>
                </div>

                <div class="card perfil-seccion">
                    <h3>⚡ Acciones rápidas</h3>
                    <div class="acciones-lista">
                        <button class="accion-btn" @click="$router.push('/inicio')">
                            <span class="accion-icon">🛍️</span>
                            <div>
                                <strong>Ver catálogo</strong>
                                <p>Explora todos los productos</p>
                            </div>
                            <span class="accion-arrow">→</span>
                        </button>
                        <button class="accion-btn" @click="$router.push('/mis-pedidos')">
                            <span class="accion-icon">📦</span>
                            <div>
                                <strong>Mis pedidos</strong>
                                <p>Revisa el estado de tus compras</p>
                            </div>
                            <span class="accion-arrow">→</span>
                        </button>
                        <button class="accion-btn danger" @click="logout">
                            <span class="accion-icon">🚪</span>
                            <div>
                                <strong>Cerrar sesión</strong>
                                <p>Salir de tu cuenta</p>
                            </div>
                            <span class="accion-arrow">→</span>
                        </button>
                    </div>
                </div>

            </div>
        </div>
    </div>
</template>

<script>
import NavbarCliente from "../../components/NavbarCliente.vue";
import { actualizarUsuario } from "../../services/api";
import "@/assets/styles/perfilview.css";
export default {
    components: { NavbarCliente },
    data() {
        return {
            nombre: localStorage.getItem("nombre") || "Cliente",
            rol: localStorage.getItem("rol") || "CLIENTE",
            id: localStorage.getItem("id") || "-",
            direcciones: [],
            agregando: false,
            nuevaDireccion: "",
            guardando: false,
            mensaje: ""
        };
    },
    computed: {
        iniciales() {
            return this.nombre.split(" ").map(n => n[0]).join("").toUpperCase().slice(0, 2);
        }
    },
    mounted() {
        // Cargar direcciones guardadas en localStorage
        const dirs = localStorage.getItem("direcciones");
        if (dirs) this.direcciones = JSON.parse(dirs);
    },
    methods: {
        async guardarDireccion() {
            if (!this.nuevaDireccion.trim()) return;

            this.guardando = true;
            try {
                this.direcciones.push(this.nuevaDireccion.trim());
                localStorage.setItem("direcciones", JSON.stringify(this.direcciones));

                // Guardar la última dirección agregada en el perfil del usuario
                await actualizarUsuario(this.id, {
                    nombre: this.nombre,
                    direccion: this.direcciones[this.direcciones.length - 1]
                });

                this.nuevaDireccion = "";
                this.agregando = false;
                this.mensaje = "Dirección guardada";
                setTimeout(() => this.mensaje = "", 3000);
            } catch {
                this.mensaje = "Error al guardar";
            }
            this.guardando = false;
        },

        eliminarDireccion(i) {
            this.direcciones.splice(i, 1);
            localStorage.setItem("direcciones", JSON.stringify(this.direcciones));
        },

        logout() {
            localStorage.clear();
            this.$router.push("/login");
        }
    }
}
</script>