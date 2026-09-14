import request from './request'

export function listCategory() {
  return request.get('/category/list')
}
