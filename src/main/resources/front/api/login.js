function loginApi(data) {//移动端用户登录时，发送post请求到虚拟路径/user/login
    return $axios({
      'url': '/user/login',
      'method': 'post',
      data
    })
  }

function sendMsgApi(data) {//移动端用户获取验证码时，发送post请求到虚拟路径/user/sendMsg
    return $axios({
        'url': '/user/sendMsg',
        'method': 'post',
        data
    })
}

function loginoutApi() {
  return $axios({
    'url': '/user/loginout',
    'method': 'post',
  })
}

  