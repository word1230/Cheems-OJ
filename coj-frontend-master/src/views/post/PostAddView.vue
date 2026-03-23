<template>
  <div id="postAddView">
    <h2>{{ updatePage ? "编辑帖子" : "写帖子" }}</h2>
    <a-form :model="form" label-align="left">
      <a-form-item field="title" label="标题">
        <a-input v-model="form.title" placeholder="请输入标题" />
      </a-form-item>
      <a-form-item field="tags" label="标签">
        <a-input-tag
          v-model="form.tags"
          placeholder="请输入标签，按回车添加"
          allow-clear
        />
      </a-form-item>
      <a-form-item field="content" label="内容">
        <MdEditor
          :value="form.content ?? ''"
          :handle-change="onContentChange"
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" @click="doSubmit">{{
          updatePage ? "保存修改" : "发布"
        }}</a-button>
        <a-button style="margin-left: 12px" @click="router.back()"
          >取消</a-button
        >
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import {
  PostControllerService,
  PostAddRequest,
  PostEditRequest,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import MdEditor from "@/components/MdEditor.vue";

const route = useRoute();
const router = useRouter();

const updatePage = route.path.includes("edit");

const form = ref<PostAddRequest & PostEditRequest>({
  title: "",
  tags: [],
  content: "",
});

const loadData = async () => {
  const id = route.query.id;
  if (!updatePage || !id) return;
  const res = await PostControllerService.getPostVoByIdUsingGet(Number(id));
  if (res.code === 0 && res.data) {
    form.value = {
      id: res.data.id,
      title: res.data.title,
      tags: res.data.tagList ?? [],
      content: res.data.content,
    };
  } else {
    message.error("加载失败，" + res.message);
  }
};

onMounted(() => {
  loadData();
});

const onContentChange = (value: string) => {
  form.value.content = value;
};

const doSubmit = async () => {
  if (!form.value.title?.trim()) {
    message.warning("请输入标题");
    return;
  }
  if (!form.value.content?.trim()) {
    message.warning("请输入内容");
    return;
  }
  if (updatePage) {
    const res = await PostControllerService.editPostUsingPost(form.value);
    if (res.code === 0) {
      message.success("修改成功");
      router.push(`/post/${form.value.id}`);
    } else {
      message.error("修改失败，" + res.message);
    }
  } else {
    const res = await PostControllerService.addPostUsingPost(form.value);
    if (res.code === 0) {
      message.success("发布成功");
      router.push("/post");
    } else {
      message.error("发布失败，" + res.message);
    }
  }
};
</script>

<style scoped>
#postAddView {
  max-width: 900px;
  margin: 0 auto;
}
</style>
