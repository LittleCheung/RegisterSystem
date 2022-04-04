<template>
  <div class="app-container">

      <el-form label-width="120px">
         <el-form-item label="医院名称">
            <el-input v-model="hospitalSet.hosname"/>
         </el-form-item>
         <el-form-item label="医院编号">
            <el-input v-model="hospitalSet.hoscode"/>
         </el-form-item>
         <el-form-item label="api基础路径">
            <el-input v-model="hospitalSet.apiUrl"/>
         </el-form-item>
         <el-form-item label="联系人姓名">
            <el-input v-model="hospitalSet.contactsName"/>
         </el-form-item>
         <el-form-item label="联系人手机">
            <el-input v-model="hospitalSet.contactsPhone"/>
         </el-form-item>
         <el-form-item>
            <el-button type="primary" @click="saveOrUpdate">保存</el-button>
         </el-form-item>
      </el-form>

  </div>
</template>

<script>
import hospset from '@/api/hospset'

  export default{
    data(){
      return {
        hospitalSet:{}  
      }
    },
    created(){
      //获取路由id值
      if(this.$route.params && this.$route.params.id)  {
        //修改
        const id = this.$route.params.id;
        this.getHospSet(id);
      }else{
        //清空
        this.hospitalSet = {};
      }
    },
    methods:{
      //添加医院设置
      save(){
        hospset.saveHospSet(this.hospitalSet)
          .then(response => {
            //提示
            this.$message({
                  type: 'success',
                  message: '添加成功!'
              })
            //跳转页面
            this.$router.push({path:'/hospSet/list'})
          })        
      },
      //修改医院
      update(){
        hospset.updateHospSet(this.hospitalSet)
          .then(response => {
            //提示
            this.$message({
                  type: 'success',
                  message: '修改成功!'
               })
            //跳转页面
            this.$router.push({path:'/hospSet/list'})
          })
      },
      saveOrUpdate(){
        //判断是修改还是添加
        if(!this.hospitalSet.id){
          //没有id 添加操作
          this.save();
        }else{
          //有id 修改操作
          this.update();
        }
      },
      //根据id 查询医院
      getHospSet(id){
        hospset.getHospSet(id)
          .then(response =>{
            this.hospitalSet = response.data;
          })
      },


    }
  }


</script>

