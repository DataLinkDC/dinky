import { FormContextValue } from '@/components/Context/FormContext';
import { NORMAL_MODAL_OPTIONS, SWITCH_OPTIONS } from '@/services/constants';
import { l } from '@/utils/intl';
import {
  ModalForm,
  ProFormCheckbox,
  ProFormDigit,
  ProFormGroup,
  ProFormSelect,
  ProFormSwitch,
  ProFormText,
  ProFormTextArea
} from '@ant-design/pro-components';

import {
  PriorityList,
  TimeoutNotifyStrategy
} from '@/pages/DataStudio/HeaderContainer/PushDolphin/constants';
import { transformPushDolphinParams } from '@/pages/DataStudio/HeaderContainer/PushDolphin/function';
import { TaskDataType } from '@/pages/DataStudio/model';
import {
  DolphinTaskDefinition,
  DolphinTaskMinInfo,
  DolphinTaskGroupInfo,
  PushDolphinParams
} from '@/types/Studio/data.d';
import { InitPushDolphinParams } from '@/types/Studio/init.d';
import { Button, Form, Tag } from 'antd';
import { DefaultOptionType } from 'antd/es/select';
import React from 'react';

type PushDolphinProps = {
  onCancel: () => void;
  dolphinTaskList: DolphinTaskMinInfo[];
  dolphinTaskGroup:DolphinTaskGroupInfo[],
  dolphinDefinitionTask: Partial<DolphinTaskDefinition>;
  modalVisible: boolean;
  currentDinkyTaskValue: Partial<TaskDataType>;
  loading: boolean;
  onSubmit: (values: DolphinTaskDefinition) => void;
};

export const PushDolphin: React.FC<PushDolphinProps> = (props) => {
  const {
    onCancel,
    onSubmit,
    modalVisible,
    dolphinTaskList,
    dolphinDefinitionTask,
    dolphinTaskGroup,
    currentDinkyTaskValue,
    loading
  } = props;
 console.log('我获取到了,0',dolphinTaskList,dolphinTaskGroup);
 
  const [formValues, setFormValues] = React.useState<PushDolphinParams>(
    transformPushDolphinParams(
      dolphinDefinitionTask as DolphinTaskDefinition,
      { ...InitPushDolphinParams, taskId: currentDinkyTaskValue?.id ?? '' },
      true
    ) as PushDolphinParams
  );

  /**
   * init form
   */
  const [form] = Form.useForm<PushDolphinParams>();

  /**
   * init form context
   */
  const formContext = React.useMemo<FormContextValue>(
    () => ({
      resetForm: () => form.resetFields() // 定义 resetForm 方法
    }),
    [form]
  );

  /**
   * cancel choose
   */
  const handleCancel = () => {
    onCancel();
    formContext.resetForm();
  };

  const handlePushDolphinSubmit = async () => {
    const values = form.validateFields();
    if (!values) {
      return;
    }
    const transformPushDolphinParamsValue: DolphinTaskDefinition = transformPushDolphinParams(
      dolphinDefinitionTask as DolphinTaskDefinition,
      formValues,
      false
    ) as DolphinTaskDefinition;
    onSubmit(transformPushDolphinParamsValue);
    handleCancel();
  };

  const renderFooter = () => {
    return [
      <Button key={'pushCancel'} onClick={handleCancel}>
        {l('button.cancel')}
      </Button>,
      <Button
        key={'push'}
        type='primary'
        htmlType={'submit'}
        loading={loading}
        onClick={() => handlePushDolphinSubmit()}
      >
        {l('button.finish')}
      </Button>
    ];
  };

  const buildUpstreamTaskOptions = (
    data: DolphinTaskMinInfo[] | undefined
  ): DefaultOptionType[] => {
    if (data && data.length > 0) {
      return data.map((item) => {
        const label = (
          <>
            <Tag color={'purple'}>
              {l('datastudio.header.pushdolphin.taskName', '', { name: item.taskName })}
            </Tag>
            <span style={{ color: '#8a8a8a' }}>
              {l('datastudio.header.pushdolphin.taskNameExt', '', {
                type: item.taskType,
                processDefinitionName: item.processDefinitionName
              })}
            </span>
          </>
        );
        return {
          label: label,
          value: item.taskCode.toString(),
          key: item.taskCode
        };
      });
    }
    return [];
  };

  const handleValueChange = (changedValues: any, allValues: any) => {
    if (allValues) {
      setFormValues({ ...formValues, ...allValues });
    }
  };

  const pushDolphinForm = () => {
    return (
      <>
        <ProFormText name={'taskId'} label={l('datastudio.header.pushdolphin.taskId')} disabled />
        <ProFormSelect
          label={l('datastudio.header.pushdolphin.upstreamCodes')}
          name={'upstreamCodes'}
          showSearch
          mode={'multiple'}
          extra={l('datastudio.header.pushdolphin.upstreamCodesTip')}
          options={buildUpstreamTaskOptions(dolphinTaskList)}
        />

        <ProFormGroup>
          <ProFormSelect
            label={l('datastudio.header.pushdolphin.taskPriority')}
            name={'taskPriority'}
            width={'sm'}
            options={PriorityList}
          />
          <ProFormSelect
            label={l('datastudio.header.pushdolphin.taskGroup')}
            name={'taskGroupVVVV'}
            width={'sm'}
            options={dolphinTaskGroup}
          />
          <ProFormDigit
            label={l('datastudio.header.pushdolphin.failRetryTimes')}
            name={'failRetryTimes'}
            initialValue={formValues.failRetryTimes}
            width={'sm'}
            min={0}
            max={99}
            fieldProps={{
              precision: 0
            }}
          />

          <ProFormDigit
            label={l('datastudio.header.pushdolphin.failRetryInterval')}
            name={'failRetryInterval'}
            width={'sm'}
            rules={[
              {
                required: true,
                message: l('datastudio.header.pushdolphin.failRetryIntervalPlaceholder')
              }
            ]}
            min={0}
            fieldProps={{
              precision: 0
            }}
          />

          <ProFormDigit
            label={l('datastudio.header.pushdolphin.delayTime')}
            name={'delayTime'}
            width={'sm'}
            rules={[
              { required: true, message: l('datastudio.header.pushdolphin.delayTimePlaceholder') }
            ]}
            min={0}
            fieldProps={{
              precision: 0
            }}
          />

          <ProFormSwitch
            label={l('datastudio.header.pushdolphin.timeoutFlag')}
            rules={[{ required: true, message: l('datastudio.header.pushdolphin.timeoutFlagTip') }]}
            {...SWITCH_OPTIONS()}
            name={'timeoutFlag'}
          />

          <ProFormSwitch
            label={l('datastudio.header.pushdolphin.flag')}
            rules={[{ required: true, message: l('datastudio.header.pushdolphin.flagTip') }]}
            {...SWITCH_OPTIONS()}
            name={'flag'}
          />

          <ProFormSwitch
            label={l('datastudio.header.pushdolphin.isCache')}
            rules={[{ required: true, message: l('datastudio.header.pushdolphin.isCacheTip') }]}
            {...SWITCH_OPTIONS()}
            name={'isCache'}
          />
        </ProFormGroup>
        {/*如果是失败告警，则需要设置告警策略*/}
        {formValues.timeoutFlag && (
          <>
            <ProFormGroup>
              <ProFormCheckbox.Group
                label={l('datastudio.header.pushdolphin.timeoutNotifyStrategy')}
                name={'timeoutNotifyStrategy'}
                rules={[
                  {
                    required: true,
                    message: l('datastudio.header.pushdolphin.timeoutNotifyStrategyTip')
                  }
                ]}
                width={'sm'}
                options={TimeoutNotifyStrategy}
              />
              <ProFormDigit
                label={l('datastudio.header.pushdolphin.timeout')}
                name={'timeout'}
                width={'sm'}
                rules={[
                  { required: true, message: l('datastudio.header.pushdolphin.timeoutPlaceholder') }
                ]}
                min={0}
                max={30}
                fieldProps={{
                  precision: 0
                }}
              />
            </ProFormGroup>
          </>
        )}

        <ProFormTextArea label={l('global.table.note')} name={'description'} />
      </>
    );
  };

  return (
    <ModalForm<PushDolphinParams>
      {...NORMAL_MODAL_OPTIONS}
      title={l('datastudio.header.pushdolphin.title', '', {
        name: currentDinkyTaskValue?.name ?? ''
      })}
      open={modalVisible}
      form={form}
      initialValues={formValues}
      modalProps={{
        onCancel: handleCancel,
        destroyOnClose: true
      }}
      submitter={{ render: () => [...renderFooter()] }}
      onValuesChange={handleValueChange}
      loading={loading}
    >
      {pushDolphinForm()}
    </ModalForm>
  );
};

export default PushDolphin;
