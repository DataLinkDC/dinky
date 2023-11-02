package org.dinky.data.dto;

import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

@Data
public class CommonDTO<T> {
    private T data;
}
