<template>
  <a-row id="globalHeader" align="center" :wrap="false">
    <a-col flex="auto">
      <a-menu
        mode="horizontal"
        :selected-keys="selectedKeys"
        @menu-item-click="doMenuClick"
      >
        <a-menu-item
          key="0"
          :style="{ padding: 0, marginRight: '38px' }"
          disabled
        >
          <div class="title-bar">
            <img class="logo" src="../assets/oj-logo.svg" />
            <div class="title">C OJ</div>
          </div>
        </a-menu-item>
        <a-menu-item v-for="item in visibleRoutes" :key="item.path">
          {{ item.name }}
        </a-menu-item>
      </a-menu>
    </a-col>
    <a-col flex="100px">
      <a-dropdown trigger="hover">
        <div class="user-info">
          {{ store.state.user?.loginUser?.userName ?? "未登录" }}
          <icon-down />
        </div>
        <template #content>
          <a-doption v-if="store.state.user?.loginUser" @click="goToProfile">
            <template #icon>
              <icon-user />
            </template>
            个人信息
          </a-doption>
          <a-doption v-if="store.state.user?.loginUser" @click="handleLogout">
            <template #icon>
              <icon-export />
            </template>
            退出登录
          </a-doption>
          <a-doption v-else @click="goToLogin">
            <template #icon>
              <icon-import />
            </template>
            去登录
          </a-doption>
        </template>
      </a-dropdown>
    </a-col>
  </a-row>
</template>

<script setup lang="ts">
import { routes } from "../router/routes";
import { useRoute, useRouter } from "vue-router";
import { computed, ref } from "vue";
import { useStore } from "vuex";
import checkAccess from "@/access/checkAccess";
import ACCESS_ENUM from "@/access/accessEnum";
import { UserControllerService } from "../../generated";

const router = useRouter();
const store = useStore();

const goToProfile = () => {
  router.push("/user/profile");
};

const handleLogout = async () => {
  const res = await UserControllerService.userLogoutUsingPost();
  if (res.code === 0) {
    store.commit("user/setLoginUser", null);
    router.push("/user/login");
  }
};

const goToLogin = () => {
  router.push("/user/login");
};

// 展示在菜单的路由数组
const visibleRoutes = computed(() => {
  return routes.filter((item, index) => {
    if (item.meta?.hideInMenu) {
      return false;
    }
    // 根据权限过滤菜单
    if (
      !checkAccess(store.state.user.loginUser, item?.meta?.access as string)
    ) {
      return false;
    }
    return true;
  });
});

// 默认主页
const selectedKeys = ref(["/"]);

// 路由跳转后，更新选中的菜单项
router.afterEach((to, from, failure) => {
  selectedKeys.value = [to.path];
});

console.log();

const doMenuClick = (key: string) => {
  router.push({
    path: key,
  });
};
</script>

<style scoped>
.title-bar {
  display: flex;
  align-items: center;
}

.title {
  color: #444;
  margin-left: 16px;
}

.logo {
  height: 48px;
}
</style>
