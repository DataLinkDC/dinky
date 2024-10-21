/**
 * 计算右侧 proform list 组件宽度
 * @param width
 */
export const calculatorWidth = (width: number) => {
  const resultWidth = width - 50; // 50 为右侧 proform list 组件的 删除按钮宽度
  return resultWidth > 0 ? resultWidth / 2 : 300;
};
