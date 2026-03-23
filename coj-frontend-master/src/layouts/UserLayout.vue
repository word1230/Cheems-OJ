<template>
  <div id="userLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header">
        <div class="brand">
          <img src="../assets/oj-logo.svg" class="logo" alt="C OJ Logo" />
          <span class="brand-name">C OJ</span>
        </div>
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
#userLayout {
  background: #f5f5f7;
}

#userLayout .header {
  background: transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 64px;
  line-height: 64px;
  padding: 0 24px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
}

.logo {
  width: 28px;
  height: 28px;
}

.brand-name {
  font-size: 17px;
  font-weight: 600;
  color: var(--color-text);
  letter-spacing: -0.02em;
}

#userLayout .content {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  flex: 1;
}

#userLayout .footer {
  background: transparent;
  padding: 16px;
  text-align: center;
  font-size: 13px;
  color: var(--color-text-tertiary);
}

#userLayout .footer a {
  color: var(--color-text-secondary);
  text-decoration: none;
  transition: color var(--transition);
}

#userLayout .footer a:hover {
  color: var(--color-primary);
}
</style>
<script></script>
