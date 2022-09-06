(function (win) {
  axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
  // 创建axios实例
  const service = axios.create({
    // axios中请求配置有baseURL选项，表示请求URL公共部分
    baseURL: '/',
    // 超时
    timeout: 1000000
  })
  // request拦截器
  service.interceptors.request.use(config => {
    // 让每个请求携带自定义token 请根据实际情况自行修改
    const isToken = (config.headers || {}).isToken === false
    const token = localStorage.getItem('front_token')
    if (token && !isToken) {
      config.headers['authorization'] = 'front ' + token
    }
    // get请求映射params参数
    if (config.method === 'get' && config.params) {
      let url = config.url + '?';
      for (const propName of Object.keys(config.params)) {
        const value = config.params[propName];
        var part = encodeURIComponent(propName) + "=";
        if (value !== null && typeof(value) !== "undefined") {
          if (typeof value === 'object') {
            for (const key of Object.keys(value)) {
              let params = propName + '[' + key + ']';
              var subPart = encodeURIComponent(params) + "=";
              url += subPart + encodeURIComponent(value[key]) + "&";
            }
          } else {
            url += part + encodeURIComponent(value) + "&";
          }
        }
      }
      url = url.slice(0, -1);
      config.params = {};
      config.url = url;
    }
    return config
  }, error => {
      Promise.reject(error)
  })

  // 响应拦截器
  service.interceptors.response.use(res => {
      console.log('---响应拦截器---',res)
      if (res.data.code === 0 && res.data.msg === 'NOTLOGIN') {// 返回登录页面
        localStorage.removeItem('front_token')
        window.top.location.href = '/front/page/login.html'
      } else {
        //如果后端返回来的响应中有token
        //以front_token为key，token转成的json对象为value，存储到浏览器中
        const token = res.headers['authorization']
        if (token) {
          localStorage.setItem('front_token', token)
        }
        return res.data
      }
    },
    error => {
      let { message } = error;
      if (message == "Network Error") {
        message = "后端接口连接异常";
      }
      else if (message.includes("timeout")) {
        message = "系统接口请求超时";
      }
      else if (message.includes("Request failed with status code")) {
        message = "系统接口" + message.substr(message.length - 3) + "异常";
      }
      window.vant.Notify({
        message: message,
        type: 'warning',
        duration: 5 * 1000
      })
      //window.top.location.href = '/front/page/no-wify.html'
      return Promise.reject(error)
    }
  )
  win.$axios = service
})(window);
