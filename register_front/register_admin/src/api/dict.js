import request from '@/utils/request'


export default{
    //数据字典展示列表
    dictList(id){
        return request({
            url: `/admin/cmn/dict/findChildData/${id}`,
            method: 'get'
        })
    }
}
