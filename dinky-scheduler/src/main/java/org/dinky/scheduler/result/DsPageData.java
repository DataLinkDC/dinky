package org.dinky.scheduler.result;

import lombok.Data;

import java.util.List;

@Data
public class DsPageData<T> {
    private List<T> totalList;
}
