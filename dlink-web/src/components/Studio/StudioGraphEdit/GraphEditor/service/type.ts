import { AxiosRequestConfig, AxiosResponse } from "axios";
interface XKRequestInterceptors<T = AxiosResponse> {
  requestInterceptor?: (config: AxiosRequestConfig) => AxiosRequestConfig;
  requestInterceptorCatch?: (error: any) => any;
  responseInterceptor?: (config: T) => T;
  responseInterceptorCatch?: (error: any) => any;
}
interface XKRequestConfig<T = AxiosResponse> extends AxiosRequestConfig {
  interceptors?: XKRequestInterceptors<T>;
}

export type { XKRequestInterceptors, XKRequestConfig };
