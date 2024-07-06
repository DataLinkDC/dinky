import { Button, Card, Space, Typography } from 'antd';
import {
  AlertInstanceIcon,
  ClusterConfigIcon,
  ClusterInstanceIcon,
  DatabaseIcon
} from '@/components/Icons/HomeIcon';
import React from 'react';
import { AllJobIcons } from '@/components/Icons/DevopsIcons';
import { l } from '@/utils/intl';

const FastLink = () => {
  const imgStyle = {
    display: 'block',
    width: 20,
    height: 20
  };

  const links = [
    {
      title: l('menu.datastudio'),
      href: '',
      icon: <ClusterInstanceIcon style={imgStyle} />
    },
    {
      title: l('menu.registration.alert.instance'),
      href: '',
      icon: <AlertInstanceIcon style={imgStyle} />
    },
    {
      title: l('menu.registration.datasource'),
      href: '',
      icon: <DatabaseIcon style={imgStyle} />
    },
    {
      title: l('menu.registration.cluster.cluster-config'),
      href: '',
      icon: <ClusterInstanceIcon style={imgStyle} />
    },
    {
      title: l('menu.registration.resource'),
      href: '',
      icon: <ClusterConfigIcon style={imgStyle} />
    }
  ];

  return (
    <Space size={[12, 8]} wrap>
      {links.map((link) => {
        return (
          <Button key={link.title} type='text' ghost size={'large'} icon={link.icon}>
            {link.title}
          </Button>
        );
      })}
    </Space>
  );
};
export default FastLink;
