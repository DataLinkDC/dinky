
export function getDBImage(type:string) {
  let imageUrl = 'database/';
  switch (type.toLowerCase()){
    case 'mysql':
      imageUrl += 'mysql.jpg';
      break;
    case 'oracle':
      imageUrl += 'oracle.jpg';
      break;
    case 'postgresql':
      imageUrl += 'postgresql.jpg';
      break;
    case 'clickhouse':
      imageUrl += 'clickhouse.png';
      break;
    default:
      imageUrl += 'db.jpg';
  }
  return imageUrl;
}
