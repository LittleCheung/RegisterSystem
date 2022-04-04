<template>
  <div class="app-container">
    <el-row type="flex" class="row-bg" justify="space-between">
      <el-col :span="2">
          <h4>挂号详情</h4>
      </el-col>
      <el-col :span="2">
          <el-button @click="back" style="">返回</el-button>
      </el-col>
    </el-row>
           <!-- 右侧内容 #start -->
    <el-row type="flex" class="row-bg" justify="center">
      <el-col :span="4">
            
    <div class="page-container">
      <div class="order-detail">

        <div class="status-bar">
                 <h4><span class="iconfont">挂号状态：</span> {{ orderInfo.param.orderStatusString }}</h4>
        </div>
        <div class="info-wrapper">
          <div class="title-wrapper">
            <div class="block"></div>
          </div>
          <div class="info-form">
            <el-form >
              <el-form-item label="就诊人信息：">
                <div class="content"><span>{{ orderInfo.patientName }}</span></div>
              </el-form-item>
              <el-form-item label="就诊日期：">
                <div class="content"><span>{{ orderInfo.reserveDate }} {{ orderInfo.reserveTime == 0 ? '上午' : '下午' }}</span></div>
              </el-form-item>
              <el-form-item label="就诊医院：">
                <div class="content"><span>{{ orderInfo.hosname }} </span></div>
              </el-form-item>
              <el-form-item label="就诊科室：">
                <div class="content"><span>{{ orderInfo.depname }} </span></div>
              </el-form-item>
              <el-form-item label="医生职称：">
                <div class="content"><span>{{ orderInfo.title }} </span></div>
              </el-form-item>
              <el-form-item label="医事服务费：">
                <div class="content">
                  <div class="fee">{{ orderInfo.amount }}元
                  </div>
                </div>
              </el-form-item>
              <el-form-item label="挂号单号：">
                <div class="content"><span>{{ orderInfo.outTradeNo }} </span></div>
              </el-form-item>
              <el-form-item label="挂号时间：">
                <div class="content"><span>{{ orderInfo.createTime }}</span></div>
              </el-form-item>
            </el-form>
          </div>
        </div>

        <div class="bottom-wrapper mt60"  v-if="orderInfo.orderStatus == 0 || orderInfo.orderStatus == 1">

          <el-row type="flex" class="row-bg" justify="center" style="margin-top:40px">
            <el-col :span="12">
              <el-button type="primary" size="medium"  @click="cancelOrder()">取消预约</el-button>
            </el-col>
          </el-row>

        </div>
      </div>
    </div>
      
      </el-col>
    </el-row>

  </div>
</template>


<script>
import orderInfoApi from '@/api/order'
export default {
  data() {
    return {
      orderInfo: {
        param: {}
      },
      orderId: null
    }
  },
  created(){
        //获取路由id
        const id =  this.$route.params.id
        this.orderId = id
        this.init()
  },

  methods: {
    init(){
        orderInfoApi.getOrder(this.orderId).then(response => {
        this.orderInfo = response.data
      })
    },

    //获取订单信息
    getOrder(id){
      orderInfoApi.getOrder(id).then(response =>{
        this.orderInfo = response.data
      })
    },
    //取消预约
    //取消订单
    cancelOrder(){
      this.$confirm('确定取消预约吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => { // promise
          // 点击确定，远程调用
          return orderInfoApi.cancelOrder(this.orderId)
        }).then((response) => {
          this.$message.success('取消成功')
          this.init();
        }).catch(() => {
          this.$message.info('操作失败')
        })
    },

    //页面返回
    back(){
        this.$router.push({ path: '/order/order/list' })
    }

  }
 
  
}
</script>
