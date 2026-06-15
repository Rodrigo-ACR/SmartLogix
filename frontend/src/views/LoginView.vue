<template>
    <div class="login-page">

        <div class="login-left">
            <div class="login-brand">
                <span class="brand-icon">⚡</span>
                <h1>SmartLogix</h1>
                <p>Plataforma inteligente para la gestión logística de eCommerce en PyMEs</p>
            </div>
            <div class="login-features">
                <div class="feature">📦 Inventario y stock en tiempo real</div>
                <div class="feature">🛒 Gestión de pedidos automatizada</div>
                <div class="feature">🚚 Trazabilidad completa de envíos</div>
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
                    <!-- Error normal (1-4 intentos) -->
                    <p v-if="error && errorTipo === 'normal'" class="error-msg">{{ error }}</p>

                    <!-- Error de bloqueo (5+ intentos) -->
                    <div v-if="error && errorTipo === 'bloqueo'" class="bloqueo-banner">
                        <div class="bloqueo-icon">🔒</div>
                        <div>
                            <strong>Demasiados intentos fallidos</strong>
                            <p>{{ error }}</p>
                            <p v-if="timerSegundos > 0" class="bloqueo-timer">Espera {{ timerSegundos }}s para volver a
                                intentar</p>
                        </div>
                    </div>

                    <!-- Error de servicio caído -->
                    <div v-if="error && errorTipo === 'servicio'" class="servicio-banner">
                        <span>⚠️</span>
                        <p>{{ error }}</p>
                    </div>

                    <button type="submit" class="btn btn-primary w-full" :disabled="loading || bloqueado">
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
                    <!-- Error normal (1-4 intentos) -->
                    <p v-if="error && errorTipo === 'normal'" class="error-msg">{{ error }}</p>

                    <!-- Error de bloqueo (5+ intentos) -->
                    <div v-if="error && errorTipo === 'bloqueo'" class="bloqueo-banner">
                        <div class="bloqueo-icon">🔒</div>
                        <div>
                            <strong>Demasiados intentos fallidos</strong>
                            <p>{{ error }}</p>
                            <p v-if="timerSegundos > 0" class="bloqueo-timer">Espera {{ timerSegundos }}s para volver a
                                intentar</p>
                        </div>
                    </div>

                    <!-- Error de servicio caído -->
                    <div v-if="error && errorTipo === 'servicio'" class="servicio-banner">
                        <span>⚠️</span>
                        <p>{{ error }}</p>
                    </div>

                    <button type="submit" class="btn btn-primary w-full" :disabled="loading || bloqueado">
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
            errorTipo: "normal",
            intentos: 0,
            bloqueado: false,
            timerSegundos: 0,
            loading: false,
            reg: { nombre: "", correo: "", password: "", telefono: "", direccion: "" }
        };
    },
    methods: {
        async hacerLogin() {
            if (this.bloqueado) return;
            this.loading = true;
            this.error = "";
            try {
                const data = await login(this.correo, this.password);
                if (data.codigo === 503 || data.mensaje?.includes("no disponible")) {
                    this.errorTipo = "servicio";
                    this.error = "Servicio de usuarios temporalmente no disponible. Intenta en unos momentos.";
                } else if (data.token) {
                    this.intentos = 0;
                    localStorage.setItem("token", data.token);
                    localStorage.setItem("rol", data.rol);
                    localStorage.setItem("nombre", data.nombre);
                    localStorage.setItem("id", data.id);
                    if (data.direccion) {
                        localStorage.setItem("direcciones", JSON.stringify([data.direccion]));
                    }
                    this.$router.push(data.rol === "ADMIN" ? "/admin" : "/inicio");
                } else {
                    this.intentos++;
                    if (this.intentos >= 5) {
                        this.errorTipo = "bloqueo";
                        this.error = "Has superado el límite de intentos. Verifica tus credenciales o espera antes de volver a intentarlo.";
                        this.iniciarBloqueo(30);
                    } else {
                        this.errorTipo = "normal";
                        this.error = "Credenciales incorrectas";
                    }
                }
            } catch {
                this.errorTipo = "servicio";
                this.error = "Servicio de usuarios temporalmente no disponible. Intenta en unos momentos.";
            }
            this.loading = false;
        },
        iniciarBloqueo(segundos) {
            this.bloqueado = true;
            this.timerSegundos = segundos;
            const timer = setInterval(() => {
                this.timerSegundos--;
                if (this.timerSegundos <= 0) {
                    clearInterval(timer);
                    this.bloqueado = false;
                    this.intentos = 0;
                    this.error = "";
                    this.errorTipo = "normal";
                }
            }, 1000);
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
                this.error = "⚠️ Servicio de usuarios temporalmente no disponible. Intenta en unos momentos.";
            }
            this.loading = false;
        }
    }
}
</script>