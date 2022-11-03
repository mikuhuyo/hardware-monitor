import request from '@/utils/request'

// 获取报警日志
export const getAlarmLog = (data: any) =>
  request({
    url: '/api/alarm-log/search',
    method: 'post',
    data
  })

export const alarm = (data: any) =>
  request({
    url: '/api/alarm/search',
    method: 'post',
    data
  })
export const addAlarm = (data: any) =>
  request({
    url: '/api/alarm',
    method: 'post',
    data
  })
export const putAlarm = (data: any) =>
  request({
    url: '/api/alarm',
    method: 'put',
    data
  })
export const delAlarm = (data: any) =>
  request({
    url: `/api/alarm/${data}`,
    method: 'delete',
  })
