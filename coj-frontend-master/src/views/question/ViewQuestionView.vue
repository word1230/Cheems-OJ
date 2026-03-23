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
                <div v-if="question.sampleInput" class="sample-input">
                  <div class="sample-label">示例输入</div>
                  <pre class="sample-code">{{ question.sampleInput }}</pre>
                </div>
                <div v-if="question.sampleOutput" class="sample-input">
                  <div class="sample-label">示例输出</div>
                  <pre class="sample-code">{{ question.sampleOutput }}</pre>
                </div>
                <div class="question-content">
                  <MdViewer :value="question.content || ''" />
                </div>
              </div>
            </a-tab-pane>
            <a-tab-pane key="answer" title="答案">暂时无法查看答案</a-tab-pane>
            <a-tab-pane key="discussion" title="讨论">
              <!-- 讨论列表 -->
              <div v-if="discussionView === 'list'" class="discussion-container">
                <div class="discussion-toolbar">
                  <span class="discussion-count">{{ discussionTotal }} 篇讨论</span>
                  <a-button type="primary" size="small" @click="discussionView = 'add'">发帖</a-button>
                </div>
                <a-spin :loading="discussionLoading" style="width:100%">
                  <div v-if="discussionList.length === 0 && !discussionLoading" class="discussion-empty">
                    <a-empty description="暂无讨论，来发第一篇吧" />
                  </div>
                  <div
                    v-for="post in discussionList"
                    :key="post.id"
                    class="discussion-item"
                    @click="openPost(post)"
                  >
                    <div class="discussion-item-title">{{ post.title }}</div>
                    <div class="discussion-item-meta">
                      <span>{{ post.user?.userName || '匿名' }}</span>
                      <span class="discussion-dot">·</span>
                      <span>{{ moment(post.createTime).fromNow() }}</span>
                      <span class="discussion-dot">·</span>
                      <icon-thumb-up style="font-size:12px" /> {{ post.thumbNum ?? 0 }}
                    </div>
                  </div>
                </a-spin>
              </div>

              <!-- 帖子详情 -->
              <div v-else-if="discussionView === 'detail'" class="discussion-container">
                <div class="discussion-detail-topbar">
                  <div class="discussion-back" @click="discussionView = 'list'"><icon-left /> 返回列表</div>
                  <button v-if="isPostOwner(activePost)" class="disc-btn" @click="openEditPost(activePost)">
                    <icon-edit /> 编辑
                  </button>
                </div>
                <div v-if="activePost" class="discussion-detail">
                  <h3 class="discussion-detail-title">{{ activePost.title }}</h3>
                  <div class="discussion-detail-meta">
                    <span>{{ activePost.user?.userName || '匿名' }}</span>
                    <span class="discussion-dot">·</span>
                    <span>{{ moment(activePost.createTime).format('YYYY-MM-DD HH:mm') }}</span>
                  </div>
                  <div v-if="activePost.tagList?.length" class="discussion-tags">
                    <a-tag v-for="(t, i) of activePost.tagList" :key="i" size="small" color="arcoblue">{{ t }}</a-tag>
                  </div>
                  <div class="discussion-detail-content">
                    <MdViewer :value="activePost.content || ''" />
                  </div>
                  <div class="discussion-detail-actions">
                    <button class="disc-btn" :class="{ active: activePost.hasThumb }" @click="doThumbPost">
                      <icon-thumb-up /> {{ activePost.hasThumb ? '已点赞' : '点赞' }}（{{ activePost.thumbNum ?? 0 }}）
                    </button>
                    <button class="disc-btn" :class="{ active: activePost.hasFavour }" @click="doFavourPost">
                      <icon-star /> {{ activePost.hasFavour ? '已收藏' : '收藏' }}（{{ activePost.favourNum ?? 0 }}）
                    </button>
                  </div>
                </div>
              </div>

              <!-- 发帖/编辑表单 -->
              <div v-else-if="discussionView === 'add'" class="discussion-container">
                <div class="discussion-back" @click="discussionView = 'list'"><icon-left /> 返回列表</div>
                <a-form :model="postForm" layout="vertical" class="discussion-form">
                  <a-form-item label="标题">
                    <a-input v-model="postForm.title" placeholder="请输入标题" />
                  </a-form-item>
                  <a-form-item label="标签">
                    <a-input-tag v-model="postForm.tags" placeholder="输入标签后按回车" allow-clear />
                  </a-form-item>
                  <a-form-item label="内容">
                    <MdEditor :value="postForm.content" :handle-change="(v: string) => postForm.content = v" />
                  </a-form-item>
                  <a-form-item>
                    <a-space>
                      <a-button type="primary" @click="doAddPost">{{ postForm.id ? '保存修改' : '发布' }}</a-button>
                      <a-button @click="discussionView = 'list'">取消</a-button>
                    </a-space>
                  </a-form-item>
                </a-form>
              </div>
            </a-tab-pane>
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
import { onMounted, ref, watch, defineProps, reactive, computed } from "vue";
import {
  QuestionControllerService,
  QuestionSubmitAddRequest,
  QuestionVO,
  PostControllerService,
  PostThumbControllerService,
  PostFavourControllerService,
  PostVO,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import CodeEditor from "@/components/CodeEditor.vue";
import MdViewer from "@/components/MdViewer.vue";
import MdEditor from "@/components/MdEditor.vue";
import { codeTemplates } from "@/utils/codeTemplates";
import moment from "moment";
import { useStore } from "vuex";

const store = useStore();

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
  code: codeTemplates["java"],
});

watch(
  () => form.value.language,
  (lang) => {
    form.value.code = codeTemplates[lang ?? "java"] ?? "";
  }
);

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

// ---- 讨论 Tab ----
const discussionView = ref<'list' | 'detail' | 'add'>('list');
const discussionList = ref<PostVO[]>([]);
const discussionTotal = ref(0);
const discussionLoading = ref(false);
const activePost = ref<PostVO | null>(null);
const postForm = reactive({ id: undefined as number | undefined, title: '', tags: [] as string[], content: '' });

const isPostOwner = (post: PostVO) => {
  const loginUser = store.state.user?.loginUser;
  return loginUser?.id && post.userId && String(loginUser.id) === String(post.userId);
};

const openEditPost = (post: PostVO) => {
  postForm.id = post.id;
  postForm.title = post.title ?? '';
  postForm.tags = post.tagList ?? [];
  postForm.content = post.content ?? '';
  discussionView.value = 'add';
};

const loadDiscussion = async () => {
  if (!question.value?.id) return;
  discussionLoading.value = true;
  const res = await PostControllerService.listPostVoByPageUsingPost({
    questionId: question.value.id,
    pageSize: 20,
    current: 1,
  });
  if (res.code === 0) {
    discussionList.value = res.data?.records ?? [];
    discussionTotal.value = res.data?.total ?? 0;
  }
  discussionLoading.value = false;
};

const openPost = (post: PostVO) => {
  activePost.value = post;
  discussionView.value = 'detail';
};

const doThumbPost = async () => {
  if (!activePost.value?.id) return;
  const res = await PostThumbControllerService.doThumbUsingPost({ postId: activePost.value.id });
  if (res.code === 0) {
    activePost.value = {
      ...activePost.value,
      hasThumb: !activePost.value.hasThumb,
      thumbNum: (activePost.value.thumbNum ?? 0) + (activePost.value.hasThumb ? -1 : 1),
    };
  } else {
    message.error('操作失败，' + res.message);
  }
};

const doFavourPost = async () => {
  if (!activePost.value?.id) return;
  const res = await PostFavourControllerService.doPostFavourUsingPost({ postId: activePost.value.id });
  if (res.code === 0) {
    activePost.value = {
      ...activePost.value,
      hasFavour: !activePost.value.hasFavour,
      favourNum: (activePost.value.favourNum ?? 0) + (activePost.value.hasFavour ? -1 : 1),
    };
  } else {
    message.error('操作失败，' + res.message);
  }
};

const doAddPost = async () => {
  if (!postForm.title.trim()) { message.warning('请输入标题'); return; }
  if (!postForm.content.trim()) { message.warning('请输入内容'); return; }
  let res;
  if (postForm.id) {
    res = await PostControllerService.editPostUsingPost({
      id: postForm.id,
      title: postForm.title,
      tags: postForm.tags,
      content: postForm.content,
    });
  } else {
    res = await PostControllerService.addPostUsingPost({
      title: postForm.title,
      tags: postForm.tags,
      content: postForm.content,
      questionId: question.value?.id,
    });
  }
  if (res.code === 0) {
    message.success(postForm.id ? '修改成功' : '发布成功');
    postForm.id = undefined;
    postForm.title = '';
    postForm.tags = [];
    postForm.content = '';
    discussionView.value = 'list';
    await loadDiscussion();
  } else {
    message.error((postForm.id ? '修改' : '发布') + '失败，' + res.message);
  }
};

watch(discussionView, (v) => {
  if (v === 'list') loadDiscussion();
});
// ---- end 讨论 Tab ----

onMounted(() => {
  loadData();
});

watch(question, (q) => {
  if (q?.id) loadDiscussion();
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

.sample-input {
  margin-top: 20px;
  border-radius: var(--radius-md);
  background: var(--color-bg);
  border: 1px solid var(--color-border-light);
  overflow: hidden;
}

.sample-label {
  font-size: 12px;
  font-weight: 600;
  color: var(--color-text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  padding: 8px 14px;
  border-bottom: 1px solid var(--color-border-light);
  background: var(--color-surface);
}

.sample-code {
  margin: 0;
  padding: 12px 14px;
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  font-size: 13px;
  line-height: 1.6;
  color: var(--color-text);
  white-space: pre-wrap;
  word-break: break-all;
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

/* 讨论 Tab */
.discussion-container {
  padding: 12px 16px;
  overflow-y: auto;
  height: 100%;
}

.discussion-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.discussion-count {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.discussion-empty {
  padding: 40px 0;
  text-align: center;
}

.discussion-item {
  padding: 12px 0;
  border-bottom: 1px solid var(--color-border-light);
  cursor: pointer;
  transition: background var(--transition);
}

.discussion-item:last-child {
  border-bottom: none;
}

.discussion-item:hover .discussion-item-title {
  color: var(--color-primary);
}

.discussion-item-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text);
  margin-bottom: 4px;
  transition: color var(--transition);
}

.discussion-item-meta {
  font-size: 12px;
  color: var(--color-text-secondary);
  display: flex;
  align-items: center;
  gap: 4px;
}

.discussion-dot {
  opacity: 0.4;
}

.discussion-detail-topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.discussion-back {
  font-size: 13px;
  color: var(--color-primary);
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
}

.discussion-back:hover {
  opacity: 0.8;
}

.discussion-detail-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 8px;
}

.discussion-detail-meta {
  font-size: 12px;
  color: var(--color-text-secondary);
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 8px;
}

.discussion-tags {
  margin-bottom: 12px;
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.discussion-detail-content {
  border-top: 1px solid var(--color-border-light);
  padding-top: 12px;
  margin-bottom: 12px;
}

.discussion-detail-actions {
  display: flex;
  gap: 10px;
  padding-top: 8px;
  border-top: 1px solid var(--color-border-light);
}

.disc-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 8px;
  border: 1px solid var(--color-border);
  background: transparent;
  color: var(--color-text-secondary);
  font-size: 13px;
  cursor: pointer;
  transition: all var(--transition);
}

.disc-btn:hover,
.disc-btn.active {
  border-color: var(--color-primary);
  color: var(--color-primary);
  background: rgba(0,102,204,0.06);
}

.discussion-form {
  padding-top: 4px;
}
</style>
