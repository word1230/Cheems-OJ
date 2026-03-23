<template>
  <div id="viewQuestionView">
    <a-row :gutter="[20, 20]">
      <!-- Left: Question detail -->
      <a-col :md="12" :xs="24">
        <div class="panel">
          <a-tabs default-active-key="question" class="detail-tabs">
            <a-tab-pane key="question" title="题目">
              <div v-if="question" class="question-detail">
                <div class="question-header">
                  <h2 class="question-title">{{ question.title }}</h2>
                  <a-space wrap class="question-tags">
                    <a-tag
                      v-for="(tag, index) of question.tags"
                      :key="index"
                      color="arcoblue"
                      >{{ tag }}</a-tag
                    >
                  </a-space>
                </div>
                <div class="judge-config">
                  <div class="config-item">
                    <span class="config-label">时间限制</span>
                    <span class="config-value"
                      >{{ question.judgeConfig?.timeLimit ?? 0 }} ms</span
                    >
                  </div>
                  <div class="config-item">
                    <span class="config-label">内存限制</span>
                    <span class="config-value"
                      >{{ question.judgeConfig?.memoryLimit ?? 0 }} MB</span
                    >
                  </div>
                  <div class="config-item">
                    <span class="config-label">堆栈限制</span>
                    <span class="config-value"
                      >{{ question.judgeConfig?.stackLimit ?? 0 }} MB</span
                    >
                  </div>
                </div>
                <div class="question-content">
                  <MdViewer :value="question.content || ''" />
                </div>
              </div>
            </a-tab-pane>
            <a-tab-pane key="answer" title="答案">暂时无法查看答案</a-tab-pane>
            <a-tab-pane key="comment" title="评论" disabled>评论区</a-tab-pane>
          </a-tabs>
        </div>
      </a-col>

      <!-- Right: Code editor -->
      <a-col :md="12" :xs="24">
        <div class="panel editor-panel">
          <div class="editor-toolbar">
            <span class="toolbar-label">编程语言</span>
            <a-select
              v-model="form.language"
              :style="{ width: '160px' }"
              size="small"
            >
              <a-option>java</a-option>
              <a-option>cpp</a-option>
              <a-option>go</a-option>
              <a-option>html</a-option>
            </a-select>
          </div>
          <div class="editor-body">
            <CodeEditor
              :value="form.code as string"
              :language="form.language"
              :handle-change="changeCode"
            />
          </div>
          <div class="editor-footer">
            <a-button
              type="primary"
              size="large"
              class="submit-btn"
              @click="doSubmit"
            >
              提交代码
            </a-button>
          </div>
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, defineProps } from "vue";
import {
  QuestionControllerService,
  QuestionSubmitAddRequest,
  QuestionVO,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import CodeEditor from "@/components/CodeEditor.vue";
import MdViewer from "@/components/MdViewer.vue";

const props = defineProps({ id: { type: String, required: true } });

const question = ref<QuestionVO>();

const loadData = async () => {
  const res = await QuestionControllerService.getQuestionVoByIdUsingGet(
    props.id as any
  );
  if (res.code === 0) {
    question.value = res.data;
  } else {
    message.error("加载失败，" + res.message);
  }
};

const form = ref<QuestionSubmitAddRequest>({
  language: "java",
  code: "",
});

const doSubmit = async () => {
  if (!question.value?.id) return;
  const res = await QuestionControllerService.doQuestionSubmitUsingPost({
    ...form.value,
    questionId: question.value.id,
  });
  if (res.code === 0) {
    message.success("提交成功");
  } else {
    message.error("提交失败，" + res.message);
  }
};

onMounted(() => {
  loadData();
});

const changeCode = (value: string) => {
  form.value.code = value;
};
</script>

<style scoped>
#viewQuestionView {
  max-width: 1400px;
  margin: 0 auto;
}

.panel {
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border-light);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
}

.detail-tabs {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.detail-tabs :deep(.arco-tabs-content) {
  flex: 1;
  overflow-y: auto;
  padding: 0;
}

.detail-tabs :deep(.arco-tabs-nav) {
  padding: 0 20px;
  border-bottom: 1px solid var(--color-border-light);
}

.question-detail {
  padding: 24px;
}

.question-header {
  margin-bottom: 20px;
}

.question-title {
  font-size: 22px;
  font-weight: 700;
  color: var(--color-text);
  letter-spacing: -0.02em;
  margin: 0 0 12px;
}

.question-tags {
  margin-bottom: 4px;
}

.judge-config {
  display: flex;
  gap: 24px;
  padding: 16px 20px;
  background: var(--color-bg);
  border-radius: var(--radius-md);
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.config-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.config-label {
  font-size: 11px;
  color: var(--color-text-tertiary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  font-weight: 500;
}

.config-value {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text);
}

.question-content {
  font-size: 15px;
  line-height: 1.7;
  color: var(--color-text);
}

.editor-panel {
  flex-direction: column;
}

.editor-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-bottom: 1px solid var(--color-border-light);
  background: var(--color-surface);
  flex-shrink: 0;
}

.toolbar-label {
  font-size: 13px;
  color: var(--color-text-secondary);
  font-weight: 500;
}

.editor-body {
  flex: 1;
  overflow: hidden;
}

.editor-footer {
  padding: 12px 16px;
  border-top: 1px solid var(--color-border-light);
  display: flex;
  justify-content: flex-end;
  background: var(--color-surface);
  flex-shrink: 0;
}

.submit-btn {
  border-radius: 10px !important;
  padding: 0 28px !important;
  font-weight: 500 !important;
}
</style>
