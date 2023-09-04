// import {EditTwoTone, PlusOutlined} from '@ant-design/icons';
// import {Button, Card, List, Modal, Typography} from 'antd';
// import {PageContainer} from '@ant-design/pro-layout';
// import styles from './style.less';
// import {useRequest} from "@@/exports";
// import {API_CONSTANTS} from "@/services/endpoints";
// import {Alert,} from "@/types/RegCenter/data";
// import {l} from "@/utils/intl";
// import {DangerDeleteIcon} from "@/components/Icons/CustomIcons";
// import React, {useState} from "react";
// import {AlertTemplateState} from "@/types/RegCenter/state";
// import { InitAlertTemplateState }  from '@/types/RegCenter/init.d';
// import AlertTemplateForm from "@/pages/RegCenter/Alert/AlertTemplate/components/AlertTemplateForm";
// import {handleAddOrUpdate, handleRemoveById} from "@/services/BusinessCrud";
// const {Paragraph} = Typography;
//
// export default () => {
//
//   const [alertTemplateState, setAlertTemplateState] = useState<AlertTemplateState>(InitAlertTemplateState);
//
//   const {data, loading,run} = useRequest({url: API_CONSTANTS.ALERT_TEMPLATE,});
//   const nullData: Partial<Alert.AlertTemplate> = {};
//
//
//   /**
//    * edit click callback
//    * @param item
//    */
//   const editClick = (item: Alert.AlertTemplate) => {
//     setAlertTemplateState((prevState) => ({
//       ...prevState,
//       editOpen: !prevState.editOpen,
//       value: item
//     }));
//   };
//
//   /**
//    * handle delete alert template
//    * @param id
//    */
//   const handleDeleteSubmit = async (id: number) => {
//     Modal.confirm({
//       title: l('rc.template.delete'),
//       content: l('rc.template.deleteConfirm'),
//       okText: l('button.confirm'),
//       cancelText: l('button.cancel'),
//       onOk: async () =>{
//         await handleRemoveById(API_CONSTANTS.ALERT_TEMPLATE, id);
//         run();
//       }
//     });
//   };
//
//   /**
//    * cancel callback
//    */
//   const handleCleanState = () => {
//     setAlertTemplateState((prevState) => ({
//       ...prevState,
//       value: {},
//       addedOpen: false,
//       editOpen: false
//     }));
//   };
//
//   /**
//    * handle add alert instance
//    */
//   const handleSubmit = async (value: Alert.AlertTemplate) => {
//     await handleAddOrUpdate(
//       API_CONSTANTS.ALERT_TEMPLATE,
//       value,
//       () => {},
//       () => handleCleanState()
//     )
//     run();
//   };
//
//
//   const renderAlertTemplateActionButton = (item: Alert.AlertTemplate) => {
//     return [
//       <Button
//         className={'options-button'}
//         key={'AlertGroupEdit'}
//         icon={<EditTwoTone/>}
//         title={l('button.edit')}
//         onClick={() => editClick(item)}
//       />,
//       <Button
//         className={'options-button'}
//         key={'DeleteAlertGroupIcon'}
//         icon={<DangerDeleteIcon/>}
//         onClick={() => handleDeleteSubmit(item.id)}
//       />
//     ];
//   };
//
//
//   return (
//     <PageContainer>
//       <div className={styles.cardList}>
//         <List<Alert.AlertTemplate>
//           rowKey="id"
//           loading={loading}
//           grid={{gutter: 16, xs: 1, sm: 2, md: 3, lg: 3, xl: 4, xxl: 4,}}
//           dataSource={[nullData, ...data ?? []]}
//           renderItem={(item) => {
//             if (item && item.id) {
//               return (
//                 <List.Item key={item.id}>
//                   <Card
//                     hoverable
//                     className={styles.card}
//                     actions={renderAlertTemplateActionButton(item)}
//                   >
//                     <Card.Meta
//                       title={<a>{item.name}</a>}
//                       description={
//                         <Paragraph className={styles.item} ellipsis={{rows: 3}}>
//                           {item.templateContent}
//                         </Paragraph>
//                       }
//                     />
//                   </Card>
//                 </List.Item>
//               );
//             }
//             return (
//               <List.Item>
//                 <Button type="dashed" className={styles.newButton}
//                         onClick={() => setAlertTemplateState((prevState) => ({ ...prevState, addedOpen: true }))}
//                 >
//                   <PlusOutlined/> 新增模板
//                 </Button>
//               </List.Item>
//             );
//           }}
//         />
//       </div>
//
//
//       <AlertTemplateForm
//         onSubmit={handleSubmit}
//         onCancel={handleCleanState}
//         modalVisible={alertTemplateState.addedOpen}
//         values={{}}
//       />
//       {alertTemplateState.value && Object.keys(alertTemplateState.value).length > 0 && (
//         <AlertTemplateForm
//           onSubmit={handleSubmit}
//           onCancel={handleCleanState}
//           modalVisible={alertTemplateState.editOpen}
//           values={alertTemplateState.value}
//         />
//       )}
//
//     </PageContainer>
//   );
// };
//
