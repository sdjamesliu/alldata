import request from '@/utils/request'

export function listDept (data) {
  return request({
    url: '/data/system/depts/list',
    method: 'get',
    params: data
  })
}

export function getDept (id) {
  return request({
    url: '/data/system/depts/' + id,
    method: 'get'
  })
}

export function delDept (id) {
  return request({
    url: '/data/system/depts/' + id,
    method: 'delete'
  })
}

export function delDepts (ids) {
  return request({
    url: '/data/system/depts/batch/' + ids,
    method: 'delete'
  })
}

export function addDept (data) {
  return request({
    url: '/data/system/depts',
    method: 'post',
    data: data
  })
}

export function updateDept (data) {
  return request({
    url: '/data/system/depts/' + data.id,
    method: 'put',
    data: data
  })
}
