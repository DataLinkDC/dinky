/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import TableLineage from "@/components/TableLineage"
import type {GraphData} from "@antv/g6/src/spec/data";
import {useState} from 'react';
import {lineageDataTransformToGraphData} from "@/components/TableLineage/function"


export default () => {

  const lineageData = {
    "tables": [
      {
        "id": "5",
        "name": "default_catalog.default_database.ods_order_sale_goods_snapshot_r",
        "columns": [
          {
            "name": "category_code",
            "title": "category_code"
          },
          {
            "name": "category_des",
            "title": "category_des"
          },
          {
            "name": "purchase_kind_code",
            "title": "purchase_kind_code"
          },
          {
            "name": "purchase_kind_des",
            "title": "purchase_kind_des"
          },
          {
            "name": "is_unmember_goods",
            "title": "is_unmember_goods"
          },
          {
            "name": "retail_price",
            "title": "retail_price"
          },
          {
            "name": "member_price",
            "title": "member_price"
          },
          {
            "name": "is_unfree_goods",
            "title": "is_unfree_goods"
          },
          {
            "name": "proc_time",
            "title": "proc_time"
          }
        ]
      },
      {
        "id": "3",
        "name": "default_catalog.default_database.dwd_sd_so_ordr_detl_r",
        "columns": [
          {
            "name": "stsc_date",
            "title": "stsc_date"
          },
          {
            "name": "sale_ordr_doc",
            "title": "sale_ordr_doc"
          },
          {
            "name": "ordr_sour_code",
            "title": "ordr_sour_code"
          },
          {
            "name": "sale_order_proc_time",
            "title": "sale_order_proc_time"
          },
          {
            "name": "ordr_sale_time",
            "title": "ordr_sale_time"
          },
          {
            "name": "prmn_prog_type_code",
            "title": "prmn_prog_type_code"
          },
          {
            "name": "ordr_type_code",
            "title": "ordr_type_code"
          },
          {
            "name": "ordr_cate_code",
            "title": "ordr_cate_code"
          },
          {
            "name": "prmn_prog_code",
            "title": "prmn_prog_code"
          },
          {
            "name": "coup_type",
            "title": "coup_type"
          },
          {
            "name": "coup_code",
            "title": "coup_code"
          },
          {
            "name": "line_item",
            "title": "line_item"
          },
          {
            "name": "refn_ordr_type_code",
            "title": "refn_ordr_type_code"
          },
          {
            "name": "refn_ordr_doc",
            "title": "refn_ordr_doc"
          },
          {
            "name": "refn_ordr_item",
            "title": "refn_ordr_item"
          },
          {
            "name": "memb_disc_mode",
            "title": "memb_disc_mode"
          },
          {
            "name": "goods_code",
            "title": "goods_code"
          },
          {
            "name": "sale_order_details_proc_time",
            "title": "sale_order_details_proc_time"
          },
          {
            "name": "cate_clas_code",
            "title": "cate_clas_code"
          },
          {
            "name": "cate_clas_name",
            "title": "cate_clas_name"
          },
          {
            "name": "purc_clas_code_new",
            "title": "purc_clas_code_new"
          },
          {
            "name": "purc_clas_name_new",
            "title": "purc_clas_name_new"
          },
          {
            "name": "lotn",
            "title": "lotn"
          },
          {
            "name": "sale_tax",
            "title": "sale_tax"
          },
          {
            "name": "memb_id",
            "title": "memb_id"
          },
          {
            "name": "memb_point",
            "title": "memb_point"
          },
          {
            "name": "chk_out_id",
            "title": "chk_out_id"
          },
          {
            "name": "casr_id",
            "title": "casr_id"
          },
          {
            "name": "is_memb_goods",
            "title": "is_memb_goods"
          },
          {
            "name": "retail_pric",
            "title": "retail_pric"
          },
          {
            "name": "memb_pric",
            "title": "memb_pric"
          },
          {
            "name": "exec_pric_type",
            "title": "exec_pric_type"
          },
          {
            "name": "divid_pric",
            "title": "divid_pric"
          },
          {
            "name": "acnt_pric",
            "title": "acnt_pric"
          },
          {
            "name": "exec_pric",
            "title": "exec_pric"
          },
          {
            "name": "dct_amt",
            "title": "dct_amt"
          },
          {
            "name": "gift_dct_amt",
            "title": "gift_dct_amt"
          },
          {
            "name": "other_free_amt",
            "title": "other_free_amt"
          },
          {
            "name": "amt1",
            "title": "amt1"
          },
          {
            "name": "amt2",
            "title": "amt2"
          },
          {
            "name": "sale_amt",
            "title": "sale_amt"
          },
          {
            "name": "sale_qty",
            "title": "sale_qty"
          },
          {
            "name": "china_med_qty",
            "title": "china_med_qty"
          },
          {
            "name": "is_n_surd_prof",
            "title": "is_n_surd_prof"
          },
          {
            "name": "cost_pric",
            "title": "cost_pric"
          },
          {
            "name": "cost_amt",
            "title": "cost_amt"
          },
          {
            "name": "is_effe_ordr",
            "title": "is_effe_ordr"
          },
          {
            "name": "order_code",
            "title": "order_code"
          },
          {
            "name": "stat_date",
            "title": "stat_date"
          },
          {
            "name": "proj_cate_code",
            "title": "proj_cate_code"
          },
          {
            "name": "purchase_kind_code",
            "title": "purchase_kind_code"
          },
          {
            "name": "sale_goods_snapshot_proc_time",
            "title": "sale_goods_snapshot_proc_time"
          },
          {
            "name": "casr_code",
            "title": "casr_code"
          },
          {
            "name": "service_charge",
            "title": "service_charge"
          },
          {
            "name": "sale_pers_id",
            "title": "sale_pers_id"
          },
          {
            "name": "phmc_code",
            "title": "phmc_code"
          },
          {
            "name": "out_phmc_code",
            "title": "out_phmc_code"
          },
          {
            "name": "is_ydch_flag",
            "title": "is_ydch_flag"
          },
          {
            "name": "coup_prog_code",
            "title": "coup_prog_code"
          }
        ]
      },
      {
        "id": "4",
        "name": "default_catalog.default_database.ods_order_sale_order_r",
        "columns": [
          {
            "name": "order_from",
            "title": "order_from"
          },
          {
            "name": "proc_time",
            "title": "proc_time"
          },
          {
            "name": "bill_type",
            "title": "bill_type"
          },
          {
            "name": "bill_kind",
            "title": "bill_kind"
          },
          {
            "name": "coupon_type",
            "title": "coupon_type"
          },
          {
            "name": "coupon_code",
            "title": "coupon_code"
          },
          {
            "name": "member_id",
            "title": "member_id"
          },
          {
            "name": "point_number",
            "title": "point_number"
          },
          {
            "name": "cashdesk_id",
            "title": "cashdesk_id"
          },
          {
            "name": "casher_code",
            "title": "casher_code"
          },
          {
            "name": "cash_free_money",
            "title": "cash_free_money"
          },
          {
            "name": "gift_free_money",
            "title": "gift_free_money"
          },
          {
            "name": "other_free_money",
            "title": "other_free_money"
          },
          {
            "name": "pair_count",
            "title": "pair_count"
          },
          {
            "name": "order_code",
            "title": "order_code"
          },
          {
            "name": "sale_order_code",
            "title": "sale_order_code"
          },
          {
            "name": "coupon_plan_code",
            "title": "coupon_plan_code"
          }
        ]
      },
      {
        "id": "7",
        "name": "default_catalog.default_database.ods_zt_ord_order_r",
        "columns": [
          {
            "name": "from_warehouse_code",
            "title": "from_warehouse_code"
          },
          {
            "name": "warehouse_code",
            "title": "warehouse_code"
          },
          {
            "name": "pos_code",
            "title": "pos_code"
          }
        ]
      },
      {
        "id": "2",
        "name": "default_catalog.default_database.ods_order_sale_order_details_r",
        "columns": [
          {
            "name": "sale_time",
            "title": "sale_time"
          },
          {
            "name": "sale_order_code",
            "title": "sale_order_code"
          },
          {
            "name": "promotionbill_plan_type",
            "title": "promotionbill_plan_type"
          },
          {
            "name": "row_num",
            "title": "row_num"
          },
          {
            "name": "refer_bill_type",
            "title": "refer_bill_type"
          },
          {
            "name": "refer_bill_code",
            "title": "refer_bill_code"
          },
          {
            "name": "refer_bill_row_num",
            "title": "refer_bill_row_num"
          },
          {
            "name": "member_coupon_type",
            "title": "member_coupon_type"
          },
          {
            "name": "good_code",
            "title": "good_code"
          },
          {
            "name": "proc_time",
            "title": "proc_time"
          },
          {
            "name": "lot_num",
            "title": "lot_num"
          },
          {
            "name": "output_tax",
            "title": "output_tax"
          },
          {
            "name": "perform_price_type",
            "title": "perform_price_type"
          },
          {
            "name": "apportion_price",
            "title": "apportion_price"
          },
          {
            "name": "account_price",
            "title": "account_price"
          },
          {
            "name": "perform_price",
            "title": "perform_price"
          },
          {
            "name": "perform_profit_margin",
            "title": "perform_profit_margin"
          },
          {
            "name": "account_price_gross_money",
            "title": "account_price_gross_money"
          },
          {
            "name": "account_money",
            "title": "account_money"
          },
          {
            "name": "quantity",
            "title": "quantity"
          },
          {
            "name": "cost_price",
            "title": "cost_price"
          },
          {
            "name": "item_type",
            "title": "item_type"
          },
          {
            "name": "service_charge",
            "title": "service_charge"
          },
          {
            "name": "store_code",
            "title": "store_code"
          }
        ]
      },
      {
        "id": "6",
        "name": "default_catalog.default_database.ods_order_sales_ordersalesman_r",
        "columns": [
          {
            "name": "sales_job_number",
            "title": "sales_job_number"
          }
        ]
      }
    ],
    "relations": [
      {
        "id": "2",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "sale_time",
        "tgtTableColName": "stsc_date"
      },
      {
        "id": "3",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "sale_order_code",
        "tgtTableColName": "sale_ordr_doc"
      },
      {
        "id": "4",
        "srcTableId": "4",
        "tgtTableId": "3",
        "srcTableColName": "order_from",
        "tgtTableColName": "ordr_sour_code"
      },
      {
        "id": "5",
        "srcTableId": "4",
        "tgtTableId": "3",
        "srcTableColName": "proc_time",
        "tgtTableColName": "sale_order_proc_time"
      },
      {
        "id": "6",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "sale_time",
        "tgtTableColName": "ordr_sale_time"
      },
      {
        "id": "7",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "promotionbill_plan_type",
        "tgtTableColName": "prmn_prog_type_code"
      },
      {
        "id": "8",
        "srcTableId": "4",
        "tgtTableId": "3",
        "srcTableColName": "bill_type",
        "tgtTableColName": "ordr_type_code"
      },
      {
        "id": "9",
        "srcTableId": "4",
        "tgtTableId": "3",
        "srcTableColName": "bill_kind",
        "tgtTableColName": "ordr_cate_code"
      },
      {
        "id": "10",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "promotionbill_plan_type",
        "tgtTableColName": "prmn_prog_code"
      },
      {
        "id": "11",
        "srcTableId": "4",
        "tgtTableId": "3",
        "srcTableColName": "coupon_type",
        "tgtTableColName": "coup_type"
      },
      {
        "id": "12",
        "srcTableId": "4",
        "tgtTableId": "3",
        "srcTableColName": "coupon_code",
        "tgtTableColName": "coup_code"
      },
      {
        "id": "13",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "row_num",
        "tgtTableColName": "line_item"
      },
      {
        "id": "14",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "refer_bill_type",
        "tgtTableColName": "refn_ordr_type_code"
      },
      {
        "id": "15",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "refer_bill_code",
        "tgtTableColName": "refn_ordr_doc"
      },
      {
        "id": "16",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "refer_bill_row_num",
        "tgtTableColName": "refn_ordr_item"
      },
      {
        "id": "17",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "member_coupon_type",
        "tgtTableColName": "memb_disc_mode"
      },
      {
        "id": "18",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "good_code",
        "tgtTableColName": "goods_code"
      },
      {
        "id": "19",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "proc_time",
        "tgtTableColName": "sale_order_details_proc_time"
      },
      {
        "id": "20",
        "srcTableId": "5",
        "tgtTableId": "3",
        "srcTableColName": "category_code",
        "tgtTableColName": "cate_clas_code"
      },
      {
        "id": "21",
        "srcTableId": "5",
        "tgtTableId": "3",
        "srcTableColName": "category_des",
        "tgtTableColName": "cate_clas_name"
      },
      {
        "id": "22",
        "srcTableId": "5",
        "tgtTableId": "3",
        "srcTableColName": "purchase_kind_code",
        "tgtTableColName": "purc_clas_code_new"
      },
      {
        "id": "23",
        "srcTableId": "5",
        "tgtTableId": "3",
        "srcTableColName": "purchase_kind_des",
        "tgtTableColName": "purc_clas_name_new"
      },
      {
        "id": "24",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "lot_num",
        "tgtTableColName": "lotn"
      },
      {
        "id": "25",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "output_tax",
        "tgtTableColName": "sale_tax"
      },
      {
        "id": "26",
        "srcTableId": "4",
        "tgtTableId": "3",
        "srcTableColName": "member_id",
        "tgtTableColName": "memb_id"
      },
      {
        "id": "27",
        "srcTableId": "4",
        "tgtTableId": "3",
        "srcTableColName": "point_number",
        "tgtTableColName": "memb_point"
      },
      {
        "id": "28",
        "srcTableId": "4",
        "tgtTableId": "3",
        "srcTableColName": "cashdesk_id",
        "tgtTableColName": "chk_out_id"
      },
      {
        "id": "29",
        "srcTableId": "4",
        "tgtTableId": "3",
        "srcTableColName": "casher_code",
        "tgtTableColName": "casr_id"
      },
      {
        "id": "30",
        "srcTableId": "5",
        "tgtTableId": "3",
        "srcTableColName": "is_unmember_goods",
        "tgtTableColName": "is_memb_goods"
      },
      {
        "id": "31",
        "srcTableId": "5",
        "tgtTableId": "3",
        "srcTableColName": "retail_price",
        "tgtTableColName": "retail_pric"
      },
      {
        "id": "32",
        "srcTableId": "5",
        "tgtTableId": "3",
        "srcTableColName": "member_price",
        "tgtTableColName": "memb_pric"
      },
      {
        "id": "33",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "perform_price_type",
        "tgtTableColName": "exec_pric_type"
      },
      {
        "id": "34",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "apportion_price",
        "tgtTableColName": "divid_pric"
      },
      {
        "id": "35",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "account_price",
        "tgtTableColName": "acnt_pric"
      },
      {
        "id": "36",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "perform_price",
        "tgtTableColName": "exec_pric"
      },
      {
        "id": "37",
        "srcTableId": "4",
        "tgtTableId": "3",
        "srcTableColName": "cash_free_money",
        "tgtTableColName": "dct_amt"
      },
      {
        "id": "38",
        "srcTableId": "4",
        "tgtTableId": "3",
        "srcTableColName": "gift_free_money",
        "tgtTableColName": "gift_dct_amt"
      },
      {
        "id": "39",
        "srcTableId": "4",
        "tgtTableId": "3",
        "srcTableColName": "other_free_money",
        "tgtTableColName": "other_free_amt"
      },
      {
        "id": "40",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "perform_profit_margin",
        "tgtTableColName": "amt1"
      },
      {
        "id": "41",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "account_price_gross_money",
        "tgtTableColName": "amt2"
      },
      {
        "id": "42",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "account_money",
        "tgtTableColName": "sale_amt"
      },
      {
        "id": "43",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "quantity",
        "tgtTableColName": "sale_qty"
      },
      {
        "id": "44",
        "srcTableId": "4",
        "tgtTableId": "3",
        "srcTableColName": "pair_count",
        "tgtTableColName": "china_med_qty"
      },
      {
        "id": "45",
        "srcTableId": "5",
        "tgtTableId": "3",
        "srcTableColName": "is_unfree_goods",
        "tgtTableColName": "is_n_surd_prof"
      },
      {
        "id": "46",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "cost_price",
        "tgtTableColName": "cost_pric"
      },
      {
        "id": "47",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "cost_price",
        "tgtTableColName": "cost_amt"
      },
      {
        "id": "48",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "quantity",
        "tgtTableColName": "cost_amt"
      },
      {
        "id": "49",
        "srcTableId": "4",
        "tgtTableId": "3",
        "srcTableColName": "bill_kind",
        "tgtTableColName": "is_effe_ordr"
      },
      {
        "id": "50",
        "srcTableId": "4",
        "tgtTableId": "3",
        "srcTableColName": "order_code",
        "tgtTableColName": "order_code"
      },
      {
        "id": "51",
        "srcTableId": "4",
        "tgtTableId": "3",
        "srcTableColName": "sale_order_code",
        "tgtTableColName": "order_code"
      },
      {
        "id": "52",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "sale_time",
        "tgtTableColName": "stat_date"
      },
      {
        "id": "53",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "item_type",
        "tgtTableColName": "proj_cate_code"
      },
      {
        "id": "54",
        "srcTableId": "5",
        "tgtTableId": "3",
        "srcTableColName": "purchase_kind_code",
        "tgtTableColName": "purchase_kind_code"
      },
      {
        "id": "55",
        "srcTableId": "5",
        "tgtTableId": "3",
        "srcTableColName": "proc_time",
        "tgtTableColName": "sale_goods_snapshot_proc_time"
      },
      {
        "id": "56",
        "srcTableId": "4",
        "tgtTableId": "3",
        "srcTableColName": "casher_code",
        "tgtTableColName": "casr_code"
      },
      {
        "id": "57",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "service_charge",
        "tgtTableColName": "service_charge"
      },
      {
        "id": "58",
        "srcTableId": "6",
        "tgtTableId": "3",
        "srcTableColName": "sales_job_number",
        "tgtTableColName": "sale_pers_id"
      },
      {
        "id": "59",
        "srcTableId": "7",
        "tgtTableId": "3",
        "srcTableColName": "from_warehouse_code",
        "tgtTableColName": "phmc_code"
      },
      {
        "id": "60",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "store_code",
        "tgtTableColName": "phmc_code"
      },
      {
        "id": "61",
        "srcTableId": "7",
        "tgtTableId": "3",
        "srcTableColName": "warehouse_code",
        "tgtTableColName": "out_phmc_code"
      },
      {
        "id": "62",
        "srcTableId": "2",
        "tgtTableId": "3",
        "srcTableColName": "store_code",
        "tgtTableColName": "out_phmc_code"
      },
      {
        "id": "63",
        "srcTableId": "7",
        "tgtTableId": "3",
        "srcTableColName": "pos_code",
        "tgtTableColName": "is_ydch_flag"
      },
      {
        "id": "64",
        "srcTableId": "4",
        "tgtTableId": "3",
        "srcTableColName": "coupon_plan_code",
        "tgtTableColName": "coup_prog_code"
      }
    ]
  };

  const data: GraphData = {
    nodes: [{ id: 'node1' }, { id: 'node2' }, { id: 'node3' }, { id: 'node4' }, { id: 'node5' }, { id: 'node6' }],
    edges: [
      { source: 'node1', target: 'node2' },
      { source: 'node1', target: 'node3' },
      { source: 'node1', target: 'node4', text: 'cubic-horizontal-dhgsjagdhjsagdhjskabxhjsvaghjdahjsvxhjsfagdhjsahjdga' },
      { source: 'node1', target: 'node5' },
      { source: 'node1', target: 'node6' },
    ],
  };

  const [graphData, setGraphData] = useState<GraphData>(lineageDataTransformToGraphData(lineageData));
  const [refreshLoading, setRefreshLoading] = useState<boolean>(false);

  const refresh = () => {
    setRefreshLoading(true);
    setTimeout(() => {
      setRefreshLoading(false);
    }, 2000);
    setGraphData(lineageDataTransformToGraphData(lineageData));

  };


  return  <TableLineage animation={true} data={graphData} dataRefreshCallback={refresh} refreshLoading={refreshLoading}/>;
}
