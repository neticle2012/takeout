//获取所有的菜品和套餐分类
function categoryListApi() {//移动端页面加载时，发送get请求到虚拟路径/category/list
    return $axios({
      'url': '/category/list',
      'method': 'get',
    })
  }

//获取菜品分类对应的菜品
function dishListApi(data) {//移动端主页上选中某一菜品分类时，发送get请求到虚拟路径/dish/list
    return $axios({
        'url': '/dish/list',
        'method': 'get',
        params:{...data}
    })
}

//获取菜品分类对应的套餐
function setmealListApi(data) {//移动端主页上选中某一套餐分类时，发送get请求到虚拟路径/setmeal/list
    return $axios({
        'url': '/setmeal/list',
        'method': 'get',
        params:{...data}
    })
}

//获取购物车内商品的集合
//增减购物车的菜品/套餐或者移动端主页加载时，发送get请求到虚拟路径/shoppingCart/list
function cartListApi(data) {
    return $axios({
        'url': '/shoppingCart/list',
        'method': 'get',
        params:{...data}
    })
}

//购物车中添加商品
//点击加号或者选择口味后点击加入购物车按钮时，发送post请求到虚拟路径/shoppingCart/add
function  addCartApi(data){
    return $axios({
        'url': '/shoppingCart/add',
        'method': 'post',
        data
      })
}

//购物车中减少商品
//点击减号时，发送post请求到虚拟路径/shoppingCart/sub
function  updateCartApi(data){
    return $axios({
        'url': '/shoppingCart/sub',
        'method': 'post',
        data
      })
}

//删除购物车的商品
function clearCartApi() {//点击购物车右上角的清空按钮时，发送delete请求到虚拟路径/shoppingCart/clean
    return $axios({
        'url': '/shoppingCart/clean',
        'method': 'delete',
    })
}

//获取套餐的全部菜品
function setMealDishDetailsApi(id) {
    return $axios({
        'url': `/setmeal/dish/${id}`,
        'method': 'get',
    })
}


