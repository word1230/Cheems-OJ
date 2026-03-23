<template>
  <div id="managePostView">
    <div class="page-header">
      <h1 class="page-title">帖子管理</h1>
      <p class="page-desc">管理所有用户发布的帖子</p>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <a-input
        v-model="searchParams.title"
        placeholder="搜索帖子标题"
        allow-clear
        size="large"
        class="search-input"
      >
        <template #prefix><icon-search /></template>
      </a-input>
      <a-button type="primary" size="large" class="search-btn" @click="doSearch">搜索</a-button>
    </div>

    <!-- 表格 -->
    <div class="table-wrapper">
      <a-table
        :columns="columns"
        :data="dataList"
        :pagination="{
          showTotal: true,
          pageSize: searchParams.pageSize,
          current: searchParams.current,
          total,
        }"
        @page-change="onPageChange"
        :bordered="false"
        :loading="loading"
      >
        <template #tags="{ record }">
          <a-space wrap>
            <a-tag v-for="(tag, idx) of record.tagList" :key="idx" color="arcoblue" size="small">{{ tag }}</a-tag>
          </a-space>
        </template>
        <template #content="{ record }">
          <span class="content-preview">{{ preview(record.content) }}</span>
        </template>
        <template #userId="{ record }">
          <span class="meta-text">{{ record.userId }}</span>
        </template>
        <template #createTime="{ record }">
          <span class="date-text">{{ moment(record.createTime).format('YYYY-MM-DD') }}</span>
        </template>
        <template #optional="{ record }">
          <a-space>
            <a-button type="primary" size="small" @click="openEdit(record)">编辑</a-button>
            <a-popconfirm
              content="确认删除该帖子？"
              type="warning"
              @ok="doDelete(record)"
            >
              <a-button status="danger" size="small">删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </a-table>
    </div>

    <!-- 编辑弹窗 -->
    <a-modal
      v-model:visible="editVisible"
      title="编辑帖子"
      @ok="doUpdate"
      @cancel="editVisible = false"
      ok-text="保存"
      cancel-text="取消"
      :width="640"
    >
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="标题" field="title">
          <a-input v-model="editForm.title" placeholder="请输入标题" />
        </a-form-item>
        <a-form-item label="标签" field="tags">
          <a-input-tag v-model="editForm.tags" placeholder="请输入标签" allow-clear />
        </a-form-item>
        <a-form-item label="内容" field="content">
          <a-textarea
            v-model="editForm.content"
            placeholder="请输入内容"
            :auto-size="{ minRows: 6, maxRows: 12 }"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watchEffect } from "vue";
import { PostControllerService, PostVO, PostUpdateRequest } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import moment from "moment";

const dataList = ref<PostVO[]>([]);
const total = ref(0);
const loading = ref(false);
const searchParams = ref({
  title: "",
  pageSize: 10,
  current: 1,
});

const loadData = async () => {
  loading.value = true;
  const res = await PostControllerService.listPostVoByPageUsingPost(searchParams.value);
  if (res.code === 0) {
    dataList.value = res.data?.records ?? [];
    total.value = res.data?.total ?? 0;
  } else {
    message.error("加载失败，" + res.message);
  }
  loading.value = false;
};

watchEffect(() => { loadData(); });
onMounted(() => { loadData(); });

const doSearch = () => {
  searchParams.value = { ...searchParams.value, current: 1 };
};

const onPageChange = (page: number) => {
  searchParams.value = { ...searchParams.value, current: page };
};

const doDelete = async (post: PostVO) => {
  const res = await PostControllerService.deletePostUsingPost({ id: post.id });
  if (res.code === 0) {
    message.success("删除成功");
    loadData();
  } else {
    message.error("删除失败，" + res.message);
  }
};

// 编辑
const editVisible = ref(false);
const editForm = ref<PostUpdateRequest>({ id: undefined, title: "", tags: [], content: "" });

const openEdit = (post: PostVO) => {
  editForm.value = {
    id: post.id,
    title: post.title ?? "",
    tags: post.tagList ?? [],
    content: post.content ?? "",
  };
  editVisible.value = true;
};

const doUpdate = async () => {
  const res = await PostControllerService.updatePostUsingPost(editForm.value);
  if (res.code === 0) {
    message.success("更新成功");
    editVisible.value = false;
    loadData();
  } else {
    message.error("更新失败，" + res.message);
  }
};

const preview = (content?: string) => {
  if (!content) return "";
  const plain = content.replace(/[#*`>!()]/g, "").trim();
  return plain.length > 60 ? plain.slice(0, 60) + "…" : plain;
};

const columns = [
  { title: "ID", dataIndex: "id", width: 80 },
  { title: "标题", dataIndex: "title", width: 180 },
  { title: "内容预览", slotName: "content" },
  { title: "标签", slotName: "tags", width: 160 },
  { title: "用户ID", slotName: "userId", width: 90 },
  { title: "创建时间", slotName: "createTime", width: 110 },
  { title: "操作", slotName: "optional", width: 130, align: "right" as const },
];
</script>

<style scoped>
#managePostView {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 28px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text);
  letter-spacing: -0.03em;
  margin: 0 0 6px;
}

.page-desc {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 0;
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  align-items: center;
}

.search-input {
  max-width: 320px;
}

.search-btn {
  border-radius: 10px !important;
}

.table-wrapper {
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--color-border-light);
}

.content-preview {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.meta-text {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.date-text {
  font-size: 13px;
  color: var(--color-text-secondary);
}
</style>
