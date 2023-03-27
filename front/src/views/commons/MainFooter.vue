<template>
  <footer class="sticky-bottom footer">
    <div>
      <b id="info"> &copy;냥뮤니티 개발자 - ppusda</b>
    </div>
  </footer>
</template>

<script>
export default {
  name: "MainFooter.vue"
}
</script>

<style>
.footer {
  background-color: #333;
  text-align: right;
  bottom: 0;
  border-radius: 15px 15px 0px 0px;
}

#info {
  margin: 1%;
  color: #fff;
  font-size: 0.8vw;
}
</style>