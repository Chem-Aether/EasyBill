import { createRouter, createWebHistory } from 'vue-router'


// 定义路由
const routes = [
  {
    path: '/', // 默认路径
    redirect: '/login', // 重定向到 /login
  },
  {
    path: '/login', // 登录页面路径
    component: () => import("@/views/Login.vue"), 
  },
  {
    path: '/register',
    component: () => import("@/views/Register.vue"),
  },
  {
    path: '/home', // 主页路径
    component:() => import("@/views/HomeView.vue"), 
    children:[
      {path: '', component: () => import("@/views/home/Home.vue"),},
      {path: 'group', component: () => import("@/views/home/MyGroup.vue"),},
      {path: 'taskcenter', component: () => import("@/views/home/TaskCenter.vue"),},
      {path: 'mytask', component: () => import("@/views/home/MyTask.vue"),},
      {path: 'taskstatistics', component: () => import("@/views/home/TaskRelease.vue"),},
      {path: 'taskrelease', component: () => import("@/views/home/TaskRelease.vue"),},
      {path: 'taskapproval', component: () => import("@/views/home/TaskApproval.vue"),},
      {path: 'learncenter', component: () => import("@/views/home/LearnCenter.vue"),},
      {path: 'message', component: () => import("@/views/home/Message.vue"),},
      {path: '/help', component: () => import("@/views/Help.vue"), },
    ]
  },

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

// router.beforeEach((to, from, next) => {
//   if (to.path === '/login') {
//     next();
//   }
//   if(to.path !== '/login') {
//     return next("/login");
//   }
//   next();
// })

export default router;
