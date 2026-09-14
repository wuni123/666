import request from './request'

// 前台浏览列表（只查上架）
export function pageProduct(params) {
  return request.get('/product/page', { params })
}

// 地图标注数据（所有带经纬度的上架产品）
export function listMapPoints() {
  return request.get('/product/map')
}

// 后台管理列表（查全部状态）
export function pageAdminProduct(params) {
  return request.get('/admin/product/page', { params })
}

export function addProduct(data) {
  return request.post('/admin/product', data)
}

export function updateProduct(id, data) {
  return request.put(`/admin/product/${id}`, data)
}

export function deleteProduct(id) {
  return request.delete(`/admin/product/${id}`)
}

export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/admin/upload/image', formData)
}

// 农户/用户发布（提交后待审核）
export function submitProduct(data) {
  return request.post('/user/product', data)
}

// 我的发布列表
export function myProducts(params) {
  return request.get('/user/product/my', { params })
}

// 农户端上传封面图
export function uploadImageUser(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/user/upload/image', formData)
}

// 后台审核：auditStatus=1 通过，2 驳回
export function auditProduct(id, auditStatus) {
  return request.put(`/admin/product/${id}/audit`, { auditStatus })
}
