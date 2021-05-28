import { Button, Result } from 'antd';
import React from 'react';
import { history } from 'umi';

const BuildPage: React.FC = () => (
  <Result
    status="404"
    title="Building"
    subTitle="Sorry, the page you visited is building."
    extra={
      <Button type="primary" onClick={() => history.push('/')}>
        Back Home
      </Button>
    }
  />
);

export default BuildPage;
