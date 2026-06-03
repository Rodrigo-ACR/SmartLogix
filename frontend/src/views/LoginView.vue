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

<style scoped>
.login-page {
    display: flex;
    min-height: 100vh;
}

.login-left {
    flex: 1;
    background: linear-gradient(135deg, #0d0d18 0%, #1a0a2e 50%, #0a0a0f 100%);
    display: flex;
    flex-direction: column;
    justify-content: center;
    padding: 60px;
    position: relative;
    overflow: hidden;
}

.login-left::before {
    content: '';
    position: absolute;
    width: 400px;
    height: 400px;
    background: radial-gradient(circle, var(--accent-glow) 0%, transparent 70%);
    top: -100px;
    right: -100px;
}

.login-brand .brand-icon {
    font-size: 3rem;
    display: block;
    margin-bottom: 16px;
}

.login-brand h1 {
    font-size: 3.5rem;
    background: linear-gradient(135deg, #fff 0%, var(--accent) 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    margin-bottom: 16px;
}

.login-brand p {
    color: var(--text-secondary);
    font-size: 1.1rem;
    max-width: 360px;
    line-height: 1.6;
}

.login-features {
    margin-top: 48px;
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.feature {
    color: var(--text-secondary);
    font-size: 1rem;
    display: flex;
    align-items: center;
    gap: 10px;
}

.login-right {
    width: 480px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 40px;
    background: var(--bg-secondary);
}

.login-box {
    width: 100%;
    max-width: 380px;
}

.login-tabs {
    display: flex;
    gap: 4px;
    background: var(--bg-card);
    border-radius: var(--radius);
    padding: 4px;
    margin-bottom: 28px;
}

.login-tabs button {
    flex: 1;
    padding: 10px;
    border-radius: 8px;
    background: transparent;
    color: var(--text-secondary);
    font-size: 0.9rem;
    font-weight: 500;
}

.login-tabs button.active {
    background: var(--accent);
    color: white;
}

form {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.w-full {
    width: 100%;
}

.error-msg {
    color: var(--danger);
    font-size: 0.85rem;
    text-align: center;
}

@media (max-width: 768px) {
    .login-left {
        display: none;
    }

    .login-right {
        width: 100%;
    }
}
</style>