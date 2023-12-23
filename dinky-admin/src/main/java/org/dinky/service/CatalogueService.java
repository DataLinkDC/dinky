/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.service;

import org.dinky.data.dto.CatalogueTaskDTO;
import org.dinky.data.model.Catalogue;
import org.dinky.data.result.Result;
import org.dinky.mybatis.service.ISuperService;

import java.util.List;

/**
 * CatalogueService
 *
 * @since 2021/5/28 14:01
 */
public interface CatalogueService extends ISuperService<Catalogue> {

    /**
     * Get the catalogue tree
     *
     * @return A list of {@link Catalogue} objects representing the catalogue tree.
     */
    List<Catalogue> getCatalogueTree();

    /**
     * Find a catalogue by its parent ID and name.
     *
     * @param parentId The ID of the parent catalogue to find the child catalogue for.
     * @param name The name of the catalogue to find.
     * @return A {@link Catalogue} object representing the found catalogue.
     */
    Catalogue findByParentIdAndName(Integer parentId, String name);

    /**
     * Save or update a catalogue and its tasks.
     *
     * @param catalogueTaskDTO A {@link CatalogueTaskDTO} object representing the updated catalogue and tasks.
     * @return A {@link Catalogue} object representing the saved or updated catalogue.
     */
    Catalogue saveOrUpdateCatalogueAndTask(CatalogueTaskDTO catalogueTaskDTO);

    /**
     * Create a catalogue and file task based on the given catalogue task DTO.
     *
     * @param catalogueTaskDTO A {@link CatalogueTaskDTO} object representing the catalogue task to create.
     * @param ment The name of the catalogue to create.
     * @return A boolean value indicating whether the catalogue was created successfully.
     */
    Catalogue createCatalogAndFileTask(CatalogueTaskDTO catalogueTaskDTO, String ment);

    /**
     * Check if the given catalogue needs to be renamed.
     *
     * @param catalogue A {@link Catalogue} object representing the catalogue to check.
     * @return A boolean value indicating whether the catalogue needs to be renamed.
     */
    boolean toRename(Catalogue catalogue);

    /**
     * Move the given catalogue from one parent ID to another.
     *
     * @param originCatalogueId The ID of the catalogue to move.
     * @param targetParentId The ID of the new parent for the catalogue.
     * @return A boolean value indicating whether the catalogue was moved successfully.
     */
    boolean moveCatalogue(Integer originCatalogueId, Integer targetParentId);

    /**
     * Copy a task from the given catalogue.
     *
     * @param catalogue A {@link Catalogue} object representing the catalogue to copy the task from.
     * @return A boolean value indicating whether the task was copied successfully.
     */
    boolean copyTask(Catalogue catalogue);

    /**
     * Add dependent catalogues for the given catalogue names.
     *
     * @param catalogueNames An array of strings representing the names of the catalogues to add as dependents.
     * @return An integer representing the ID of the added catalogue.
     */
    Integer addDependCatalogue(String[] catalogueNames);

    /**
     * Traverse the file system based on the given source path and catalogue.
     *
     * @param sourcePath The path of the source directory to traverse.
     * @param catalog A {@link Catalogue} object representing the catalogue to use for filtering files.
     */
    void traverseFile(String sourcePath, Catalogue catalog);

    /**
     * Get the catalogue with the given parent ID and name.
     *
     * @param parentId The ID of the parent catalogue to find the child catalogue for.
     * @param name The name of the catalogue to find.
     * @return A {@link Catalogue} object representing the found catalogue.
     */
    Catalogue getCatalogue(Integer parentId, String name);

    /**
     * Delete a catalogue by its ID.
     *
     * @param catalogueId The ID of the catalogue to delete.
     * @return A {@link Result<Void>} object representing the result of the deletion operation.
     */
    Result<Void> deleteCatalogueById(Integer catalogueId);

    /**
     * Save, update, or rename a catalogue based on the given parameters.
     *
     * @param catalogue A {@link Catalogue} object representing the catalogue to save, update, or rename.
     * @return A boolean value indicating whether the operation was successful.
     */
    Boolean saveOrUpdateOrRename(Catalogue catalogue);

    /**
     * Check if the catalogue task name is exist
     * @param name catalogue task name
     * @return true if the catalogue task name is exist
     */
    boolean checkCatalogueTaskNameIsExist(String name);
}
