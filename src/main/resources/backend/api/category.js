// 查询列表接口
const getCategoryPage = (params) => {//在分页查询时发送get请求到虚拟路径/category/page
  return $axios({
    url: '/category/page',
    method: 'get',
    params
  })
}

// 编辑页面反查详情接口
const queryCategoryById = (id) => {
  return $axios({
    url: `/category/${id}`,
    method: 'get'
  })
}

// 删除当前列的接口
const deleCategory = (id) => {//在后台分类管理界面删除分类时发送delete请求到虚拟路径/category
  return $axios({
    url: '/category',
    method: 'delete',
    params: { id }
  })
}

// 修改接口
const editCategory = (params) => {
  return $axios({
    url: '/category',
    method: 'put',
    data: { ...params }
  })
}

// 新增接口
const addCategory = (params) => {//在后台分类管理界面新增菜品/套餐分类后发送post请求到虚拟路径/category
  return $axios({
    url: '/category',
    method: 'post',
    data: { ...params }
  })
}