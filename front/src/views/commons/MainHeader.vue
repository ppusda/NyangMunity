<template>
  <header>
    <nav class="navbar navbar-dark navbar-expand-lg header shadow-lg sticky-top">
      <div class="container">
        <a class="navbar-brand align-middle" id="img_logo" href="/">
          <img id="logo" src="/src/images/logo.png" alt="" height="72">
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent" aria-controls="navbarContent" aria-expanded="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarContent">
          <ul class="navbar-nav ms-auto mb-2 mb-lg-0 fw-bold">
            <li class="nav-item">
              <a class="nav-link">
                <RouterLink to="/user/login">로그인</RouterLink>
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link">
                <RouterLink to="/boards/">커뮤니티</RouterLink>
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link">
                <RouterLink to="/meme">짤</RouterLink>
              </a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  </header>
</template>


<script>
</script>

<style scoped>
  .header {
    background-color: #333;
    border-radius: 0px 0px 15px 15px;
  }

  #menuDivUl {
    list-style: none;
  }
</style>