import React from 'react';
import LogicFlow from '@logicflow/core';

type IProps = {
    lf: LogicFlow
    onUploadJson: Function
}

function download(filename: string, text: string) {
    var element = document.createElement('a');
    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
    element.setAttribute('download', filename);

    element.style.display = 'none';
    document.body.appendChild(element);

    element.click();

    document.body.removeChild(element);
}

type FileEventTarget = EventTarget & { files: FileList };

export default function GraphTo(props: IProps) {
    const {lf, onUploadJson} = props;

    function downloadXml() {
        const data = lf!.getGraphData() as string;
        download('logic-flow.xml', data);
    }

    function uploadFlinkJson(ev: React.ChangeEvent<HTMLInputElement>) {
        console.info("uploadFlinkJson")
        const file = (ev.target as FileEventTarget).files[0];
        const reader = new FileReader()
        reader.onload = (event: ProgressEvent<FileReader>) => {
            if (event.target) {
                const json = event.target.result as string;
                // lf.render(json);
                onUploadJson(json);
            }
        }
        reader.readAsText(file); // you could also read images and other binaries
    }

    function downloadImage() {
        const {lf} = props;
        lf.getSnapshot();
    }

    return (
        <div className="graph-io">
      <span
          title="下载 JSON"
          onMouseDown={() => downloadXml()}
      >
        <img src={"img/download.png"} alt="下载JSON"/>
      </span>
            <span
                id="download-img"
                title="下载图片"
                onMouseDown={() => downloadImage()}
            >
        <img src={"img/img.png"} alt="下载图片"/>
      </span>
            <span
                id="upload-json"
                title="上传 JSON"
            >
        <input type="file" className="upload" onChange={(ev) => uploadFlinkJson(ev)}/>
        <img src={"img/upload.png"} alt="上传JSON"/>
      </span>
        </div>
    );
}
