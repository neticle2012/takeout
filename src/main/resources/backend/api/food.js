// 查询列表接口
const getDishPage = (params) => {//对菜品进行分页查询时发送get请求到虚拟路径/dish/page
  return $axios({
    url: '/dish/page',
    method: 'get',
    params
  })
}

// 删除接口
const deleteDish = (ids) => {//在后台菜品管理界面点击删除或者批量删除时发送delete请求到虚拟路径/dish
  return $axios({
    url: '/dish',
    method: 'delete',
    params: { ids }
  })
}

// 修改接口
const editDish = (params) => {
  return $axios({
    url: '/dish',
    method: 'put',
    data: { ...params }
  })
}

// 新增接口
const addDish = (params) => {//在后台添加菜品后发送post到虚拟路径/dish
  return $axios({
    url: '/dish',
    method: 'post',
    data: { ...params }
  })
}

// 查询详情
const queryDishById = (id) => {
  return $axios({
    url: `/dish/${id}`,
    method: 'get'
  })
}

// 获取菜品分类列表
const getCategoryList = (params) => {//在后台菜品管理界面点击添加菜品时发送get请求到虚拟路径/category/list
  return $axios({
    url: '/category/list',
    method: 'get',
    params
  })
}

// 查菜品列表的接口
const queryDishList = (params) => {
  return $axios({
    url: '/dish/list',
    method: 'get',
    params
  })
}

// 文件down预览
const commonDownload = (params) => {
  return $axios({
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
    },
    url: '/common/download',
    method: 'get',
    params
  })
}

// 起售停售---批量起售停售接口
//在后台菜品管理界面点击起售/停售或者批量起售/停售时发送post请求到虚拟路径/dish/status/${params.status}
const dishStatusByStatus = (params) => {
  return $axios({
    url: `/dish/status/${params.status}`,
    method: 'post',
    params: { ids: params.id }
  })
}