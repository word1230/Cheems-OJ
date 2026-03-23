<template>
  <div id="basicLayout">
    <a-layout style="min-height: 100vh; background: var(--color-bg)">
      <a-layout-header class="header">
        <GlobalHeader />
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
      <a-layout-footer class="footer">
        <a href="https://github.com/word1230" target="_blank">cheems' github</a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<style scoped>
#basicLayout .header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-bottom: 1px solid var(--color-border-light);
  box-shadow: none;
  height: 52px;
  line-height: 52px;
  padding: 0;
}

#basicLayout .content {
  background: var(--color-bg);
  padding: 32px 24px;
  min-height: calc(100vh - 52px - 48px);
}

#basicLayout .footer {
  background: transparent;
  border-top: 1px solid var(--color-border-light);
  padding: 12px 24px;
  text-align: center;
  font-size: 13px;
  color: var(--color-text-tertiary);
}

#basicLayout .footer a {
  color: var(--color-text-secondary);
  text-decoration: none;
  transition: color var(--transition);
}

#basicLayout .footer a:hover {
  color: var(--color-primary);
}
</style>

<script>
import GlobalHeader from "@/components/GlobalHeader";

export default {
  components: { GlobalHeader },
};
</script>
