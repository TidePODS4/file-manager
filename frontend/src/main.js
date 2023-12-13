/**
 * main.js
 *
 * Bootstraps Vuetify and other plugins then mounts the App`
 */

// Components
import App from './App.vue'

// Composables
import { createApp } from 'vue'
// Plugins
const app = createApp(App)


import { registerPlugins } from '@/plugins'

registerPlugins(app)

app.mount('#app')


