import Carousel, {CarouselInfo, CarouselItem} from "../Carousel";
import React from "react";


/**
 * @param {id} id 轮播图id
 * @param {title} title 标题
 * @param {describe} describe 描述
 * @param {image} image 图片
 * @param {imageWidth} imageWidth 图片宽度
 * @param {imageHeight} imageHeight 图片高度
 * @param {backgroundColor} backgroundColor 背景颜色
 * @param {jumpUrl} jumpUrl 跳转链接
 * @returns 轮播图信息
 * @constructor
 */
export interface CarouselItemInfo {
    id: number;
    title?: string;
    describe?: string;
    image: string | React.ReactNode;
    imageWidth?: string | number;
    imageHeight?: string | number;
    backgroundColor?: string;
    jumpUrl?: string;
}

/**
 * @param {items} items 轮播图列表
 */
type CarouselItemProps = {
  items: CarouselItemInfo[];
}

/**
 * @returns 轮播图列表
 * @param props
 * @constructor
 */
const CarouselList:React.FC<CarouselItemProps> = (props: { items: CarouselItemInfo[]; }) => {

    const {items} = props

    return (
        <Carousel>
            {items?.map((item :CarouselItemInfo)=> {
                return (
                    <CarouselItem
                        key={item.id}
                        width={item.imageWidth}
                        height={item.imageHeight}
                        // styles={{ backgroundColor: item.backgroundColor }}
                    >
                        <CarouselInfo item={item}/>
                    </CarouselItem>
                );
            })}
        </Carousel>
    );
};

export default CarouselList;

