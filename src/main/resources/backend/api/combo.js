// 查询列表数据
const getSetmealPage = (params) => {//在后台套餐管理界面进行分页查询时发送get请求到虚拟路径/setmeal/page
  return $axios({
    url: '/setmeal/page',
    method: 'get',
    params
  })
}

// 删除数据接口
//在后台套餐管理界面进行单个套餐的删除或者套餐批量删除时，发送delete请求到虚拟路径/setmeal
const deleteSetmeal = (ids) => {
  return $axios({
    url: '/setmeal',
    method: 'delete',
    params: { ids }
  })
}

// 修改数据接口
const editSetmeal = (params) => {//在后台套餐管理界面修改套餐后，发送put请求到虚拟路径/setmeal
  return $axios({
    url: '/setmeal',
    method: 'put',
    data: { ...params }
  })
}

// 新增数据接口
const addSetmeal = (params) => {//在后台套餐管理界面添加套餐后，发送post请求到虚拟路径/setmeal
  return $axios({
    url: '/setmeal',
    method: 'post',
    data: { ...params }
  })
}

// 查询详情接口
const querySetmealById = (id) => {//在后台套餐管理界面修改套餐时，发送get请求到虚拟路径/setmeal/${id}
  return $axios({
    url: `/setmeal/${id}`,
    method: 'get'
  })
}

// 批量起售禁售
//在后台套餐管理界面进行单个套餐的起售/停售和套餐批量起售/停售时，发送post请求到虚拟路径/setmeal/status/${params.status}
const setmealStatusByStatus = (params) => {
  return $axios({
    url: `/setmeal/status/${params.status}`,
    method: 'post',
    params: { ids: params.ids }
  })
}
