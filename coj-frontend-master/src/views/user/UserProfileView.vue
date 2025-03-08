<template>
  <div class="user-profile-container">
    <h2 class="profile-title">用户详细信息</h2>
    <a-descriptions :column="2" bordered :label-style="{ width: '120px' }">
      <a-descriptions-item label="用户昵称">
        {{ userData.userName || "暂无昵称" }}
      </a-descriptions-item>

      <!-- 扩展信息 -->
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

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <a-button type="primary" @click="handleEdit">编辑资料</a-button>
      <a-button @click="handleBack">返回首页</a-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { ClockCircleOutlined, ContactsOutlined } from "@ant-design/icons-vue";
import { UserControllerService } from "../../../generated";
import { useStore } from "vuex";
import ACCESS_ENUM from "@/access/accessEnum";
import { formatTime } from "@/utils/time";
import { useRouter } from "vue-router";

const router = useRouter();
const store = useStore();
const userData = ref<any>({});

// 计算属性
const userInitial = computed(
  () => userData.value.userAccount?.charAt(0).toUpperCase() || "U"
);

const roleStatus = computed(() => {
  return userData.value.userRole === ACCESS_ENUM.ADMIN ? "success" : "default";
});

// 角色映射
const userRoleMap = {
  [ACCESS_ENUM.NOT_LOGIN]: "未登录用户",
  [ACCESS_ENUM.USER]: "普通用户",
  [ACCESS_ENUM.ADMIN]: "系统管理员",
  [ACCESS_ENUM.VIP]: "VIP用户",
};

// 生命周期
onMounted(async () => {
  await loadUserData();
});

// 数据加载
const loadUserData = async () => {
  try {
    await store.dispatch("user/getLoginUser");
    const res = await UserControllerService.getLoginUserUsingGet();
    if (res.code === 0) {
      userData.value = res.data;
    }
  } catch (e) {
    console.error("数据加载失败:", e);
  }
};

// 操作处理
const handleEdit = () => {
  router.push("/user/profile/edit");
};

const handleBack = () => {
  router.push("/");
};
</script>

<style scoped>
.user-profile-container {
  max-width: 1000px;
  margin: 30px auto;
  padding: 25px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.profile-title {
  text-align: center;
  color: #1a3353;
  margin-bottom: 28px;
}

.avatar-container {
  background-color: #f0f2f5;

  :deep(.avatar-text) {
    font-size: 24px;
    font-weight: 500;
    color: #1890ff;
  }
}

.action-buttons {
  margin-top: 25px;
  text-align: center;

  button {
    margin: 0 12px;
  }
}

:deep(.ant-descriptions-item-label) {
  font-weight: 500;
  color: #595959;
}
</style>
