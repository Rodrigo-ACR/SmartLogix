<template>
    <span class="icon-wrap" :style="{ width: size + 'px', height: size + 'px' }">

        <!-- Flecha diagonal (original) -->
        <svg v-if="name === 'arrow'" :viewBox="'0 0 91 91'" xmlns="http://www.w3.org/2000/svg" :width="size"
            :height="size">
            <path :fill="color"
                d="M5.1,72c0.9,0.5,2,0.7,3.2,0.5c10.2-1.7,20.3-3.5,30.4-5.3c0.7,5.2,1.3,10.4,1.9,15.6c0.4,3.6,5.1,6.7,8,3.3C61,71.9,74.9,59.3,88.8,46.5c2.4-2.3,1.7-6.4-1-8.1C72.7,29.7,58.6,19.7,47,6.6C44,3.3,38.9,6.1,38.7,10c-0.3,6.2-0.7,12.3-1,18.5c-5.2-0.4-10.5-0.7-15.7-1.1c-6-0.4-13-1.9-18.7,0.3c-1.6,0.6-2.1,2.8-0.5,3.8c5.3,3.2,12.4,3,18.4,3.7c6.9,0.8,13.8,1.5,20.7,2.3c2.5,0.3,4.1-2.2,4.4-4.3c0.4-4,0.8-8,1.2-12.1c8.8,8.6,18.8,16,29.3,22.6c-9.7,8.9-19.2,18.2-28,28c-0.4-3.3-0.8-6.6-1.1-10c-0.4-3.3-2.7-5.1-6-4.6c-10.2,1.7-20.4,3.4-30.6,5.1c-0.6-5.6-2-11.1-2.8-16.7c-0.6-4.5-7-2.8-7.4,1c-0.8,7.1,0.2,13.9,0.8,21C1.7,70,3.3,71.5,5.1,72z" />
        </svg>

        <!-- Flecha izquierda (paginación) -->
        <svg v-else-if="name === 'arrow-left'" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"
            :width="size" :height="size">
            <path d="M14.5 17L9.5 12L12 9.5L14.5 7" :stroke="color" stroke-width="1.5" stroke-linecap="round"
                stroke-linejoin="round" />
            <path d="M14.5 17L9.5 12L12 9.5" :stroke="color" stroke-width="1.5" stroke-linecap="round"
                stroke-linejoin="round" />
        </svg>

        <!-- Flecha derecha (paginación) -->
        <svg v-else-if="name === 'arrow-right'" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"
            :width="size" :height="size">
            <path d="M9.5 7L14.5 12L12 14.5L9.5 17" :stroke="color" stroke-width="1.5" stroke-linecap="round"
                stroke-linejoin="round" />
            <path d="M9.5 7L14.5 12L12 14.5" :stroke="color" stroke-width="1.5" stroke-linecap="round"
                stroke-linejoin="round" />
        </svg>

    </span>
</template>

<script>
export default {
    name: "Icons",
    props: {
        name: { type: String, default: "arrow" },
        size: { type: Number, default: 18 },
        color: { type: String, default: "#7c5cfc" }
    }
}
</script>

<style scoped>
.icon-wrap {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
}
</style>