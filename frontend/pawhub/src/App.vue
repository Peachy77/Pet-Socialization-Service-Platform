<template>
  <div id="app">
    <!-- <nav>
      <router-link to="/">Home</router-link> |
      <router-link to="/about">About</router-link>
    </nav> -->
    <router-view/>
  </div>
</template>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

body {
  cursor: default;
}

input,
textarea,
[contenteditable="true"] {
  cursor: text;
}

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
}

/* 全局交互光标：可点击元素统一手型 */
button:not(:disabled),
a,
[role="button"],
.clickable,
.tab,
.action-btn,
.reply-send,
.send-btn,
.top-fav,
.follow-btn,
.booking-row .book-btn,
.order-list .action-btn,
.order-list [data-action],
.bottom-nav .nav-item,
.search-header .back-btn,
.search-header .search-btn,
.post-footer .like-btn,
.post-footer,
.comment-actions .action-btn {
  cursor: pointer;
}

button:disabled {
  cursor: not-allowed;
}

/* 全局导航悬停：用于所有会进入其他页面的点击入口 */
.nav-item,
.back,
.back-btn,
.search-btn,
.search-wrapper,
.setting,
.stat.clickable,
.edit-btn,
.user-card,
.post-card,
.card,
.btn,
.menu-item,
.message-item:not(.system-message),
.book-btn,
.link,
.top-link,
.back-link,
.search-results .back,
.search-page .tag {
  transition: transform 0.2s ease, box-shadow 0.2s ease, filter 0.2s ease, background-color 0.2s ease;
}

@media (hover: hover) {
  .nav-item:hover,
  .back:hover,
  .back-btn:hover,
  .search-btn:hover,
  .search-wrapper:hover,
  .setting:hover,
  .stat.clickable:hover,
  .edit-btn:hover,
  .user-card:hover,
  .post-card:hover,
  .card:hover,
  .btn:hover,
  .menu-item:hover,
  .message-item:not(.system-message):hover,
  .book-btn:hover,
  .link:hover,
  .top-link:hover,
  .back-link:hover,
  .search-results .back:hover,
  .search-page .tag:hover {
    transform: translateY(-1px);
    filter: brightness(0.98);
  }

  .nav-item:hover,
  .user-card:hover,
  .post-card:hover,
  .card:hover,
  .message-item:not(.system-message):hover {
    box-shadow: 0 6px 18px rgba(30, 24, 45, 0.08);
  }
}
</style>
