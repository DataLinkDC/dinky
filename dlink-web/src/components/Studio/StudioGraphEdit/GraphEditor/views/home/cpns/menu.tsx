import { Menu } from '@antv/x6-react-components';
import { FC, memo, useState } from 'react';
import { message } from 'antd';
import { DataUri, Graph } from '@antv/x6';
import '@antv/x6-react-components/es/menu/style/index.css';
import {
  CopyOutlined,
  SnippetsOutlined,
  RedoOutlined,
  UndoOutlined,
  ScissorOutlined,
  DeleteOutlined,
  ExportOutlined,
} from '@ant-design/icons';

type MenuPropsType = {
  top: number;
  left: number;
  graph: Graph | undefined;
};

export const CustomMenu: FC<MenuPropsType> = memo(({ top = 0, left = 0, graph }) => {
  const MenuItem = Menu.Item;
  const SubMenu = Menu.SubMenu;
  const Divider = Menu.Divider;
  const [messageApi, contextHolder] = message.useMessage();
  const [isDisablePaste, setIsDisablePaste] = useState(true);

  const copy = () => {
    const cells = graph!.getSelectedCells();
    if (cells.length) {
      graph!.copy(cells);
    }
    return false;
  };

  const cut = () => {
    const cells = graph!.getSelectedCells();
    if (cells.length) {
      graph!.cut(cells);
    }
    return false;
  };

  const paste = () => {
    if (!graph!.isClipboardEmpty()) {
      const cells = graph!.paste({ offset: 32 });
      graph!.cleanSelection();
      graph!.select(cells);
    }
    return false;
  };
  const onMenuClick = (name: string) => {
    messageApi.info(`clicked ${name}`);
    if (graph) {
      switch (name) {
        case 'undo':
          graph.undo();
          break;
        case 'redo':
          graph.redo();
          break;
        case 'delete':
          graph.clearCells();
          break;
        case 'save-PNG':
          graph.toPNG(
            (dataUri: string) => {
              DataUri.downloadDataUri(dataUri, 'chartx.png');
            },
            {
              backgroundColor: 'white',
              padding: {
                top: 20,
                right: 30,
                bottom: 40,
                left: 50,
              },
              quality: 1,
            },
          );
          break;
        case 'save-SVG':
          graph.toSVG((dataUri: string) => {
            // 下载
            DataUri.downloadDataUri(DataUri.svgToDataUrl(dataUri), 'chart.svg');
          });
          break;
        case 'save-JPEG':
          graph.toJPEG((dataUri: string) => {
            // 下载
            DataUri.downloadDataUri(dataUri, 'chart.jpeg');
          });
          break;
        case 'print':
          // graph.printPreview();
          break;
        case 'copy':
          copy();
          setIsDisablePaste(false);
          break;
        case 'cut':
          cut();
          setIsDisablePaste(false);
          break;
        case 'paste':
          paste();
          setIsDisablePaste(true);
          break;
        case 'save-JSON':
          let data = JSON.stringify(graph.toJSON(), null, 4);
          const blob = new Blob([data], { type: 'text/json' }),
            e = new MouseEvent('click'),
            a = document.createElement('a');

          a.download = '流程数据';
          a.href = window.URL.createObjectURL(blob);
          a.dataset.downloadurl = ['text/json', a.download, a.href].join(':');
          a.dispatchEvent(e);

          // graph.fromJSON({cells:[graph.toJSON().cells[0],graph.toJSON().cells[1]]})
          break;
        default:
          break;
      }
    }
  };
  const onMenuItemClick = () => {};

  const styleObj: any = {
    position: 'absolute',
    top: `${top}px`, // 将top属性设置为state变量的值
    left: `${left}px`,
    width: '100px',
    height: '105px',
    zIndex: 9999,
  };
  return (
    <div style={styleObj}>
      {contextHolder}
      <Menu hasIcon={true} onClick={onMenuClick}>
        <MenuItem
          onClick={onMenuItemClick}
          name="undo"
          icon={<UndoOutlined />}
          hotkey="Cmd+Z"
          text="Undo"
        />
        <MenuItem name="redo" icon={<RedoOutlined />} hotkey="Cmd+Shift+Z" text="Redo" />
        <SubMenu text="Export" icon={<ExportOutlined />}>
          <MenuItem name="save-PNG" text="Save As PNG" />
          <MenuItem name="save-SVG" text="Save As SVG" />
          <MenuItem name="save-JPEG" text="Save As JPEG" />
          <MenuItem name="save-JSON" text="Save As JSON" />
        </SubMenu>
        <Divider />
        <MenuItem icon={<ScissorOutlined />} name="cut" hotkey="Cmd+X" text="Cut" />
        <MenuItem icon={<CopyOutlined />} name="copy" hotkey="Cmd+C" text="Copy" />
        <MenuItem
          name="paste"
          icon={<SnippetsOutlined />}
          hotkey="Cmd+V"
          disabled={isDisablePaste}
          text="Paste"
        />
        <MenuItem name="delete" icon={<DeleteOutlined />} hotkey="Delete" text="Delete" />
      </Menu>
    </div>
  );
});
