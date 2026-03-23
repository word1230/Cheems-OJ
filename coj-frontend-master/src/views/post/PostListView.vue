<template>
  <div id="postListView">
    <a-row justify="space-between" align="center" style="margin-bottom: 16px">
      <a-col flex="auto">
        <a-form :model="searchParams" layout="inline">
          <a-form-item field="title" label="标题" style="min-width: 240px">
            <a-input v-model="searchParams.title" placeholder="请输入标题" />
          </a-form-item>
          <a-form-item field="tags" label="标签" style="min-width: 240px">
            <a-input-tag v-model="searchParams.tags" placeholder="请输入标签" />
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="doSearch">搜索</a-button>
          </a-form-item>
        </a-form>
      </a-col>
      <a-col flex="100px">
        <a-button type="primary" @click="router.push('/post/add')"
          >写帖子</a-button
        >
      </a-col>
    </a-row>

    <div class="post-list">
      <div
        v-for="post in dataList"
        :key="post.id"
        class="post-item"
        @click="toDetail(post)"
      >
        <!-- 左侧：作者头像 -->
        <div class="post-avatar">
          <a-avatar
            :size="40"
            :style="{ backgroundColor: avatarColor(post.user?.userName) }"
          >
            {{ (post.user?.userName || "匿名").charAt(0).toUpperCase() }}
          </a-avatar>
        </div>

        <!-- 中间：标题 + 摘要 + 标签 -->
        <div class="post-main">
          <div class="post-title">{{ post.title }}</div>
          <div class="post-summary">{{ summary(post.content) }}</div>
          <a-space wrap style="margin-top: 6px">
            <a-tag
              v-for="(tag, idx) of post.tagList"
              :key="idx"
              size="small"
              color="arcoblue"
              >{{ tag }}</a-tag
            >
          </a-space>
        </div>

        <!-- 右侧：作者名 + 点赞/收藏 + 时间 -->
        <div class="post-meta">
          <span class="post-author">{{ post.user?.userName || "匿名" }}</span>
          <div class="post-stats">
            <span><icon-thumb-up /> {{ post.thumbNum ?? 0 }}</span>
            <span><icon-star /> {{ post.favourNum ?? 0 }}</span>
          </div>
          <span class="post-time">{{ moment(post.createTime).fromNow() }}</span>
        </div>
      </div>

      <a-empty v-if="dataList.length === 0" description="暂无帖子" />
    </div>

    <div style="display: flex; justify-content: center; margin-top: 24px">
      <a-pagination
        :total="total"
        :page-size="searchParams.pageSize"
        :current="searchParams.current"
        show-total
        @change="onPageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watchEffect } from "vue";
import {
  PostControllerService,
  PostQueryRequest,
  PostVO,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import moment from "moment";
import "moment/locale/zh-cn";

moment.locale("zh-cn");

const router = useRouter();
const dataList = ref<PostVO[]>([]);
const total = ref(0);
const searchParams = ref<PostQueryRequest>({
  title: "",
  tags: [],
  pageSize: 10,
  current: 1,
});

const loadData = async () => {
  const res = await PostControllerService.listPostVoByPageUsingPost(
    searchParams.value
  );
  if (res.code === 0) {
    dataList.value = res.data?.records ?? [];
    total.value = res.data?.total ?? 0;
  } else {
    message.error("加载失败，" + res.message);
  }
};

watchEffect(() => {
  loadData();
});

onMounted(() => {
  loadData();
});

const doSearch = () => {
  searchParams.value = { ...searchParams.value, current: 1 };
};

const onPageChange = (page: number) => {
  searchParams.value = { ...searchParams.value, current: page };
};

const toDetail = (post: PostVO) => {
  router.push(`/post/${post.id}`);
};

/** 截取内容前 80 字作为摘要 */
const summary = (content?: string) => {
  if (!content) return "";
  const plain = content.replace(/[#*`>\-!()[]]/g, "").trim();
  return plain.length > 80 ? plain.slice(0, 80) + "…" : plain;
};

/** 根据用户名生成固定头像背景色 */
const colors = [
  "#165dff",
  "#0fc6c2",
  "#ff7d00",
  "#f5319d",
  "#7816ff",
  "#00b42a",
];
const avatarColor = (name?: string) => {
  if (!name) return colors[0];
  return colors[name.charCodeAt(0) % colors.length];
};
</script>

<style scoped>
#postListView {
  max-width: 1100px;
  margin: 0 auto;
}

.post-list {
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-lg);
  overflow: hidden;
  background: var(--color-surface);
  box-shadow: var(--shadow-sm);
}

.post-item {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  padding: 24px 28px;
  border-bottom: 1px solid #f2f3f5;
  cursor: pointer;
  transition: background 0.15s;
}

.post-item:last-child {
  border-bottom: none;
}

.post-item:hover {
  background: #f7f8fa;
}

.post-avatar {
  flex-shrink: 0;
  padding-top: 2px;
}

.post-main {
  flex: 1;
  min-width: 0;
}

.post-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--color-text);
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.post-item:hover .post-title {
  color: #165dff;
}

.post-summary {
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.6;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.post-meta {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
  min-width: 110px;
}

.post-author {
  font-size: 14px;
  color: var(--color-text-secondary);
  font-weight: 500;
}

.post-stats {
  display: flex;
  gap: 12px;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.post-time {
  font-size: 13px;
  color: var(--color-text-tertiary);
}
</style>
