package org.dinky.data.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ResourcesDTO implements Serializable {
    private Integer id;
    private String fileName;
    private String description;
}
