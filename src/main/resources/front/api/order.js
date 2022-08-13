//提交订单
function  addOrderApi(data){//在订单界面点击去支付按钮时，发送post请求到虚拟路径/order/submit
    return $axios({
        'url': '/order/submit',
        'method': 'post',
        data
      })
}

//查询所有订单
function orderListApi() {
  return $axios({
    'url': '/order/list',
    'method': 'get',
  })
}

//分页查询订单
function orderPagingApi(data) {//在加载用户中心界面时，发送get请求到虚拟路径/order/userPage
  return $axios({
      'url': '/order/userPage',
      'method': 'get',
      params:{...data}
  })
}

//再来一单
function orderAgainApi(data) {//点击再来一单按钮时，发送post请求到虚拟路径/order/again
  return $axios({
      'url': '/order/again',
      'method': 'post',
      data
  })
}