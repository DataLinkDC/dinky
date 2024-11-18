export function formatDate(inputDate:string) {
  const now = new Date();
  const then = new Date(inputDate);

  // 计算时间差
  const diff = (now - then) / 1000; // 转换为秒
  const diffMinutes = Math.floor(diff / 60); // 转换为分钟
  const diffHours = Math.floor(diff / 3600); // 转换为小时

  if (diff < 60) { // 如果小于1分钟，显示“刚刚”
    return "刚刚";
  } else if (diff < 3600 && now.toDateString() === then.toDateString()) { // 如果小于1小时且是同一天，显示几分钟前
    return `${diffMinutes}分钟前`;
  } else if (diff < 86400 && now.toDateString() === then.toDateString()) { // 如果小于1天且是同一天，显示几小时前
    return `${diffHours}小时前`;
  } else { // 否则显示日期和时间
    const options = { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' } as Intl.DateTimeFormatOptions;
    return then.toLocaleString('zh-CN', options).replace(/\//g, '-').slice(0, -3);
  }
}
