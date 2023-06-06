import axios from "axios";
import type { AxiosInstance } from "axios";
import type { XKRequestInterceptors, XKRequestConfig } from "./type";
class XKRequest {
  instance: AxiosInstance;
  interceptors?: XKRequestInterceptors;
  constructor(config: XKRequestConfig) {
    //创建axios实例对象
    this.instance = axios.create(config);
    this.interceptors = config.interceptors;
    //为一个个实例对象添加拦截器（需要传进来）
    this.instance.interceptors.request.use(
      // this.interceptors?.requestInterceptor,
      this.interceptors?.requestInterceptorCatch,
      this.interceptors?.requestInterceptor
    );
    this.instance.interceptors.response.use(
      this.interceptors?.responseInterceptor,
      this.interceptors?.responseInterceptorCatch
    );

    //为所有实例统一添加默认拦截器
    this.instance.interceptors.request.use(
      (config) => {
        return config;
      },
      (error) => {
        return error;
      }
    );
    this.instance.interceptors.response.use(
      (response) => {
        return response;
      },
      (error) => {
        return error;
      }
    );
  }
  request<T>(config: XKRequestConfig<T>): Promise<T> {
    return new Promise((resolve, reject) => {
      //为单个方法进行请求响应拦截
      if (config?.interceptors?.requestInterceptor) {
        config = config.interceptors.requestInterceptor(config);
      }
      this.instance
        .request<any, T>(config)
        .then((res) => {
          //为单个方法中进行响应拦截
          if (config?.interceptors?.responseInterceptor) {
            res = config.interceptors.responseInterceptor(res);
          }
          resolve(res);
        })
        .catch((error) => {
          reject(error);
        });
    });
  }
  get<T>(config: XKRequestConfig<T>): Promise<T> {
    return this.request<T>({ ...config, method: "GET" });
  }
  post<T>(config: XKRequestConfig<T>): Promise<T> {
    return this.request<T>({ ...config, method: "POST" });
  }
  delete<T>(config: XKRequestConfig<T>): Promise<T> {
    return this.request<T>({ ...config, method: "DELETE" });
  }
  patch<T>(config: XKRequestConfig<T>): Promise<T> {
    return this.request<T>({ ...config, method: "PATCH" });
  }
  put<T>(config: XKRequestConfig<T>): Promise<T> {
    return this.request<T>({ ...config, method: "PUT" });
  }
}

export default XKRequest;
