
create table ods_order_sale_goods_snapshot_r
(
    store_code string,
    --import_code string,
    good_code string,
    create_time string,
    category_code string,
    purchase_kind_code string,
    modify_time string,
    is_unfree_goods char,
    is_unmember_goods char,
    member_price decimal(22,4),
    retail_price decimal(22,4),
    purchase_kind_des string,
    sale_order_code string,
    id string,
    category_des string,
    proc_time as proctime(),
    sale_time TIMESTAMP(3)  ,
    origin_table STRING   ,
    PRIMARY KEY (id) NOT ENFORCED
) with (
      'connector'='datagen'
      );

create table ods_order_sale_order_details_r
(
    --import_code string,
    member_coupon_type string,
    lot_num string,
    item_type string,
    modify_time string,
    perform_price_type string,
    row_num int,
    account_price decimal(22,4),
    perform_price decimal(22,4),
    sale_order_code string,
    refer_bill_code string,
    id string,
    cost_price decimal(22,4),
    store_code string,
    account_money decimal(22,4),
    good_code string,
    quantity decimal(22,4),
    apportion_price decimal(22,4),
    create_time string,
    --is_preorder string,
    perform_profit_margin decimal(22,4),
    --promotionbill_plan_code string,
    service_charge decimal(22,4),
    promotionbill_plan_type string,
    account_price_gross_money decimal(22,4),
    refer_bill_type string,
    refer_bill_row_num int,
    output_tax string,
    --useful int,
    proc_time as proctime(),
    sale_time TIMESTAMP(3) ,
    PRIMARY KEY (sale_order_code,row_num,lot_num,good_code) NOT ENFORCED
) with (
      'connector'='datagen'
      );

create table ods_order_sales_ordersalesman_r
(
    store_code       string,
    create_time      string,
    edit_time        string,
    sale_order_code  string,
    sales_job_number     string,
    id string,
    row_num int,
    sale_details_id string,
    sale_time TIMESTAMP(3) ,
    PRIMARY KEY (id) NOT ENFORCED
) with (
      'connector'='datagen'
      );

create table ods_order_sale_order_r
(
    --import_code string,
    modify_time string,
    --around_money decimal(22,4),
    --odd_change_money decimal,
    --customer_pay_money decimal,
    sale_order_code string,
    other_free_money decimal,
    gift_free_money decimal,
    bill_type string,
    refer_bill_code string,
    pair_count decimal,
    store_code string,
    member_id string,
    cashdesk_id string,
    point_number decimal,
    coupon_code string,
    create_time string,
    casher_code string,
    bill_kind string,
    order_code string,
    order_from string,
    refer_bill_type string,
    coupon_plan_code string,
    cash_free_money decimal,
    coupon_type string,
    proc_time as proctime(),
    sale_time TIMESTAMP(3) ,
    PRIMARY KEY (sale_order_code) NOT ENFORCED
) with (
      'connector'='datagen'
      );


CREATE TABLE IF NOT EXISTS ods_zt_ord_order_r (
                                                  id                        string,
                                                  parent_order_code         string,
                                                  order_code                string,
                                                  customer_code             string,
                                                  top_channel_code          string,
                                                  goods_channel_code        string,
                                                  pos_code                  string,
                                                  channel_code              string,
                                                  warehouse_code            string,
                                                  state                     string,
                                                  need_receive_amount       decimal(22,4),
    customer_need_pay_amount   decimal(22,4),
    goods_total_amount         decimal(22,4),
    expense_total_amount       decimal(22,4),
    preferential_total_amount  decimal(22,4),
    subsidy_total_amount       decimal(22,4),
    decucted_total_amount      decimal(22,4),
    exercise_total_amount      decimal(22,4),
    ordonnance_id             string,
    splited                   string,
    delivery_provider         string,
    payee                     string,
    referrer_id               string,
    ticket_num                string,
    referrer_name             string,
    out_trade_code            string,
    remark                    string,
    creater_id                string,
    create_time               string,
    modify_time               string,
    del_flag                  string,
    from_warehouse_code       string,
    order_type                int,
    PRIMARY KEY (`id`) NOT ENFORCED
    ) WITH (
          'connector'='datagen'
          );

CREATE TABLE IF NOT EXISTS dwd_sd_so_ordr_detl_r (
                                                     stsc_date string comment '统计日期-天',
                                                     sale_ordr_doc string comment '销售订单编号',
    --store_code string comment '门店编码',
                                                     ordr_sour_code string comment '订单来源编码',
                                                     sale_order_proc_time timestamp comment '',
                                                     ordr_sale_time string comment '',
                                                     prmn_prog_type_code string comment '促销方案类型编码',
                                                     ordr_type_code string comment '订单类型编码(销售单/退货单)',
                                                     ordr_cate_code string comment '订单类别编码(标准/团购单/赠品记账(积分兑奖)/财务记账(订金))',
                                                     prmn_prog_code string comment '促销方案编码',
                                                     coup_type string comment '',
                                                     coup_code string comment '',
                                                     line_item INT comment '行项目',
                                                     refn_ordr_type_code string comment '参考单据类型编码(销售单/团购单/处方单/订金退货申请单)',
                                                     refn_ordr_doc string comment '参考单据编号',
                                                     refn_ordr_item INT comment '参考单据行项目号',
                                                     memb_disc_mode string comment '会员优惠方式(会员折扣/会员价/VIP会员价)',
    --proj_cate string comment '',
                                                     goods_code string comment '商品编码',
                                                     sale_order_details_proc_time timestamp comment '',
                                                     cate_clas_code string comment '',
                                                     cate_clas_name string comment '',
                                                     purc_clas_code_new string comment '',
                                                     purc_clas_name_new string comment '',
                                                     lotn string comment '批号',
                                                     sale_tax string comment '',
                                                     memb_id string comment '会员ID',
                                                     memb_point decimal comment '',
                                                     chk_out_id string comment '',
                                                     casr_id string comment '',
                                                     is_memb_goods string comment '非会员商品标识',
                                                     retail_pric decimal(22,4) comment '零售单价',
    memb_pric decimal(22,4) comment '会员单价',
    exec_pric_type string comment '执行价类型编码',
    --befo_divid_pric decimal(22,4) comment '',
    divid_pric decimal(22,4) comment '医保单价',
    acnt_pric decimal(22,4) comment '记账单价',
    exec_pric decimal(22,4) comment '执行单价',
    dct_amt decimal comment '',
    gift_dct_amt decimal comment '',
    other_free_amt decimal comment '',
    amt1 decimal(22,4) comment '',
    amt2 decimal(22,4) comment '',
    sale_amt decimal(22,4) comment '销售金额',
    sale_qty decimal(22,4) comment '销售数量',
    china_med_qty decimal comment '',
    etl_time timestamp comment '',
    is_n_surd_prof string comment '不让利商品标识',
    cost_pric decimal(22,4) comment '成本单价',
    cost_amt decimal(22,4) comment '',
    --sale_cost_amt decimal(22,4) comment '销售成本额',
    is_effe_ordr string comment '是否有效订单',
    order_code string comment '',
    is_ecp_self_dstn_ordr string comment '是否ECP自配送订单',
    stat_date string comment '',
    proj_cate_code string comment '项目类别编码',
    purchase_kind_code string comment '项目类别编码',
    sale_goods_snapshot_proc_time timestamp comment '',
    casr_code string comment '收银员编码',
    service_charge decimal(22,4)  comment '',
    sale_pers_id string comment '营销员ID',
    phmc_code string comment '门店编码',
    out_phmc_code  string comment '出货门店',
    is_ydch_flag   string comment '是否异店出货',
    coup_prog_code string comment '券方案编码',
    PRIMARY KEY (stsc_date,sale_ordr_doc,goods_code,line_item,lotn) NOT ENFORCED
    ) WITH (
          'connector'='print'
          );


insert into dwd_sd_so_ordr_detl_r
select ifnull(date_format(cast(t1.sale_time as timestamp), 'yyyyMMdd'), 'NA')  as stsc_date
     , t1.sale_order_code                                                                  as sale_ordr_doc
     --, t1.store_code                                                                       as store_code
     , (case when coalesce(t.order_from, '') = '' then '9999' else t.order_from end)       as ordr_sour_code
     , t.proc_time                                               as sale_order_proc_time
     , date_format(cast(t1.sale_time as timestamp),'yyyy-MM-dd HH:mm:ss') as ordr_sale_time
     , t1.promotionbill_plan_type                                as prmn_prog_type_code
     , t.bill_type                                               as ordr_type_code
     , t.bill_kind                                               as ordr_cate_code
     , t1.promotionbill_plan_type                                as prmn_prog_code
     , t.coupon_type                                             as coup_type
     , t.coupon_code                                             as coup_code
     , t1.row_num                                                as line_item
     , t1.refer_bill_type                                        as refn_ordr_type_code
     , t1.refer_bill_code                                        as refn_ordr_doc
     , t1.refer_bill_row_num                                     as refn_ordr_item
     , t1.member_coupon_type                                     as memb_disc_mode
     --, t1.item_type                                              as proj_cate
     , t1.good_code                                              as goods_code
     , t1.proc_time                                              as sale_order_details_proc_time
     , t2.category_code                                          as cate_clas_code
     , t2.category_des                                           as cate_clas_name
     , t2.purchase_kind_code                                     as purc_clas_code_new
     , t2.purchase_kind_des                                      as purc_clas_name_new
     , t1.lot_num                                                as lotn
     , t1.output_tax                                             as sale_tax
     , t.member_id                                               as memb_id
     , t.point_number                                            as memb_point
     , t.cashdesk_id                                             as chk_out_id
     ,case when coalesce(t.casher_code , '') = '' then 'NA' else LPAD(CAST(t.casher_code  as string), 8, '0') end as casr_id
     , t2.is_unmember_goods                                      as is_memb_goods
     , t2.retail_price                                           as retail_pric
     , t2.member_price                                           as memb_pric
     , t1.perform_price_type                                     as exec_pric_type
     --, t1.perform_price                                          as befo_divid_pric
     , t1.apportion_price                                        as divid_pric
     , t1.account_price                                          as acnt_pric
     , t1.perform_price                                          as exec_pric
     , t.cash_free_money                                         as dct_amt
     , t.gift_free_money                                         as gift_dct_amt
     , t.other_free_money                                        as other_free_amt
     , t1.perform_profit_margin                                  as amt1
     , t1.account_price_gross_money                              as amt2
     , t1.account_money                                          as sale_amt
     , t1.quantity                                               as sale_qty
     , t.pair_count                                              as china_med_qty
     , current_timestamp                                         as etl_time
     , t2.is_unfree_goods                                        as is_n_surd_prof
     , t1.cost_price                                             as cost_pric
     , t1.cost_price * t1.quantity                               as cost_amt
     --, t1.cost_price * t1.quantity                               as sale_cost_amt
     , case when t.bill_kind in ('3', '4', '15') then 'N' else 'Y' end as is_effe_ordr
     , coalesce(t.order_code, t.sale_order_code)                 as order_code
     , 'N'                                                       as is_ecp_self_dstn_ordr
     , date_format(cast(t1.sale_time as timestamp), 'yyyyMMdd')        as stat_date
     , t1.item_type                                              as proj_cate_code
     , t2.purchase_kind_code                                     as purchase_kind_code
     , t2.proc_time                                              as sale_goods_snapshot_proc_time
     , t.casher_code                                             as casr_code
     , t1.service_charge                                         as service_charge
     ,case when coalesce(t3.sales_job_number , '') = '' then 'NA' else LPAD(CAST(t3.sales_job_number  as string), 8, '0') end as sale_pers_id
     ,case when t4.from_warehouse_code is not null then t4.from_warehouse_code
           when coalesce(trim(t1.store_code),'')='' then 'NA'
           else trim(t1.store_code) end                               as phmc_code
     ,case when t4.warehouse_code is not null then t4.warehouse_code
           when coalesce(trim(t1.store_code),'')='' then 'NA'
           else trim(t1.store_code) end                               as out_phmc_code
     ,if(t4.pos_code is not null, 'Y', 'N')                      as is_ydch_flag
     ,t.coupon_plan_code                                         as coup_prog_code
from ods_order_sale_order_r t
         inner join ods_order_sale_order_details_r t1
                    on t.sale_order_code = t1.sale_order_code
                        and date_format(cast(t1.sale_time as timestamp), 'yyyyMMdd')=date_format(localtimestamp,'yyyyMMdd')
         left join ods_order_sale_goods_snapshot_r t2
                   on t1.sale_order_code = t2.sale_order_code
                       and t1.good_code = t2.good_code
                       and t2.origin_table='sale_goods_snapshot'
                       and date_format(cast(t2.sale_time as timestamp), 'yyyyMMdd')=date_format(localtimestamp,'yyyyMMdd')
         left join ods_order_sales_ordersalesman_r t3
                   on t1.sale_order_code = t3.sale_order_code
                       and t1.row_num = t3.row_num
                       and date_format(cast(t3.sale_time as timestamp), 'yyyyMMdd')=date_format(localtimestamp,'yyyyMMdd')
         left join ods_zt_ord_order_r t4
                   on t.sale_order_code = t4.pos_code
                       and t4.order_type =1
where date_format(cast(t.sale_time as timestamp), 'yyyyMMdd')=date_format(localtimestamp,'yyyyMMdd');
