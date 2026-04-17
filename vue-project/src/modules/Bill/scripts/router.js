export default [
    {
        path: '/bill',
        name: 'Bill',
        component: () => import('../main.vue'),
        redirect: '/bill/home',
        children: [
            { path: 'home', component: () => import('../views/Home.vue'), },
            { path: 'add', component: () => import('../views/AddRecord.vue'), },
            { path: 'query', component: () => import('../views/bills.vue'), },
            { path: 'classify', component: () => import('../views/QueryTable.vue'), },
            { path: 'account', component: () => import('../views/TaskRelease.vue'), },
        ]
    }
]