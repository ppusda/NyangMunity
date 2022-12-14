<template>
  <header>
    <div class="header">
      <a id="img_logo" href="/"><img id="logo" src="/src/images/logo.png" alt=""></a>
      <nav>
        <b class="menu">
          <RouterLink to="/user/login">로그인</RouterLink> &nbsp&nbsp&nbsp
          <RouterLink to="/community">커뮤니티</RouterLink>
        </b>
      </nav>
    </div>
  </header>
</template>

<script>
</script>

<style scoped>
.header {
  position: sticky;
  width: 100%;
  top: 0%;
  background-color: #333;
  border-radius: 0px 0px 15px 15px;
}

#img_logo {
  position: relative;
  left: 43.5%;
  transform:translate(-50%, 0%);
}
#logo {
  width: 13%;
  height: 13%;
}

.menu {
  position: relative;
  left: 85%;
  transform:translate(-50%, 0%);
  font-size: 1vw;
}
</style>