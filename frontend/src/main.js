import { createApp } from 'vue'

import App from './App.vue'

import router from './router'

import './assets/styles/main.css'
import './assets/styles/navbar.css'
import './assets/styles/login.css'

createApp(App)
    .use(router)
    .mount('#app')