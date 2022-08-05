function loginApi(data) {
  return $axios({ //后台登录，点击登录按钮后发送post请求到虚拟路径/employee/login
    'url': '/employee/login',
    'method': 'post',
    data
  })
}

function logoutApi(){
  return $axios({
    'url': '/employee/logout',
    'method': 'post',
  })
}
