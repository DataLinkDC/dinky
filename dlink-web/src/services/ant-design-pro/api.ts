// @ts-ignore
/* eslint-disable */
import { request } from 'umi';



/** 获取当前的用户 GET /api/currentUser */
export async function currentUser(options?: { [key: string]: any }) {
  return request<API.Result>('/api/current', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 退出登录接口 POST /api-uaa/oauth/remove/token?token= */
export async function outLogin(options?: { [key: string]: any }) {
  return request<Record<string, any>>('/api/outLogin', {
    method: 'DELETE',
    ...(options || {}),
  });
}

/** 登录接口 POST /api-uaa/oauth/token */
export async function login(body: API.LoginParams, options?: { [key: string]: any }) {
  return request<API.Result>('/api/login', {
    method: 'POST',
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /api/notices */
export async function getNotices(options?: { [key: string]: any }) {
  return request<API.NoticeIconList>('/api/notices', {
    method: 'GET',
    ...(options || {}),
  });
}
