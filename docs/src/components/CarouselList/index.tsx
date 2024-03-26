import Carousel, {CarouselInfo, CarouselItem} from "../Carousel";
import React from "react";




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

type CarouselItemProps = {
  items: CarouselItemInfo[];
}

const CarouselList = (props: { items: CarouselItemInfo[]; }) => {

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

