import { RouteRecordRaw } from "vue-router";
import HomeView from "@/views/HomeView.vue";
import UserLayout from "@/layouts/UserLayout.vue";
import UserLoginView from "@/views/user/UserLoginView.vue";
import UserRegisterView from "@/views/user/UserRegisterView.vue";
import AdminView from "@/views/AdminView.vue";
import NoAuthView from "@/views/NoAuthView.vue";
import ACCESS_ENUM from "@/access/accessEnum";
import AddQuestionView from "@/views/question/AddQuestionView.vue";
import ManageQuestionView from "@/views/question/ManageQuestionView.vue";
import QuestionsView from "@/views/question/QuestionsView.vue";
import QuestionSubmitView from "@/views/question/QuestionSubmitView.vue";
import ViewQuestionView from "@/views/question/ViewQuestionView.vue";
import UserProfileView from "@/views/user/UserProfileView.vue";
import UserProfileEdit from "@/views/user/UserProfileEdit.vue";
import PostListView from "@/views/post/PostListView.vue";
import PostDetailView from "@/views/post/PostDetailView.vue";
import PostAddView from "@/views/post/PostAddView.vue";
import ManagePostView from "@/views/post/ManagePostView.vue";

export const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "主页",
    component: HomeView,
  },
  {
    path: "/user",
    name: "用户",
    component: UserLayout,
    children: [
      {
        path: "/user/login",
        name: "用户登录",
        component: UserLoginView,
      },
      {
        path: "/user/register",
        name: "用户注册",
        component: UserRegisterView,
      },
      {
        path: "/user/profile",
        name: "个人信息",
        component: UserProfileView,
        meta: {
          access: ACCESS_ENUM.USER,
          hideInMenu: true,
        },
      },
      {
        path: "/user/profile/edit",
        name: "用户信息编辑",
        component: UserProfileEdit,
      },
    ],
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/questions",
    name: "浏览题目",
    component: QuestionsView,
    meta: {
      access: ACCESS_ENUM.USER,
    },
  },
  {
    path: "/question_submit",
    name: "浏览提交",
    component: QuestionSubmitView,
  },
  {
    path: "/my/posts",
    name: "我的讨论",
    component: () => import('@/views/user/MyPostsView.vue'),
    meta: {
      access: ACCESS_ENUM.USER,
    },
  },
  {
    path: "/my/favours",
    name: "我的收藏",
    component: () => import('@/views/user/MyFavoursView.vue'),
    meta: {
      access: ACCESS_ENUM.USER,
    },
  },
  {
    path: "/view/question/:id",
    name: "在线做题",
    component: ViewQuestionView,
    props: true,
    meta: {
      access: ACCESS_ENUM.USER,
      hideInMenu: true,
    },
  },
  {
    path: "/add/question",
    name: "创建题目",
    component: AddQuestionView,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/update/question",
    name: "更新题目",
    component: AddQuestionView,
    meta: {
      access: ACCESS_ENUM.ADMIN,
      hideInMenu: true,
    },
  },
  {
    path: "/post",
    name: "讨论",
    component: PostListView,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/post/add",
    name: "发帖",
    component: PostAddView,
    meta: {
      access: ACCESS_ENUM.USER,
      hideInMenu: true,
    },
  },
  {
    path: "/post/edit",
    name: "编辑帖子",
    component: PostAddView,
    meta: {
      access: ACCESS_ENUM.USER,
      hideInMenu: true,
    },
  },
  {
    path: "/post/:id",
    name: "帖子详情",
    component: PostDetailView,
    props: true,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/manage/question/",
    name: "管理题目",
    component: ManageQuestionView,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/manage/post",
    name: "管理帖子",
    component: ManagePostView,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  // {
  //   path: "/hide",
  //   name: "隐藏页面",
  //   component: HomeView,
  //   meta: {
  //     hideInMenu: true,
  //   },
  // },
  {
    path: "/noAuth",
    name: "无权限",
    component: NoAuthView,
    meta: {
      hideInMenu: true,
    },
  },
  // {
  //   path: "/admin",
  //   name: "管理员可见",
  //   component: AdminView,
  //   meta: {
  //     access: ACCESS_ENUM.ADMIN,
  //   },
  // },
  // {
  //   path: "/about",
  //   name: "关于我的",
  //   // route level code-splitting
  //   // this generates a separate chunk (about.[hash].js) for this route
  //   // which is lazy-loaded when the route is visited.
  //   component: () =>
  //     import(/* webpackChunkName: "about" */ "../views/AboutView.vue"),
  // },
];
