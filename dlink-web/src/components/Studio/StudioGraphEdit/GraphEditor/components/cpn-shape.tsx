import { memo } from "react";
import CustomSvg, {SvgType} from "./custom-svg";

export type NodeType = {
    node?: any;
};

const CpnShape = memo<SvgType>(({ iconPath, styleObj }) => {
  return (
      <CustomSvg iconPath={iconPath} styleObj={styleObj} />
  );
});

export default CpnShape;
