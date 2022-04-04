<template>
  <!-- header -->
  <div>
  </div>
  <!-- footer -->
</template>
<script>
export default {

  data() {
    return {
    }
  },
  mounted() {
      //使用这种方法，获取?aa=1&bb=2这种参数传递
    let token = this.$route.query.token
    let name = this.$route.query.name
    let openid = this.$route.query.openid
    // 调用父vue方法
    window.parent['loginCallback'](name, token, openid)
  }
}
</script>
