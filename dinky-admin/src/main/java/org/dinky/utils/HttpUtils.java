package org.dinky.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class HttpUtils {
    public static void asyncRequest(List<String> addressList, String urlParams, int timeout, Consumer<HttpResponse> consumer){
        if (CollUtil.isEmpty(addressList)){
            return;
        }
        int index = RandomUtil.randomInt(addressList.size());
        String url = addressList.get(index);
        try {
            HttpUtil.createGet(url+urlParams)
                    .disableCache().timeout(timeout).then(consumer);
        } catch (Exception e) {
            log.error("url-timeout :{} ", url);
            addressList.remove(index);
            asyncRequest(addressList, urlParams, timeout,consumer);
        }
    }
}
