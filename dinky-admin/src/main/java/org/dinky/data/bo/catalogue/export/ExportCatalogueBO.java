package org.dinky.data.bo.catalogue.export;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportCatalogueBO {

    private String name;

    private Boolean enabled;

    private Boolean isLeaf;

    private ExportTaskBO task;

    private String type;

    private List<ExportCatalogueBO> children;

}
