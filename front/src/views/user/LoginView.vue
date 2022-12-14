<template>
  <input value="true" type="text" name="loginState" id="loginState" hidden="true"/>

  <div class="login_page">
    <form id="login_form">
      <table>
        <tr>
          <td>ID :</td> <td><input id="email" name="email" type="text"></td>
        </tr>
        <tr>
          <td>PW :</td> <td><input id="password" name="password" type="password"></td>
        </tr>
        <tr>
          <td colspan="2"> <a id="loginBtn" onclick="chkValue()">커뮤니티 로그인</a> </td>
        </tr>
        <tr>
          <td colspan="2">
            <a href="https://kauth.kakao.com/oauth/authorize?client_id=78effdc7d3e403e800af579ee0059fef&redirect_uri=http://localhost:8080/loginKakao&response_type=code">
              <img id="kakaoLoginIamge" src="/src/images/kakao_login_medium_wide.png"/>
            </a>
          </td>
        </tr>
        <tr>
          <td colspan="2" style="font-size: 2px">--------------- 처음이신가요? ---------------</td>
        </tr>
        <tr>
          <td colspan="2"> <a> 회원가입 </a></td>
        </tr>
      </table>
    </form>
  </div>

</template>

<script>
export default {
  name: "LoginView.vue"
}
</script>

<style scoped>

</style>