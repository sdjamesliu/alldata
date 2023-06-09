import request from '@/utils/request'

export function pageRole (data) {
  return request({
    url: '/data/system/roles/page',
    method: 'get',
    params: data
  })
}

export function listRole (data) {
  return request({
    url: '/data/system/roles/list',
    method: 'get',
    params: data
  })
}

export function getRole (id) {
  return request({
    url: '/data/system/roles/' + id,
    method: 'get'
  })
}

export function delRole (id) {
  return request({
    url: '/data/system/roles/' + id,
    method: 'delete'
  })
}

export function delRoles (ids) {
  return request({
    url: '/data/system/roles/batch/' + ids,
    method: 'delete'
  })
}

export function addRole (data) {
  return request({
    url: '/data/system/roles',
    method: 'post',
    data: data
  })
}

export function updateRole (data) {
  return request({
    url: '/data/system/roles/' + data.id,
    method: 'put',
    data: data
  })
}
