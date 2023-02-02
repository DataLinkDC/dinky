import { l } from '@/utils/intl';
import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import React from 'react';

const Footer: React.FC = () => {
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      copyright={`${currentYear} `+ l('app.copyright.produced')}
      links={[
        {
          key: 'Dinky',
          title: 'Dinky',
          href: 'https://github.com/DataLinkDC/dlink',
          blankTarget: true,
        },
        {
          key: 'github',
          title: <GithubOutlined/>,
          href: 'https://github.com/DataLinkDC/dlink',
          blankTarget: true,
        },
      ]}
    />
  );
};

export default Footer;
