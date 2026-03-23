<template>
  <div class="my-list-container">
    <div class="page-header">
      <h1 class="page-title">我的收藏</h1>
    </div>
    <a-spin :loading="loading" style="width:100%">
      <div class="list-wrapper">
        <a-empty v-if="!loading && list.length === 0" description="暂无收藏" style="padding: 60px 0" />
        <div
          v-for="post in list"
          :key="post.id"
          class="post-item"
          @click="router.push(`/post/${post.id}`)"
        >
          <div class="post-title">{{ post.title }}</div>
          <div class="post-meta">
            <a-space wrap>
              <a-tag v-for="(t, i) of post.tagList" :key="i" size="small" color="arcoblue">{{ t }}</a-tag>
            </a-space>
            <span class="post-time">{{ moment(post.createTime).format('YYYY-MM-DD') }}</span>
          </div>
        </div>
      </div>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { PostFavourControllerService, PostVO } from "../../../generated";
import moment from "moment";

const router = useRouter();
const list = ref<PostVO[]>([]);
const loading = ref(false);

onMounted(async () => {
  loading.value = true;
  const res = await PostFavourControllerService.listMyFavourPostByPageUsingPost({ pageSize: 20, current: 1 });
  if (res.code === 0) list.value = res.data?.records ?? [];
  loading.value = false;
});
</script>

<style scoped>
.my-list-container {
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text);
  margin: 0;
}

.list-wrapper {
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border-light);
  box-shadow: var(--shadow-sm);
  padding: 0 24px;
}

.post-item {
  padding: 16px 0;
  border-bottom: 1px solid var(--color-border-light);
  cursor: pointer;
  transition: background var(--transition);
}

.post-item:last-child {
  border-bottom: none;
}

.post-item:hover .post-title {
  color: var(--color-primary);
}

.post-title {
  font-size: 15px;
  font-weight: 500;
  color: var(--color-text);
  margin-bottom: 8px;
  transition: color var(--transition);
}

.post-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.post-time {
  font-size: 12px;
  color: var(--color-text-secondary);
  flex-shrink: 0;
}
</style>
