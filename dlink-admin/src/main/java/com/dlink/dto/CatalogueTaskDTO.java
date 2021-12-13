package com.dlink.dto;

import com.dlink.config.Dialect;
import lombok.Getter;
import lombok.Setter;

/**
 * CatalogueTaskDTO
 *
 * @author wenmo
 * @since 2021/6/1 20:16
 */
@Getter
@Setter
public class CatalogueTaskDTO {
    private Integer id;
    private Integer parentId;
    private boolean isLeaf;
    private String name;
    private String alias;
    private String dialect = Dialect.DEFAULT.getValue();
}
