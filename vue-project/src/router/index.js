import { createRouter, createWebHistory } from 'vue-router'


const moduleFiles = import.meta.glob('../modules/**/router.js', { eager: true })
let allModuleRoutes = []

for (const path in moduleFiles) {
    const mod = moduleFiles[path].default || []
    allModuleRoutes = allModuleRoutes.concat(mod)
}

const routes = [
    { path: '/', redirect: '/login' },
    { path: '/login', component: () => import('@/system/Login.vue') },
    { path: '/register', component: () => import('@/system/Register.vue') },
    { path: '/forgot-password', component: () => import('@/system/ForgotPassword.vue') },
    { path: '/exit', redirect: '/login' },

    // 首页布局
    { path: '/home', name: 'SysHome', component: () => import('@/system/SysMain.vue') },

    ...allModuleRoutes
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router