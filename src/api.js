import axios from "axios";
import { ref, watch } from 'vue'
import { updateToken } from '@/plugins/keycloak-util'
import { useRoute, useRouter } from 'vue-router'
const AUTHORIZATION_HEADER = 'Authorization'


const api = axios.create({
    baseURL: 'http://localhost:8081', // Замените на базовый URL вашего API
  });
  const token = ref('')
  const route = useRoute();
  const router = useRouter();
  
  api.interceptors.request.use(async config => {
  token.value = await updateToken();
//   if (!config.headers) {
//       config.headers = {};
//   }
//   if (!config.headers.common) {
//       config.headers.common = {};
//   }
  config.headers[AUTHORIZATION_HEADER] = `Bearer ${token.value}`;
  return config;
}); 
  
//   api.interceptors.response.use( (response) => {
//     return response
//   }, error => {
//     return new Promise((resolve, reject) => {
//       router.push('/error')
//       reject(error)
//     })
//   })
  
  watch(() => route, () => {
    updateToken()
  })

  export default api;