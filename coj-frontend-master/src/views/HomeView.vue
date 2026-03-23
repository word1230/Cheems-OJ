<template>
  <div class="home-container">
    <div class="hero">
      <div class="hero-inner">
        <div class="hero-text">
          <h1 class="hero-title">挑战代码，<br />超越自我</h1>
          <p class="hero-desc">
            在这里，你可以挑战各种编程题目，提升你的算法思维与工程能力。
          </p>
          <div class="hero-actions">
            <a-button type="primary" size="large" class="btn-primary" @click="router.push('/questions')">
              开始做题
            </a-button>
            <a-button size="large" class="btn-secondary" @click="router.push('/user/profile')">
              用户中心
            </a-button>
          </div>
        </div>
        <div class="hero-stats">
          <div class="stat-card">
            <div class="stat-value">{{ userStats.solvedProblems }}</div>
            <div class="stat-label">已解决题目</div>
          </div>
          <div class="stat-card">
            <div class="stat-value">{{ userStats.submitCount }}</div>
            <div class="stat-label">提交次数</div>
          </div>
          <div class="stat-card">
            <div class="stat-value">{{ userStats.passRate.toFixed(1) }}<span class="stat-unit">%</span></div>
            <div class="stat-label">通过率</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import { QuestionControllerService } from "../../generated";

const router = useRouter();
const store = useStore();

const statsData = ref({ submitCount: 0, acceptedCount: 0 });

onMounted(async () => {
  if (store.state.user?.loginUser?.id) {
    const res = await QuestionControllerService.getMyQuestionStatsUsingGet();
    if (res.code === 0 && res.data) {
      statsData.value = res.data;
    }
  }
});

const userStats = computed(() => ({
  solvedProblems: statsData.value.acceptedCount,
  submitCount: statsData.value.submitCount,
  passRate:
    statsData.value.submitCount > 0
      ? (statsData.value.acceptedCount / statsData.value.submitCount) * 100
      : 0,
}));
</script>

<style scoped>
.home-container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 24px;
}

.hero {
  padding: 80px 0 64px;
}

.hero-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 64px;
}

.hero-text {
  flex: 1;
  max-width: 520px;
}

.hero-title {
  font-size: 52px;
  font-weight: 700;
  color: var(--color-text);
  line-height: 1.1;
  letter-spacing: -0.04em;
  margin: 0 0 20px;
}

.hero-desc {
  font-size: 18px;
  color: var(--color-text-secondary);
  line-height: 1.65;
  margin: 0 0 40px;
  font-weight: 400;
}

.hero-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.btn-primary {
  height: 48px !important;
  padding: 0 28px !important;
  font-size: 15px !important;
  font-weight: 500 !important;
  border-radius: 10px !important;
}

.btn-secondary {
  height: 48px !important;
  padding: 0 28px !important;
  font-size: 15px !important;
  font-weight: 500 !important;
  border-radius: 10px !important;
  background: rgba(0,0,0,0.05) !important;
  border: none !important;
  color: var(--color-text) !important;
}

.btn-secondary:hover {
  background: rgba(0,0,0,0.09) !important;
}

.hero-stats {
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex-shrink: 0;
}

.stat-card {
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  padding: 24px 32px;
  min-width: 200px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--color-border-light);
  transition: box-shadow var(--transition);
}

.stat-card:hover {
  box-shadow: var(--shadow-md);
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  color: var(--color-text);
  letter-spacing: -0.03em;
  line-height: 1;
  margin-bottom: 6px;
}

.stat-unit {
  font-size: 20px;
  font-weight: 500;
}

.stat-label {
  font-size: 13px;
  color: var(--color-text-secondary);
  font-weight: 400;
}

@media (max-width: 768px) {
  .hero-inner {
    flex-direction: column;
    gap: 40px;
  }
  .hero-title {
    font-size: 36px;
  }
  .hero-stats {
    flex-direction: row;
    flex-wrap: wrap;
    width: 100%;
  }
  .stat-card {
    flex: 1;
    min-width: 120px;
    padding: 16px 20px;
  }
  .stat-value {
    font-size: 28px;
  }
}
</style>
