// 查询列表页接口
//在订单详细管理页面进行分页查询时，发送get请求到虚拟路径/order/page
const getOrderDetailPage = (params) => {
  return $axios({
    url: '/order/page',
    method: 'get',
    params
  })
}

// 查看接口
const queryOrderDetailById = (id) => {
  return $axios({
    url: `/orderDetail/${id}`,
    method: 'get'
  })
}

// 取消，派送，完成接口
const editOrderDetail = (params) => {//在订单详细管理页面点击派送或者完成时，发送put请求到虚拟路径/order
  return $axios({
    url: '/order',
    method: 'put',
    data: { ...params }
  })
}
