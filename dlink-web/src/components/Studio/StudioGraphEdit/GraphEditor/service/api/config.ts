import XKRequest from "../http";
//设置某一个实例
const xkrequest = new XKRequest({
  // baseURL: process.env.VUE_APP_BASE_URL,
  // timeout: process.env.VUE_APP_TIME_OUT,
  // baseURL: "http://jsonplaceholder.typicode.com",
  timeout: 5000,
  interceptors: {
    requestInterceptor: (config) => {
      return config;
    },
    requestInterceptorCatch: (error) => {
      return error;
    },
    responseInterceptor: (config) => {
      return config;
    },
    responseInterceptorCatch: (error) => {
      return error;
    },
  },
});

export default xkrequest;
