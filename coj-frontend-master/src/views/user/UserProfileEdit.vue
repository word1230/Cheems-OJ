<template>
  <div class="user-edit-container">
    <a-page-header
      title="返回"
      @back="() => router.push('/user/profile')"
      style="margin-bottom: 24px"
    >
      <template #title> 编辑资料</template>
    </a-page-header>

    <a-form
      v-if="!loading.value"
      :model="formState"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 16 }"
      @submit="handleSubmit"
    >
      <!-- 用户昵称 -->
      <a-form-item
        label="用户昵称"
        name="userName"
        :rules="[{ required: true, message: '请输入昵称' }]"
      >
        <a-input v-model="formState.userName" />
      </a-form-item>

      <!-- 用户简介 -->
      <a-form-item
        label="用户简介"
        name="userProfile"
        :rules="[{ required: false, message: '请输入用户简介' }]"
      >
        <a-input v-model="formState.userProfile" />
      </a-form-item>

      <!-- 提交按钮 -->
      <a-form-item :wrapper-col="{ offset: 6, span: 16 }">
        <a-button type="primary" html-type="submit"> 保存修改</a-button>
      </a-form-item>
    </a-form>
    <div v-else>加载中...</div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, nextTick } from "vue";
import { message } from "ant-design-vue";
import { useRouter } from "vue-router";
import { UserControllerService } from "../../../generated";

const router = useRouter();

interface FormState {
  userName: string;
  userProfile: string;
}

// 表单状态
const formState = reactive({
  userName: "",
  userProfile: "",
} as FormState);

// 加载状态
const loading = ref(true);

const loadUserData = async () => {
  try {
    const res = await UserControllerService.getLoginUserUsingGet();
    console.log("[DEBUG] 接口原始响应:", res);
    if (res.code === 0 && res.data) {
      formState.userName = res.data.userName ?? "";
      formState.userProfile = res.data.userProfile ?? "";
      console.log("初始化后数据:", JSON.parse(JSON.stringify(formState)));
    }
  } catch (e) {
    console.error("加载用户数据失败:", e);
    message.error("数据加载失败");
  } finally {
    loading.value = false;
  }
};
onMounted(async () => {
  // 硬编码赋值
  loadUserData();
  // 等待 DOM 更新
  await nextTick();
  // 确保表单显示
  loading.value = false;
});

// 表单提交
const handleSubmit = async () => {
  console.log("开始提交++++++++++++++++++++++++");
  if (!formState.userName.trim()) {
    message.error("昵称不能为空");
    return;
  }
  try {
    const params = {
      userName: formState.userName,
      userProfile: formState.userProfile,
    };
    const res = await UserControllerService.updateMyUserUsingPost(params);
    if (res.code === 0) {
      message.success("更新成功");
      router.push("/user/profile");
    }
  } catch (e) {
    message.error("更新失败");
  }
};
</script>

<style scoped>
.user-edit-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 24px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>
