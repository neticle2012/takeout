(function (win) {
  axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
  // 创建axios实例
  const service = axios.create({
    // axios中请求配置有baseURL选项，表示请求URL公共部分
    baseURL: '/',
    // 超时
    timeout: 1000000
  })
  // request拦截器（前端的拦截器）
  service.interceptors.request.use(config => {
    // 让每个请求携带自定义token 请根据实际情况自行修改
    const isToken = (config.headers || {}).isToken === false
    const token = localStorage.getItem('backend_token')
    if (token && !isToken) {
      config.headers['authorization'] = 'backend ' + token
    }
    // get请求映射params参数，将params参数以?param1=val1&param2=val2...拼接到url后面
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
      console.log(error)
      Promise.reject(error)
  })

  // 响应拦截器（前端的拦截器）
  service.interceptors.response.use(res => {
      // 如果响应数据（res.data才是封装为json字符串的R对象）满足以下条件，则直接跳转回登录页面
      if (res.data.code === 0 && res.data.msg === 'NOTLOGIN') {
        console.log('---/backend/page/login/login.html---')
        localStorage.removeItem('empInfo')
        localStorage.removeItem('backend_token')
        window.top.location.href = '/backend/page/login/login.html'
      } else {
        //如果后端返回来的响应中有token
        //以backend_token为key，token转成的json对象为value，存储到浏览器中
        const token = res.headers['authorization']
        if (token) {
          localStorage.setItem('backend_token', token)
        }
        return res.data //注意！！！这里前端拦截器放行后直接返回的res.data！！！
      }
    },
    error => {
      console.log('err' + error)
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
      window.ELEMENT.Message({
        message: message,
        type: 'error',
        duration: 5 * 1000
      })
      return Promise.reject(error)
    }
  )
  win.$axios = service
})(window);
