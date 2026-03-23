<template>
  <div id="questionSubmitView">
    <a-form :model="searchParams" layout="inline">
      <a-form-item field="questionId" label="题号" style="min-width: 240px">
        <a-input v-model="searchParams.questionId" placeholder="请输入" />
      </a-form-item>
      <a-form-item field="language" label="编程语言" style="min-width: 240px">
        <a-select
          v-model="searchParams.language"
          :style="{ width: '320px' }"
          placeholder="选择编程语言"
        >
          <a-option>java</a-option>
          <a-option>cpp</a-option>
          <a-option>go</a-option>
          <a-option>html</a-option>
        </a-select>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" @click="doSubmit">搜索</a-button>
      </a-form-item>
      <a-form-item>
        <a-button type="outline" @click="loadData" class="refresh-btn"><icon-refresh />刷新</a-button>
      </a-form-item>
    </a-form>
    <a-divider size="0" />
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
    >
      <template #status="{ record }">
        <a-tag :color="statusColorMap[record.status]" size="small">
          {{ statusMap[record.status] ?? '未知' }}
        </a-tag>
      </template>
      <template #judgeMessage="{ record }">
        <span v-if="record.judgeInfo?.message"
          :style="{ color: ['Accepted','成功','通过'].includes(record.judgeInfo.message) ? 'var(--color-success)' : 'var(--color-danger)' }">
          {{ record.judgeInfo.message }}
        </span>
        <span v-else style="color:var(--color-text-secondary)">-</span>
      </template>
      <template #judgeTime="{ record }">
        <span v-if="record.judgeInfo?.time" style="color:var(--color-text-secondary)">
          {{ record.judgeInfo.time }} ms
        </span>
        <span v-else style="color:var(--color-text-secondary)">-</span>
      </template>
      <template #questionTitle="{ record }">
        <a-link @click="toQuestionPage(record.questionVO)">{{ record.questionVO?.title ?? record.questionId }}</a-link>
      </template>
      <template #createTime="{ record }">
        {{ moment(record.createTime).format("YYYY-MM-DD HH:mm") }}
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watchEffect } from "vue";
import {
  Question,
  QuestionControllerService,
  QuestionSubmitQueryRequest,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import moment from "moment";

const tableRef = ref();

const dataList = ref([]);
const total = ref(0);
const searchParams = ref<QuestionSubmitQueryRequest>({
  questionId: undefined,
  language: undefined,
  pageSize: 10,
  current: 1,
});

const loadData = async () => {
  const res = await QuestionControllerService.listQuestionSubmitByPageUsingPost(
    {
      ...searchParams.value,
      sortField: "createTime",
      sortOrder: "descend",
    }
  );
  if (res.code === 0) {
    dataList.value = res.data.records;
    total.value = res.data.total;
  } else {
    message.error("加载失败，" + res.message);
  }
};

/**
 * 监听 searchParams 变量，改变时触发页面的重新加载
 */
watchEffect(() => {
  loadData();
});

/**
 * 页面加载时，请求数据
 */
onMounted(() => {
  loadData();
});

const statusMap: Record<number, string> = {
  0: "待判题",
  1: "判题中",
  2: "判题完成",
  3: "失败",
};

const statusColorMap: Record<number, string> = {
  0: "gray",
  1: "blue",
  2: "green",
  3: "red",
};

const columns = [
  {
    title: "题目",
    slotName: "questionTitle",
    width: 200,
  },
  {
    title: "编程语言",
    dataIndex: "language",
    width: 110,
  },
  {
    title: "判题状态",
    slotName: "status",
    width: 100,
  },
  {
    title: "判题结果",
    slotName: "judgeMessage",
    width: 120,
  },
  {
    title: "执行时间",
    slotName: "judgeTime",
    width: 100,
  },
  {
    title: "提交时间",
    slotName: "createTime",
    width: 140,
  },
];

const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};

const router = useRouter();

/**
 * 跳转到做题页面
 * @param question
 */
const toQuestionPage = (question: Question) => {
  router.push({
    path: `/view/question/${question.id}`,
  });
};

/**
 * 确认搜索，重新加载数据
 */
const doSubmit = () => {
  // 这里需要重置搜索页号
  searchParams.value = {
    ...searchParams.value,
    current: 1,
  };
};
</script>

<style scoped>
#questionSubmitView {
  max-width: 1280px;
  margin: 0 auto;
}

.refresh-btn {
  border-radius: 8px !important;
  border-color: var(--color-primary) !important;
  color: var(--color-primary) !important;
  gap: 4px;
}

.refresh-btn:hover {
  background: rgba(0, 102, 204, 0.08) !important;
}
</style>
