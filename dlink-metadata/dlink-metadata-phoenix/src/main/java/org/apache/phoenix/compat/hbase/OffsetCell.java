/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.phoenix.compat.hbase;

import org.apache.hadoop.hbase.Cell;

public class OffsetCell implements Cell {

    private Cell cell;
    private int offset;

    public OffsetCell(Cell cell, int offset) {
        this.cell = cell;
        this.offset = offset;
    }

    @Override
    public byte[] getRowArray() {
        return cell.getRowArray();
    }

    @Override
    public int getRowOffset() {
        return cell.getRowOffset() + offset;
    }

    @Override
    public short getRowLength() {
        return (short) (cell.getRowLength() - offset);
    }

    @Override
    public byte[] getFamilyArray() {
        return cell.getFamilyArray();
    }

    @Override
    public int getFamilyOffset() {
        return cell.getFamilyOffset();
    }

    @Override
    public byte getFamilyLength() {
        return cell.getFamilyLength();
    }

    @Override
    public byte[] getQualifierArray() {
        return cell.getQualifierArray();
    }

    @Override
    public int getQualifierOffset() {
        return cell.getQualifierOffset();
    }

    @Override
    public int getQualifierLength() {
        return cell.getQualifierLength();
    }

    @Override
    public long getTimestamp() {
        return cell.getTimestamp();
    }

    @Override
    public byte getTypeByte() {
        return cell.getTypeByte();
    }

    @Override public long getSequenceId() {
        return cell.getSequenceId();
    }

    @Override
    public byte[] getValueArray() {
        return cell.getValueArray();
    }

    @Override
    public int getValueOffset() {
        return cell.getValueOffset();
    }

    @Override
    public int getValueLength() {
        return cell.getValueLength();
    }

    @Override
    public byte[] getTagsArray() {
        return cell.getTagsArray();
    }

    @Override
    public int getTagsOffset() {
        return cell.getTagsOffset();
    }

    @Override
    public int getTagsLength() {
        return cell.getTagsLength();
    }

    @Override
    public Type getType() {
        return cell.getType();
    }

    @Override
    public long heapSize() {
        return cell.heapSize();
    }

    @Override
    public int getSerializedSize() {
        return cell.getSerializedSize() - offset;
    }

}
