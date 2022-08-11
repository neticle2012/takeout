//获取所有地址
//在进入地址管理界面时自动获取当前用户的所有收货地址，发送get请求到虚拟路径/addressBook/list
function addressListApi() {
    return $axios({
      'url': '/addressBook/list',
      'method': 'get',
    })
  }

//获取最新地址
function addressLastUpdateApi() {
    return $axios({
      'url': '/addressBook/lastUpdate',
      'method': 'get',
    })
}

//新增地址
function  addAddressApi(data){//新增收货地址，点击保存地址按钮时发送post请求到虚拟路径/addressBook
    return $axios({
        'url': '/addressBook',
        'method': 'post',
        data
      })
}

//修改地址
function  updateAddressApi(data){//修改某个地址时，发送put请求到虚拟路径/addressBook
    return $axios({
        'url': '/addressBook',
        'method': 'put',
        data
      })
}

//删除地址
function deleteAddressApi(params) {//删除某个地址时，发送delete请求到虚拟路径/addressBook
    return $axios({
        'url': '/addressBook',
        'method': 'delete',
        params
    })
}

//查询单个地址
function addressFindOneApi(id) {//修改某个地址时，发送get请求到虚拟路径/addressBook/${id}
  return $axios({
    'url': `/addressBook/${id}`,
    'method': 'get',
  })
}

//设置默认地址
//点击将某个收货地址设置为默认收货地址时，发送put请求到虚拟路径/addressBook/default
function  setDefaultAddressApi(data){
  return $axios({
      'url': '/addressBook/default',
      'method': 'put',
      data
    })
}

//获取默认地址
function getDefaultAddressApi() {
  return $axios({
    'url': `/addressBook/default`,
    'method': 'get',
  })
}