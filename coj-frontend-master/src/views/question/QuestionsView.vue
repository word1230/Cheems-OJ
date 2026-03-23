<template>
  <div id="questionsView">
    <div class="page-header">
      <h1 class="page-title">题目列表</h1>
      <p class="page-desc">挑战各种难度的编程题目，提升你的算法能力</p>
    </div>

    <div class="search-bar">
      <a-input
        v-model="searchParams.title"
        placeholder="搜索题目名称"
        allow-clear
        size="large"
        class="search-input"
      >
        <template #prefix>
          <icon-search />
        </template>
      </a-input>
      <a-input-tag
        v-model="searchParams.tags"
        placeholder="按标签筛选"
        size="large"
        class="tag-input"
        allow-clear
      />
      <a-button type="primary" size="large" class="search-btn" @click="doSubmit">
        搜索
      </a-button>
    </div>

    <div class="table-wrapper">
      <a-table
        :ref="tableRef"
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
        row-class="table-row"
      >
        <template #tags="{ record }">
          <a-space wrap>
            <a-tag
              v-for="(tag, index) of record.tags"
              :key="index"
              color="arcoblue"
              class="tag"
            >{{ tag }}</a-tag>
          </a-space>
        </template>
        <template #acceptedRate="{ record }">
          <span class="rate-text">
            {{ record.submitNum ? ((record.acceptedNum / record.submitNum) * 100).toFixed(1) : '0.0' }}%
            <span class="rate-sub">({{ record.acceptedNum }}/{{ record.submitNum }})</span>
          </span>
        </template>
        <template #createTime="{ record }">
          <span class="date-text">{{ moment(record.createTime).format('YYYY-MM-DD') }}</span>
        </template>
        <template #optional="{ record }">
          <a-button type="primary" size="small" class="solve-btn" @click="toQuestionPage(record)">
            去做题
          </a-button>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watchEffect } from "vue";
import {
  Question,
  QuestionControllerService,
  QuestionQueryRequest,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import moment from "moment";
import { useRouter } from "vue-router";

const tableRef = ref();
const dataList = ref([]);
const total = ref(0);
const searchParams = ref<QuestionQueryRequest>({
  title: "",
  tags: [],
  pageSize: 10,
  current: 1,
});

const loadData = async () => {
  const res = await QuestionControllerService.listQuestionVoByPageUsingPost(
    searchParams.value
  );
  if (res.code === 0) {
    dataList.value = res.data.records;
    total.value = res.data.total;
  } else {
    message.error("加载失败，" + res.message);
  }
};

watchEffect(() => { loadData(); });
onMounted(() => { loadData(); });

const columns = [
  { title: "题目名称", dataIndex: "title" },
  { title: "标签", slotName: "tags" },
  { title: "通过率", slotName: "acceptedRate", width: 160 },
  { title: "创建时间", slotName: "createTime", width: 120 },
  { slotName: "optional", width: 100, align: "right" },
];

const onPageChange = (page: number) => {
  searchParams.value = { ...searchParams.value, current: page };
};

const router = useRouter();
const toQuestionPage = (question: Question) => {
  router.push({ path: `/view/question/${question.id}` });
};

const doSubmit = () => {
  searchParams.value = { ...searchParams.value, current: 1 };
};
</script>

<style scoped>
#questionsView {
  max-width: 1100px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 32px;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: var(--color-text);
  letter-spacing: -0.03em;
  margin: 0 0 8px;
}

.page-desc {
  font-size: 15px;
  color: var(--color-text-secondary);
  margin: 0;
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  align-items: center;
}

.search-input {
  max-width: 280px;
}

.tag-input {
  max-width: 280px;
}

.search-btn {
  flex-shrink: 0;
  border-radius: 10px !important;
}

.table-wrapper {
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--color-border-light);
}

.rate-text {
  font-size: 14px;
  color: var(--color-text);
  font-weight: 500;
}

.rate-sub {
  font-size: 12px;
  color: var(--color-text-secondary);
  margin-left: 4px;
  font-weight: 400;
}

.date-text {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.solve-btn {
  border-radius: 6px !important;
  font-size: 13px !important;
}

.tag {
  font-size: 12px;
}

@media (max-width: 768px) {
  .search-bar {
    flex-direction: column;
    align-items: stretch;
  }
  .search-input,
  .tag-input {
    max-width: 100%;
  }
}
</style>
