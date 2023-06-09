import request from '@/utils/request'

// 根据参数键名查询参数值
export function getConfigKey (configKey) {
  return request({
    url: '/data/system/configs/key/' + configKey,
    method: 'get'
  })
}

// 刷新参数缓存
export function refreshConfig () {
  return request({
    url: '/data/system/configs/refresh',
    method: 'get'
  })
}

export function pageConfig (data) {
  return request({
    url: '/data/system/configs/page',
    method: 'get',
    params: data
  })
}

export function getConfig (id) {
  return request({
    url: '/data/system/configs/' + id,
    method: 'get'
  })
}

export function delConfig (id) {
  return request({
    url: '/data/system/configs/' + id,
    method: 'delete'
  })
}

export function delConfigs (ids) {
  return request({
    url: '/data/system/configs/batch/' + ids,
    method: 'delete'
  })
}

export function addConfig (data) {
  return request({
    url: '/data/system/configs',
    method: 'post',
    data: data
  })
}

export function updateConfig (data) {
  return request({
    url: '/data/system/configs/' + data.id,
    method: 'put',
    data: data
  })
}
