<template>
    <nav class="navbar">
        <h2>SmartLogix 🚀</h2>

        <div v-if="token">
            <span>{{ rol }}</span>
            <button @click="$emit('logout')">Salir</button>
        </div>
    </nav>
</template>

<script>
export default {
    props: ["token", "rol"]
}
</script>