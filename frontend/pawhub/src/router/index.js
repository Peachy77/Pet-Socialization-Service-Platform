import Vue from 'vue'
import VueRouter from 'vue-router'
//import HomeView from '../views/HomeView.vue'

Vue.use(VueRouter)

const routes = [
  // {
  //   path: '/',
  //   name: 'home',
  //   component: HomeView
  // },
  // {
  //   path: '/about',
  //   name: 'about',
  //   component: () => import('../views/AboutView.vue')
  // },
  {
    path: '/',
    name: 'login',
    component: () => import('../views/Users/LoginView.vue')
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../views/Users/RegisterView.vue')
  },
  {
    path: '/home',
    name: 'home',
    component: () => import('../views/Main/HomeView.vue')
  },
  {
    path: '/publish',
    name: 'publish',
    component: () => import('../views/Main/PublishView.vue')
  },
  {
    path:'/message',
    name:'message',
    component: () => import('../views/Main/MessageView.vue')
  },
  {
    path:'/message/details',
    name:'messagesDetails',
    component: () => import('../views/Messages/MessagesDetailsView.vue')
  },
  {
    path:'/mine',
    name:'mine',
    component: () => import('../views/Main/MineView.vue')
  },
  {
    path:'/search',
    name:'search',
    component: () => import('../views/Search/SearchView.vue')
  },
  {
    path:'/search/results',
    name:'searchResults',
    component: () => import('../views/Search/SearchResultsView.vue')
  },
  {
  path:'/service',
  component:()=>import('@/views/Service/ServiceDetailsView.vue')
  },
  {
  path:'/service/detail',
  name:'serviceDetail',
  component:()=>import('@/views/Service/ServiceMerchantView.vue')
  },
  {
  path:'/service/appointment',
  name:'serviceAppointment',
  component:()=>import('@/views/Service/AppointmentView.vue')
  },
  {
  path:'/post/detail',
  name:'postDetail',
  component:()=>import('@/views/Posts/PostDetailsView.vue')
  },
  {
  path:'/order/detail',
  name:'orderDetail',
  component:()=>import('@/views/Users/OrderDetailsView.vue')
  },
  {
  path: '/orders/:id',
  name: 'orderDetail',
  component: () => import('@/views/Users/OrderDetailsView.vue')
  },
  {
  path:'/users/focus',
  name:'focusList',
  component:()=>import('@/views/Users/FocusView.vue')
  },
  {
  path:'/users/fans',
  name:'fansList',
  component:()=>import('@/views/Users/FansView.vue')
  },
  {
  path:'/users/edit',
  name:'editProfile',
  component:()=>import('@/views/Users/EditView.vue')
  },
  {
  path:'/users/information',
  name:'userInformation',
  component:()=>import('@/views/Users/UserInformationView.vue')
  },
  {
  path:'/users/setting',
  name:'setting',
  component:()=>import('@/views/Users/SettingView.vue')
  }
]

const router = new VueRouter({
  routes
})

// 全局导航守卫
router.beforeEach((to, from, next) => {
  // 检查用户是否已登录（检查localStorage中的userId或token）
  const userId = localStorage.getItem('userId')
  const isLoggedIn = !!userId

  // 允许直接访问的路由（无需登录）
  const allowedWithoutLogin = ['login', 'register']
  const isPublicRoute = allowedWithoutLogin.includes(to.name)

  if (isLoggedIn) {
    // 已登录：允许访问所有页面
    next()
  } else {
    // 未登录
    if (isPublicRoute) {
      // 访问登录/注册页面：允许
      next()
    } else {
      // 访问其他页面：重定向到登录页
      next({ name: 'login' })
    }
  }
})

export default router
