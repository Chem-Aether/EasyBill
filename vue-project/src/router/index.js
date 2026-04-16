import { createRouter, createWebHistory } from 'vue-router'


// 定义路由
const routes = [
  {
    path: '/', // 默认路径
    redirect: '/login', // 重定向到 /login
  },
  {
    path: '/login', // 登录页面路径
    component: () => import("@/views/system/Login.vue"), 
  },
  {
    path: '/register',
    component: () => import("@/views/system/Register.vue"),
  },
  {
    path: '/forgot-password',
    component: () => import("@/views/system/ForgotPassword.vue"),
  },
  {
    path: '/home', // 主页面路径
    component:() => import("@/views/HomeView.vue"), 
    children:[
      {path: '', component: () => import("@/views/home/Home.vue"),},
      {path: 'group', component: () => import("@/views/home/MyGroup.vue"),},
      {path: 'Add', component: () => import("@/views/home/AddRecord.vue"),},
      {path: 'classify', component: () => import("@/views/home/Classify.vue"),},
      {path: 'query', component: () => import("@/views/home/QueryTable.vue"),},
      {path: 'bill', component: () => import("@/views/Bill/main.vue"),},
      {path: 'changepassword', component: () => import("@/views/system/ForgotPassword.vue"),},
      {path: '/help', component: () => import("@/views/system/Help.vue"), },
    ]
  },
    {
        path: '/travel',
        component: () => import("@/views/travel/main.vue"),
    },
    { path:'/trainTicket', component: () => import("@/views/travel/admin/trainTicket.vue"), },
    { path:'/flightTicket', component: () => import("@/views/travel/admin/flightTicket.vue"), },

  {
    path: '/exit', 
    redirect: '/login'
  }
];



// 创建路由实例
const router = createRouter({
  history: createWebHistory(), // 使用 HTML5 历史模式
  routes, // 路由配置
});

export default router;
