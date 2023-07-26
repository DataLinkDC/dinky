// style={{color:token.colorText}}


import { l } from '@/utils/intl';
import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import React from 'react';
import {children} from "@umijs/utils/compiled/cheerio/lib/api/traversing";
import {theme} from "antd";

export type TitleProps = {
  children?: React.ReactNode;
}
const { useToken } = theme;

const Title: React.FC<TitleProps> = (props) => {
  const {token} = useToken();
  return (
    <span className={"title"} style={{color:token.colorText}}>
      {props.children}
    </span>
  );
};

export default Title;
