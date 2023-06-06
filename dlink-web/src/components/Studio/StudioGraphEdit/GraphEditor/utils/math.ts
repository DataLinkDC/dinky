import {nanoid} from '@reduxjs/toolkit'

/**
 * 返回随机数
 * @returns {number}
 */
export function generateRandomNum() {
  return nanoid(6);
}
