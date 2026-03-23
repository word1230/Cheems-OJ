<template>
  <div class="auth-card">
    <div class="auth-header">
      <h2 class="auth-title">欢迎回来</h2>
      <p class="auth-subtitle">登录你的 C OJ 账号</p>
    </div>
    <a-form
      class="auth-form"
      label-align="left"
      auto-label-width
      :model="form"
      @submit="handleSubmit"
    >
      <a-form-item field="userAccount" label="账号">
        <a-input
          v-model="form.userAccount"
          placeholder="请输入账号"
          size="large"
          allow-clear
        />
      </a-form-item>
      <a-form-item field="userPassword" label="密码" help="密码不少于 8 位">
        <a-input-password
          v-model="form.userPassword"
          placeholder="请输入密码"
          size="large"
        />
      </a-form-item>
      <a-form-item style="margin-top: 8px">
        <a-button
          type="primary"
          html-type="submit"
          long
          size="large"
          class="submit-btn"
        >
          登录
        </a-button>
      </a-form-item>
      <div class="auth-footer">
        <span>没有账号？</span>
        <router-link to="/user/register" class="auth-link">立即注册</router-link>
      </div>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import { UserControllerService, UserLoginRequest } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import { useStore } from "vuex";

const form = reactive({
  userAccount: "",
  userPassword: "",
} as UserLoginRequest);

const router = useRouter();
const store = useStore();

const handleSubmit = async () => {
  const res = await UserControllerService.userLoginUsingPost(form);
  if (res.code === 0) {
    setTimeout(async () => {
      await store.dispatch("user/getLoginUser");
      router.push({ path: "/", replace: true });
    }, 100);
  } else {
    message.error("登录失败，" + res.message);
  }
};
</script>

<style scoped>
.auth-card {
  background: var(--color-surface);
  border-radius: var(--radius-xl);
  padding: 48px 40px;
  width: 100%;
  max-width: 420px;
  box-shadow: var(--shadow-lg);
  border: 1px solid var(--color-border-light);
}

.auth-header {
  text-align: center;
  margin-bottom: 36px;
}

.auth-title {
  font-size: 26px;
  font-weight: 700;
  color: var(--color-text);
  letter-spacing: -0.03em;
  margin: 0 0 8px;
}

.auth-subtitle {
  font-size: 15px;
  color: var(--color-text-secondary);
  margin: 0;
}

.auth-form :deep(.arco-form-item-label-col) {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text);
}

.submit-btn {
  height: 48px !important;
  font-size: 15px !important;
  font-weight: 500 !important;
  border-radius: 10px !important;
  margin-top: 4px;
}

.auth-footer {
  text-align: center;
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-top: 8px;
}

.auth-link {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 500;
  margin-left: 4px;
  transition: opacity var(--transition);
}

.auth-link:hover {
  opacity: 0.75;
}
</style>
