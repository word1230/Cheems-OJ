<template>
  <div id="postDetailView">
    <a-spin :loading="loading" style="width: 100%">
      <div v-if="post" class="detail-layout">
        <!-- Back -->
        <div class="back-bar">
          <span class="back-btn" @click="router.back()">
            <icon-left /> 返回
          </span>
        </div>

        <!-- Article -->
        <article class="article-card">
          <!-- Header -->
          <header class="article-header">
            <h1 class="article-title">{{ post.title }}</h1>
            <div class="article-meta">
              <div class="author-info">
                <a-avatar
                  :size="36"
                  :style="{ backgroundColor: avatarColor(post.user?.userName) }"
                >
                  {{ (post.user?.userName || '匿名').charAt(0).toUpperCase() }}
                </a-avatar>
                <div class="author-text">
                  <span class="author-name">{{ post.user?.userName || '匿名' }}</span>
                  <span class="article-time">
                    {{ moment(post.createTime).format('YYYY年MM月DD日') }}
                    · 约 {{ readTime }} 分钟阅读
                  </span>
                </div>
              </div>
            </div>
            <div class="tag-row" v-if="post.tagList?.length">
              <a-tag
                v-for="(tag, index) of post.tagList"
                :key="index"
                color="arcoblue"
              >{{ tag }}</a-tag>
            </div>
          </header>

          <!-- Divider -->
          <div class="article-divider" />

          <!-- Content -->
          <div class="article-body">
            <MdViewer :value="post.content || ''" />
          </div>

          <!-- Footer actions -->
          <div class="article-divider" />
          <footer class="article-footer">
            <div class="footer-actions">
              <button
                class="footer-btn"
                :class="{ active: post.hasThumb }"
                @click="doThumb"
              >
                <icon-thumb-up />
                <span>{{ post.hasThumb ? '已点赞' : '点赞' }}（{{ post.thumbNum ?? 0 }}）</span>
              </button>
              <button
                class="footer-btn"
                :class="{ active: post.hasFavour }"
                @click="doFavour"
              >
                <icon-star />
                <span>{{ post.hasFavour ? '已收藏' : '收藏' }}（{{ post.favourNum ?? 0 }}）</span>
              </button>
              <button
                v-if="isOwner"
                class="footer-btn"
                @click="router.push(`/post/edit?id=${post.id}`)"
              >
                <icon-edit />
                <span>编辑</span>
              </button>
            </div>
            <span class="footer-time">发布于 {{ moment(post.createTime).format('YYYY-MM-DD HH:mm') }}</span>
          </footer>
        </article>
      </div>
      <a-empty v-else-if="!loading" description="帖子不存在" />
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import {
  PostControllerService,
  PostFavourControllerService,
  PostThumbControllerService,
  PostVO,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import MdViewer from "@/components/MdViewer.vue";
import moment from "moment";
import { useStore } from "vuex";
import ACCESS_ENUM from "@/access/accessEnum";

const route = useRoute();
const router = useRouter();
const store = useStore();
const post = ref<PostVO | null>(null);
const loading = ref(true);

const isOwner = computed(() => {
  const loginUser = store.state.user?.loginUser;
  return loginUser?.id && post.value?.userId && String(loginUser.id) === String(post.value.userId);
});

const readTime = computed(() => {
  const text = post.value?.content || "";
  const words = text.replace(/[^\u4e00-\u9fa5a-zA-Z0-9]/g, "").length;
  return Math.max(1, Math.ceil(words / 300));
});

const loadData = async () => {
  loading.value = true;
  const res = await PostControllerService.getPostVoByIdUsingGet(
    route.params.id as any
  );
  if (res.code === 0) {
    post.value = res.data;
  } else {
    message.error("加载失败，" + res.message);
  }
  loading.value = false;
};

onMounted(() => { loadData(); });

const requireLogin = () => {
  const loginUser = store.state.user?.loginUser;
  if (!loginUser || loginUser.userRole === ACCESS_ENUM.NOT_LOGIN) {
    message.warning("请先登录");
    router.push(`/user/login?redirect=/post/${route.params.id}`);
    return false;
  }
  return true;
};

const doThumb = async () => {
  if (!requireLogin()) return;
  const res = await PostThumbControllerService.doThumbUsingPost({ postId: post.value?.id });
  if (res.code === 0) {
    post.value = {
      ...post.value,
      hasThumb: !post.value?.hasThumb,
      thumbNum: (post.value?.thumbNum ?? 0) + (post.value?.hasThumb ? -1 : 1),
    };
  } else {
    message.error("操作失败，" + res.message);
  }
};

const doFavour = async () => {
  if (!requireLogin()) return;
  const res = await PostFavourControllerService.doPostFavourUsingPost({ postId: post.value?.id });
  if (res.code === 0) {
    post.value = {
      ...post.value,
      hasFavour: !post.value?.hasFavour,
      favourNum: (post.value?.favourNum ?? 0) + (post.value?.hasFavour ? -1 : 1),
    };
  } else {
    message.error("操作失败，" + res.message);
  }
};

const colors = ["#0066cc", "#0fc6c2", "#ff7d00", "#f5319d", "#7816ff", "#00b42a"];
const avatarColor = (name?: string) => {
  if (!name) return colors[0];
  return colors[name.charCodeAt(0) % colors.length];
};
</script>

<style scoped>
#postDetailView {
  max-width: 860px;
  margin: 0 auto;
}

.detail-layout {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.back-bar {
  padding: 4px 0;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--color-text-secondary);
  cursor: pointer;
  padding: 6px 10px;
  border-radius: 8px;
  transition: all var(--transition);
  user-select: none;
}

.back-btn:hover {
  color: var(--color-primary);
  background: rgba(0,102,204,0.06);
}

.article-card {
  background: var(--color-surface);
  border-radius: var(--radius-xl);
  border: 1px solid var(--color-border-light);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.article-header {
  padding: 40px 48px 28px;
}

.article-title {
  font-size: 30px;
  font-weight: 700;
  color: var(--color-text);
  letter-spacing: -0.03em;
  line-height: 1.25;
  margin: 0 0 24px;
}

.article-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-text {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.author-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text);
}

.article-time {
  font-size: 13px;
  color: var(--color-text-tertiary);
}

.action-group {
  display: flex;
  gap: 8px;
}

.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 8px;
  border: 1px solid var(--color-border);
  background: transparent;
  color: var(--color-text-secondary);
  font-size: 14px;
  cursor: pointer;
  transition: all var(--transition);
}

.action-btn:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
  background: rgba(0,102,204,0.05);
}

.action-btn.active {
  border-color: var(--color-primary);
  color: var(--color-primary);
  background: rgba(0,102,204,0.08);
}

.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.article-divider {
  height: 1px;
  background: var(--color-border-light);
  margin: 0 48px;
}

.article-body {
  padding: 36px 48px;
  font-size: 16px;
  line-height: 1.8;
  color: var(--color-text);
}

.article-footer {
  padding: 24px 48px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.footer-actions {
  display: flex;
  gap: 12px;
}

.footer-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 10px;
  border: 1px solid var(--color-border);
  background: transparent;
  color: var(--color-text-secondary);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition);
}

.footer-btn:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
  background: rgba(0,102,204,0.05);
  transform: translateY(-1px);
}

.footer-btn.active {
  border-color: var(--color-primary);
  color: var(--color-primary);
  background: rgba(0,102,204,0.08);
}

.footer-time {
  font-size: 13px;
  color: var(--color-text-tertiary);
}

@media (max-width: 768px) {
  .article-header,
  .article-body,
  .article-footer {
    padding-left: 20px;
    padding-right: 20px;
  }
  .article-divider {
    margin: 0 20px;
  }
  .article-title {
    font-size: 22px;
  }
  .article-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
