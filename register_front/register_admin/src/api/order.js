import request from '@/utils/request'

const api_name = `/admin/order`

export default {
    //订单详列表
    getPageList(page, limit, searchObj){
        return request({
            url: `${api_name}/findOrderList/${page}/${limit}`,
            method: `post`,
            params: searchObj
        })
    },
    //订单状态
    getStatusList(){
        return request({
            url: `${api_name}/getStatusList`,
            method: `get`,
        })
    },
    //获取订单详情
    getOrder(id){
        return request({
            url:`${api_name}/getOrder/${id}`,
            method: `get`
        })
    },
    //取消订单
    cancelOrder(orderId){
        return request({
            url:`${api_name}/cancelOrder/${orderId}`,
            method: `get`
        })
    }

}
