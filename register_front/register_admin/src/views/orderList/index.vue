<template>
  <div class="app-container">
         <div class="page-container">
      <div class="personal-order">
        <el-form :inline="true">

          <el-form-item>
            <el-input  v-model="searchObj.patientName" placeholder="就诊人姓名"/>
          </el-form-item>
          <el-form-item>
              <el-input v-model="searchObj.userName" placeholder="会员姓名"/>
          </el-form-item>

          <el-form-item label="订单状态：" style="margin-left: 30px">
            <el-select clearable v-model="searchObj.orderStatus" placeholder="全部" class="v-select  patient-select" style="width: 200px;">
              <el-option
                v-for="item in statusList"
                :key="item.status"
                :label="item.comment"
                :value="item.status">
              </el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="getList()">查询</el-button>
          </el-form-item>

        </el-form>
        <div class="table-wrapper table">
          <el-table
            :data="list"
            stripe
            style="width: 100%">
            <el-table-column
              prop="param.userName"
              label="会员姓名"
              width="120">
            </el-table-column>
            <el-table-column
              prop="outTradeNo"
              label="订单编号"
              width="150">
            </el-table-column>
            <el-table-column
              prop="hosname"
              label="医院"
              width="150">
            </el-table-column>
            <el-table-column
              prop="depname"
              label="科室">
            </el-table-column>
            <el-table-column
              prop="title"
              label="医生">
            </el-table-column>
            <el-table-column
              prop="amount"
              label="医事服务费">
            </el-table-column>
            <el-table-column
              prop="patientName"
              label="就诊人">
            </el-table-column>
            <el-table-column
              prop="param.orderStatusString"
              label="订单状态">
            </el-table-column>
            <el-table-column label="操作">
              <template slot-scope="scope">
                <router-link :to="'/order/order/show/'+scope.row.id">
                    <el-button type="primary" size="mini">详情</el-button>
                </router-link>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <!-- 分页 -->
        <el-pagination
          class="pagination"
          :current-page="page"
          :total="total"
          :page-size="limit"
          style="padding: 30px 0; text-align: center;"
          layout="total, prev, pager, next, jumper"
          @current-change="getList">
        </el-pagination>
      </div>
    </div>
  </div>
</template>


<script>
import orderInfoApi from '@/api/order'
export default {
  data() {
    return {
      list: [], // banner列表
      total: 0, // 数据库中的总记录数
      page: 1, // 默认页码
      limit: 10, // 每页记录数
      searchObj: {}, // 查询表单对象
      patientList: [],
      statusList: []
    }
  },
  created(){
    this.getStatusList();
    this.getList(1);
  },

  methods: {
     //获取订单状态
     getStatusList(){
       orderInfoApi.getStatusList().then(response =>{
         this.statusList = response.data 
         
       })
     },

     //订单列表
     getList(page=1){
       this.page = page
       orderInfoApi.getPageList(this.page, this.limit, this.searchObj).then(response =>{
          //请求成功
          this.list = response.data
          this.list = response.data.records
          this.total = response.data.total
       })
       .catch(error =>{
                //请求失败
          console.log(error)
       })
     },
    //订单详情
    show(id) {
      window.location.href = '/orderList/show?orderId=' + id
    }
  }
 
  
}
</script>
