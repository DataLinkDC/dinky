
const escape2Html = (str: string) => {
  let arrEntities:Record<string, string> = {'lt': '<', 'gt': '>', 'nbsp': ' ', 'amp': '&', 'quot': '"'};
  return str.replace(/&(lt|gt|nbsp|amp|quot);/ig, function (all, t) {
    return arrEntities[t];
  });
}
const getRangeText = (str: string) => {
  str = escape2Html(str);
  // @ts-ignore
  const canvas = getRangeText.canvas || (getRangeText.canvas = document.createElement("canvas"));
  const context = canvas.getContext("2d");
  context.font = "10px sans-serif";
  let result = '';
  let count = 1;
  for (let i = 0, len = str.length; i < len; i++) {
    result += str[i];
    let width = context.measureText(result).width;
    if (width >= 110 * count) {
      result += '\r\n';
      count++;
    }
  }
  return result;
};


export const buildGraphData = (data:any) => {
  const edges = [];
  for (const i in data.nodes) {
    data.nodes[i].id = data.nodes[i].id.toString();
    data.nodes[i].value = {
      title: data.nodes[i].pact,
      items: [
        {
          text: getRangeText(data.nodes[i].description),
        },
        {
          text: '\r\nParallelism: ',
          value: '\r\n  ' + data.nodes[i].parallelism,
        },
      ],
    };
    if (data.nodes[i].inputs) {
      for (let j in data.nodes[i].inputs) {
        edges.push({
          source: data.nodes[i].inputs[j].id.toString(),
          target: data.nodes[i].id.toString(),
          value: data.nodes[i].inputs[j].ship_strategy
        })
      }
    }
  }
  data.edges = edges;
  return data;
};
