import React from "react";

export type ContentScrollProps = {
  height:number|string
  children?: React.ReactNode;

}

const ContentScroll: React.FC<ContentScrollProps>  = (props) => {
  const {height,children}=props
  return (
    <div className="content-scroll" style={{height:height,display:height<1?"none":"block"}}>
      {children}
    </div>
  )
}
export default ContentScroll;
