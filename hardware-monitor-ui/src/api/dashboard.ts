import request from '@/utils/request'


// 获取指标
export const quotaNumberQuota = () =>
  request({
    url: '/api/quota/search/integer',
    method: 'get',
  })


// 获取系统面板
export const boardSystemBoard = () =>
  request({
    url: '/api/board/system',
    method: 'get',
  })
// 更改系统面板
export const boardStatus = (params: any) =>
  request({
    url: '/api/board/status',
    method: 'put',
    data: params
  })
// 添加面板
export const addBoard = (params: any) =>
  request({
    url: `/api/board`,
    method: 'post',
    data: params
  })
// 删除面板
export const delBoard = (id: number) =>
  request({
    url: `/api/board/${id}`,
    method: 'delete',
  })
// 获取非系统(私有)面板
export const getUnSystemPanel = () =>
  request({
    url: '/api/board/my',
    method: 'get',
  })


// 监控设备数 - 设备数和报警设备数量
export const monitor = () =>
  request({
    url: '/api/report/device/monitor',
    method: 'get',
  })
// 设备状态分布 - 首页环形图数据
export const statusCollect = (params: any) =>
  request({
    url: '/api/report/device/status-collect',
    method: 'get',
    params
  })
// 地图 - 根据经纬度获取设备信息
export const deviceList = (params: any) =>
  request({
    url: `/api/gps/device-details/${params.lat}/${params.lon}/${params.distance}`,
    method: 'get'
  })


// 报表-今日异常设备
// 异常趋势图
export const trendDatas = (params: any) =>
  request({
    url: '/api/report/alarm/trend',
    method: 'post',
    data: params
  })
// 异常数量Top10
export const top10Alarm = (params: any) =>
  request({
    url: '/api/report/alarm/top10',
    method: 'post',
    data: params
  })
// 报表-获取实时告警日志
export const realTimeAlarmLog = () =>
  request({
    url: '/api/report/alarm/real-time',
    method: 'get'
  })



// 报表-自定义面板
// 用列表中的对应ID获取信息
export const boardData = (params: any) =>
  request({
    url: '/api/report/board-data',
    method: 'post',
    data: params
  })
// 通过指标查询设备列表
export const quotaDevices = (params: any) =>
  request({
    url: `/api/report/devices`,
    method: 'post',
    data: params
  })
// 预览接口
export const preview = (data: any) =>
  request({
    url: '/api/report/preview',
    method: 'post',
    data: data
  })
// 添加看板
// export const board = (data: any) =>
//   request({
//     url: '/api/board',
//     method: 'post',
//     data
//   })
