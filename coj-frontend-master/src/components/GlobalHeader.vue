<template>
  <div id="globalHeader">
    <div class="header-inner">
      <!-- Brand -->
      <div class="brand" @click="router.push('/')">
        <img class="logo" src="../assets/oj-logo.svg" alt="C OJ" />
        <span class="brand-name">C OJ</span>
      </div>

      <!-- Nav -->
      <nav class="nav">
        <a
          v-for="item in visibleRoutes"
          :key="item.path"
          class="nav-item"
          :class="{ active: selectedKeys.includes(item.path) }"
          @click="doMenuClick(item.path)"
        >
          {{ item.name }}
        </a>
      </nav>

      <!-- User -->
      <a-dropdown trigger="hover" position="br">
        <div class="user-trigger">
          <div class="avatar">
            {{ userInitial }}
          </div>
          <span class="user-name">{{ displayName }}</span>
          <svg class="chevron" width="12" height="12" viewBox="0 0 12 12" fill="none">
            <path d="M2 4l4 4 4-4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <template #content>
          <div class="dropdown-menu">
            <a-doption v-if="store.state.user?.loginUser?.userRole" @click="goToProfile">
              <template #icon><icon-user /></template>
              个人信息
            </a-doption>
            <a-doption v-if="store.state.user?.loginUser?.userRole" @click="handleLogout" class="danger-option">
              <template #icon><icon-export /></template>
              退出登录
            </a-doption>
            <a-doption v-else @click="goToLogin">
              <template #icon><icon-import /></template>
              去登录
            </a-doption>
          </div>
        </template>
      </a-dropdown>
    </div>
  </div>
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
const route = useRoute();
const store = useStore();

const displayName = computed(() => {
  const u = store.state.user?.loginUser;
  if (!u?.userRole || u.userRole === ACCESS_ENUM.NOT_LOGIN) return "未登录";
  return u.userName || "用户";
});

const userInitial = computed(() => {
  const name = displayName.value;
  if (name === "未登录") return "?";
  return name.charAt(0).toUpperCase();
});

const goToProfile = () => router.push("/user/profile");

const handleLogout = async () => {
  await UserControllerService.userLogoutUsingPost();
  store.commit("user/updateUser", { userRole: ACCESS_ENUM.NOT_LOGIN });
  router.push("/user/login");
};

const goToLogin = () => router.push("/user/login");

const visibleRoutes = computed(() =>
  routes.filter((item) => {
    if (item.meta?.hideInMenu) return false;
    if (!checkAccess(store.state.user.loginUser, item?.meta?.access as string)) return false;
    return true;
  })
);

const selectedKeys = ref([route.path]);

router.afterEach((to) => {
  selectedKeys.value = [to.path];
});

const doMenuClick = (key: string) => {
  router.push({ path: key });
};
</script>

<style scoped>
#globalHeader {
  height: 52px;
  display: flex;
  align-items: center;
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  padding: 0 24px;
  display: flex;
  align-items: center;
  gap: 0;
  height: 52px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  text-decoration: none;
  margin-right: 32px;
  flex-shrink: 0;
}

.logo {
  height: 24px;
  width: 24px;
}

.brand-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
  letter-spacing: -0.02em;
}

.nav {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
}

.nav-item {
  font-size: 14px;
  font-weight: 400;
  color: var(--color-text-secondary);
  padding: 4px 12px;
  border-radius: 6px;
  cursor: pointer;
  text-decoration: none;
  transition: all var(--transition);
  white-space: nowrap;
  user-select: none;
}

.nav-item:hover {
  color: var(--color-text);
  background: rgba(0, 0, 0, 0.05);
}

.nav-item.active {
  color: var(--color-primary);
  font-weight: 500;
  background: rgba(0, 102, 204, 0.08);
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 8px;
  transition: background var(--transition);
  flex-shrink: 0;
}

.user-trigger:hover {
  background: rgba(0, 0, 0, 0.05);
}

.avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, #0066cc, #0099ff);
  color: white;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-name {
  font-size: 14px;
  color: var(--color-text);
  font-weight: 400;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chevron {
  color: var(--color-text-tertiary);
}
</style>
