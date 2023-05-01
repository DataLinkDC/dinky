import {Form, Modal} from "antd";
import {NORMAL_MODAL_OPTIONS} from "@/services/constants";
import {l} from "@/utils/intl";
import React, {useEffect} from "react";
import {UserBaseInfo} from "@/types/User/data";
import {FormContextValue} from "@/components/Context/FormContext";
import UserForm from "@/pages/AuthCenter/User/components/UserModalForm/UserForm";

type UserModalFormProps = {
    onCancel: (flag?: boolean) => void;
    onSubmit: (values: Partial<UserBaseInfo.User>) => void;
    modalVisible: boolean;
    values: Partial<UserBaseInfo.User>;
}

const UserModalForm: React.FC<UserModalFormProps> = (props) => {

    /**
     * init form
     */
    const [form] = Form.useForm();
    /**
     * init form context
     */
    const formContext = React.useMemo<FormContextValue>(() => ({
        resetForm: () => form.resetFields(), // 定义 resetForm 方法
    }), [form]);

    /**
     * init props
     */
    const {
        onSubmit: handleSubmit,
        onCancel: handleModalVisible,
        modalVisible,
        values
    } = props;


    /**
     * when modalVisible or values changed, set form values
     */
    useEffect(() => {
        form.setFieldsValue(values);
    }, [modalVisible, values, form]);


    /**
     * handle cancel
     */
    const handleCancel = () => {
        handleModalVisible();
        formContext.resetForm();
    }

    /**
     * submit form
     */
    const submitForm = async () => {
        const fieldsValue = await form.validateFields();
        await handleSubmit({...values, ...fieldsValue});
        await handleCancel();
    };




    return <Modal
        {...NORMAL_MODAL_OPTIONS}
        title={values.id ? l("user.update") : l("user.create")}
        open={modalVisible}
        onCancel={() => handleCancel()}
        onOk={() => submitForm()}
    >
        <UserForm values={values} form={form}/>
    </Modal>
}

export default UserModalForm;