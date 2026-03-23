<template>
  <div class="user-profile-container">
    <h2 class="profile-title">用户详细信息</h2>
    <a-descriptions :column="2" bordered :label-style="{ width: '120px' }">
      <a-descriptions-item label="用户昵称">
        {{ userData.userName || "暂无昵称" }}
      </a-descriptions-item>
      <a-descriptions-item label="用户头像">
        <a-avatar :size="80" class="avatar-container">
          <img v-if="userData.userAvatar" :src="userData.userAvatar" />
          <template v-else>
            <span class="avatar-text">{{ userInitial }}</span>
          </template>
        </a-avatar>
      </a-descriptions-item>
      <a-descriptions-item label="用户身份">
        <a-badge :status="roleStatus" :text="userRoleMap[userData.userRole]" />
      </a-descriptions-item>
      <a-descriptions-item label="注册时间">
        <clock-circle-outlined />
        {{ formatTime(userData.createTime) }}
      </a-descriptions-item>
      <a-descriptions-item label="用户简介">
        {{ userData.userProfile }}
      </a-descriptions-item>
    </a-descriptions>
    <div class="action-buttons">
      <a-button type="primary" @click="handleEdit">编辑资料</a-button>
      <a-button @click="handleBack">返回首页</a-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { ClockCircleOutlined } from "@ant-design/icons-vue";
import { UserControllerService } from "../../../generated";
import { useStore } from "vuex";
import ACCESS_ENUM from "@/access/accessEnum";
import { formatTime } from "@/utils/time";
import { useRouter } from "vue-router";

const router = useRouter();
const store = useStore();
const userData = ref<any>({});

const userInitial = computed(
  () => userData.value.userAccount?.charAt(0).toUpperCase() || "U"
);

const roleStatus = computed(() => {
  return userData.value.userRole === ACCESS_ENUM.ADMIN ? "success" : "default";
});

const userRoleMap: Record<string, string> = {
  admin: "管理员",
  user: "普通用户",
  ban: "封禁用户",
  vip: "VIP用户",
};

onMounted(async () => {
  await store.dispatch("user/getLoginUser");
  const res = await UserControllerService.getLoginUserUsingGet();
  if (res.code === 0) userData.value = res.data;
});

const handleEdit = () => router.push("/user/profile/edit");
const handleBack = () => router.push("/");
</script>

<style scoped>
.user-profile-container {
  max-width: 1000px;
  margin: 30px auto;
  padding: 25px;
  background: #fff;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--color-border-light);
}

.profile-title {
  text-align: center;
  color: #1a3353;
  margin-bottom: 28px;
}

.avatar-container {
  background-color: #f0f2f5;
}

:deep(.avatar-text) {
  font-size: 24px;
  font-weight: 500;
  color: #1890ff;
}

.action-buttons {
  margin-top: 25px;
  text-align: center;
}

.action-buttons button {
  margin: 0 12px;
}
</style>
