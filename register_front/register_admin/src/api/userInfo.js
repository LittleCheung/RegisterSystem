import request from '@/utils/request'

const api_name = '/admin/user'

export default {

    getPageList(page, limit, searchObj) {
        return request({
            url: `${api_name}/${page}/${limit}`,
            method: 'get',
            params: searchObj
        })
    },
    //用户锁定
    lock(id, status) {
        return request({
          url: `${api_name}/lock/${id}/${status}`,
          method: 'get'
        })
    },
    //用户详情
    //用户详情
    show(id) {
        return request({
            url: `${api_name}/show/${id}`,
            method: 'get'
        })
    },
    //认证审批
    approval(userId, authStatus) {
        return request({
        url: `${api_name}/approve/${userId}/${authStatus}`,
        method: 'get'
        })
    }
  
  
    
}
