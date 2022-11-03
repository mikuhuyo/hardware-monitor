import request from '@/utils/request'

export const getUserInfo = (data: any) =>
  request({
    url: '/api/users/info',
    method: 'get',
    data
  })

export const login = (data: any) =>
  request({
    url: '/api/login',
    method: 'post',
    data
  })

export const logout = () =>
  request({
    url: '/api/users/logout',
    method: 'delete'
  })

export const getRsaKey = () =>
  request({
    url: '/api/rsa-key',
    method: 'get'
  })

export const getPasswordRsa = (password : any) =>
  request({
    url: `/api/rsa-key/${password}`,
    method: 'get'
  })
