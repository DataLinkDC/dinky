import {UserBaseInfo} from "@/types/User/data";
import {FormInstance} from "antd/es/form/hooks/useForm";
import {Values} from "async-validator";
import React from "react";
import {FORM_LAYOUT_PUBLIC} from "@/services/constants";
import {ProForm, ProFormText} from "@ant-design/pro-components";
import {l} from "@/utils/intl";


type UserFormProps = {
    values: Partial<UserBaseInfo.Role>;
    form: FormInstance<Values>;
};
 const UserForm: React.FC<UserFormProps> = (props) => {


    const {values, form} = props;

    /**
     * user form render
     * @returns {JSX.Element}
     */
    const userFormRender = () => {
        return (
            <>
                <ProFormText
                    name="username"
                    label={l("user.username")}
                    placeholder={l("user.usernamePlaceholder")}
                    rules={[{
                        required: true,
                        message: l("user.usernamePlaceholder")
                    }]}
                />

                <ProFormText
                    name="nickname"
                    label={l("user.username")}
                    placeholder={l("user.nicknamePlaceholder")}
                    rules={[{
                        required: true,
                        message: l("user.nicknamePlaceholder")
                    }]}
                />

                <ProFormText
                    name="worknum"
                    label={l("user.jobnumber")}
                    placeholder={l("user.jobnumberPlaceholder")}
                />

                <ProFormText
                    name="mobile"
                    label={l("user.phone")}
                    placeholder={l("user.phonePlaceholder")}
                />
            </>
        );
    };

    return <>
        <ProForm
            {...FORM_LAYOUT_PUBLIC}
            form={form}
            initialValues={values}
            layout={"horizontal"}
            submitter={false}
        >
            {userFormRender()}
        </ProForm>
    </>

}

export default UserForm;