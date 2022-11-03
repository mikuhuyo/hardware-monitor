import request from '@/utils/request'

export const getDevice = (data: any) =>
  request({
    url: '/api/device',
    method: 'post',
    data
  })
export const deviceStatus = (data: any) =>
  request({
    url: '/api/device/status',
    method: 'put',
    data
  })

export const device = (data: any) =>
  request({
    url: '/api/device/deviceQuota',
    method: 'post',
    data
  })
