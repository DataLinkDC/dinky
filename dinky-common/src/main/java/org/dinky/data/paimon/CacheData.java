package org.dinky.data.paimon;

import cn.hutool.core.map.MapUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;
import org.dinky.data.annotations.paimon.PartitionKey;
import org.dinky.data.annotations.paimon.PrimaryKey;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheData implements Serializable {
    private final static Map<String, String> OPTIONS = MapUtil.builder("file.format", "parquet")
            .put("snapshot.time-retained", "10 s")
            .build();
    @PartitionKey
    private String cacheName;
    @PrimaryKey
    private String key;
    /**
     * Serialized json data
     */
    private String data;
}
