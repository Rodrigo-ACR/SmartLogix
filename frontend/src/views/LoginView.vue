<template>
    <div class="login-page">

        <div class="login-left">
            <div class="login-brand">
                <span class="brand-icon">⚡</span>
                <h1>SmartLogix</h1>
                <p>La plataforma de gestión para PyMEs que venden en línea</p>
            </div>
            <div class="login-features">
                <div class="feature">🛍️ Gestiona tu catálogo</div>
                <div class="feature">📦 Controla tus pedidos</div>
                <div class="feature">🚚 Rastrea tus envíos</div>
            </div>
        </div>

        <div class="login-right">
            <div class="login-box">

                <div class="login-tabs">
                    <button :class="{ active: modo === 'login' }" @click="modo = 'login'">Iniciar sesión</button>
                    <button :class="{ active: modo === 'register' }" @click="modo = 'register'">Registrarse</button>
                </div>

                <!-- LOGIN -->
                <form v-if="modo === 'login'" @submit.prevent="hacerLogin">
                    <div class="input-group">
                        <label>Correo</label>
                        <input v-model="correo" type="email" placeholder="correo@ejemplo.cl" required />
                    </div>
                    <div class="input-group">
                        <label>Contraseña</label>
                        <input v-model="password" type="password" placeholder="••••••••" required />
                    </div>
                    <p v-if="error" class="error-msg">{{ error }}</p>
                    <button type="submit" class="btn btn-primary w-full" :disabled="loading">
                        {{ loading ? 'Ingresando...' : 'Ingresar' }}
                    </button>
                </form>

                <!-- REGISTER -->
                <form v-else @submit.prevent="hacerRegister">
                    <div class="input-group">
                        <label>Nombre</label>
                        <input v-model="reg.nombre" type="text" placeholder="Tu nombre" required />
                    </div>
                    <div class="input-group">
                        <label>Correo</label>
                        <input v-model="reg.correo" type="email" placeholder="correo@ejemplo.cl" required />
                    </div>
                    <div class="input-group">
                        <label>Contraseña</label>
                        <input v-model="reg.password" type="password" placeholder="••••••••" required />
                    </div>
                    <div class="input-group">
                        <label>Teléfono</label>
                        <input v-model="reg.telefono" type="text" placeholder="912345678" />
                    </div>
                    <div class="input-group">
                        <label>Dirección</label>
                        <input v-model="reg.direccion" type="text" placeholder="Calle 123" />
                    </div>
                    <p v-if="error" class="error-msg">{{ error }}</p>
                    <button type="submit" class="btn btn-primary w-full" :disabled="loading">
                        {{ loading ? 'Registrando...' : 'Crear cuenta' }}
                    </button>
                </form>

            </div>
        </div>

    </div>
</template>

<script>
import { login, register } from "../services/api";
import "@/assets/styles/loginview.css";
export default {
    data() {
        return {
            modo: "login",
            correo: "",
            password: "",
            error: "",
            loading: false,
            reg: { nombre: "", correo: "", password: "", telefono: "", direccion: "" }
        };
    },
    methods: {
        async hacerLogin() {
            this.loading = true;
            this.error = "";
try {
                const data = await login(this.correo, this.password);
                if (data.token) {
                    localStorage.setItem("token", data.token);
                    localStorage.setItem("rol", data.rol);
                    localStorage.setItem("nombre", data.nombre);
                    localStorage.setItem("id", data.id);
                    if (data.direccion) {
                        localStorage.setItem("direcciones", JSON.stringify([data.direccion]));
                    }
                    this.$router.push(data.rol === "ADMIN" ? "/admin" : "/inicio");
                } else {
                    this.error = "Credenciales incorrectas";
                }
            } catch {
                this.error = "Error al conectar con el servidor";
            }
            this.loading = false;
        },
        async hacerRegister() {
            this.loading = true;
            this.error = "";
            try {
                const data = await register(this.reg);
                if (data.id) {
                    this.modo = "login";
                    this.error = "";
                    alert("Cuenta creada. Inicia sesión.");
                } else {
                    this.error = data.mensaje || "Error al registrarse";
                }
            } catch {
                this.error = "Error al conectar con el servidor";
            }
            this.loading = false;
        }
    }
}
</script>
