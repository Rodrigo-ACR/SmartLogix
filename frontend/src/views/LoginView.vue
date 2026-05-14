<template>
    <div class="login-container">

        <h1>SmartLogix 🚀</h1>

        <LoginForm @login="hacerLogin" />

    </div>
</template>

<script>
import LoginForm from "../components/LoginForm.vue";
import { login } from "../services/api";

export default {

    components: {
        LoginForm
    },

    methods: {

        async hacerLogin(user) {

            const data = await login(user);

            if (data.token) {

                localStorage.setItem("token", data.token);
                localStorage.setItem("rol", data.rol);

                this.$router.push("/dashboard");
            }
        }
    }
}
</script>