<template>
  <div class="home-container">
    <!-- 欢迎模块 -->
    <div class="welcome-section">
      <a-row :gutter="48" align="bottom">
        <a-col :span="14">
          <h1 class="welcome-title">欢迎来到 C OJ</h1>
          <p class="welcome-desc">
            在这里，你可以挑战各种编程题目，提升你的编程能力
          </p>
          <div class="action-buttons">
            <a-button
              type="primary"
              size="large"
              @click="router.push('/questions')"
              class="start-button"
            >
              开始做题
            </a-button>
            <a-button
              type="outline"
              size="large"
              @click="router.push('/user/profile')"
              class="profile-button"
            >
              用户中心
            </a-button>
          </div>
        </a-col>

        <!-- 数据统计 -->
        <a-col :span="10">
          <a-space direction="vertical" size="large">
            <a-statistic
              :value="userStats.solvedProblems"
              title="已解决问题"
              show-group-separators
              class="stats-item"
            />
            <a-statistic
              :value="userStats.submitCount"
              title="提交次数"
              show-group-separators
              class="stats-item"
            />
            <a-statistic
              :value="userStats.passRate"
              :precision="1"
              suffix="%"
              title="通过率"
              class="stats-item"
            />
          </a-space>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";

const router = useRouter();
const store = useStore();

// 用户统计数据
const userStats = computed(() => ({
  solvedProblems: store.state.user?.loginUser?.acceptedNum || 0,
  submitCount: store.state.user?.loginUser?.submitNum || 0,
  passRate: store.state.user?.loginUser?.submitNum
    ? (store.state.user.loginUser.acceptedNum /
        store.state.user.loginUser.submitNum) *
      100
    : 0,
}));
</script>

<style scoped>
.home-container {
  background: #f8faff;
  min-height: calc(100vh - 128px);
  padding: 48px 24px;
}

.welcome-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 64px 48px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 82, 217, 0.08);
}

.welcome-title {
  font-size: 36px;
  color: #1d2129;
  margin-bottom: 24px;
}

.welcome-desc {
  font-size: 16px;
  color: #4e5969;
  line-height: 1.8;
  margin-bottom: 48px;
}

.action-buttons {
  display: flex;
  gap: 24px;
}

.start-button {
  background: #165dff;
  padding: 0 32px;
  height: 48px;
  font-size: 16px;
}

.profile-button {
  color: #165dff;
  border-color: #165dff;
  padding: 0 32px;
  height: 48px;
  font-size: 16px;
}

.stats-item {
  padding: 20px;
  background: #f8faff;
  border-radius: 8px;
  width: 280px;
}

.stats-item :deep(.arco-statistic-title) {
  color: #86909c;
  font-size: 14px;
}

.stats-item :deep(.arco-statistic-value) {
  font-size: 24px;
  color: #1d2129;
}
</style>
