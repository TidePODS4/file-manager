// Composables
import { createRouter, createWebHistory } from 'vue-router'
import keycloak from '@/plugins/keycloak';

const routes = [
  {
    path: '/',
    component: () => import('../layouts/HomeView'),
    meta: { requiresAuth: false },
    children: [
      {
        path: '/drive',
        name: 'Drive',
        // route level code-splitting
        // this generates a separate chunk (about.[hash].js) for this route
        // which is lazy-loaded when the route is visited.
        component: () => import(/* webpackChunkName: "home" */ '@/views/DriveView.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: '/drive/:folderId',
        name: 'Folder',
        // route level code-splitting
        // this generates a separate chunk (about.[hash].js) for this route
        // which is lazy-loaded when the route is visited.
        component: () => import(/* webpackChunkName: "folder" */ '@/views/DriveView.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: '/drive/search',
        name: 'Search',
        // route level code-splitting
        // this generates a separate chunk (about.[hash].js) for this route
        // which is lazy-loaded when the route is visited.
        component: () => import(/* webpackChunkName: "folder" */ '@/views/SearchView.vue'),
        meta: { requiresAuth: true },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
})

router.beforeEach((to, from, next) => {
  if (to.matched.some(record => record.meta.requiresAuth) && !keycloak.authenticated) {
    keycloak.login(); // перенаправление на страницу входа, если требуется аутентификация и пользователь не аутентифицирован
  } else {
    next(); // продолжение навигации, если не требуется аутентификация или пользователь аутентифицирован
  }
});

export default router
