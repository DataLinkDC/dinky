import React, {useEffect, useState} from "react";
import style from "./carousel.module.css";
import {CarouselItemInfo} from "../CarouselList";

/**
 * @param {children} children ReactNode
 * @param {width} width 宽度
 * @param {height} height 高度
 * @param {styles} styles 样式
 * @returns 轮播图 单项
 */
type CarouselItemProps = {
    children: React.ReactNode;
    width?: string | number;
    height?: string | number;
    styles?: React.CSSProperties;
}
export const CarouselItem = (props: CarouselItemProps) => {
    const {children, width = "100%", height = "100%", styles = {}} = props;
    return (
        <div
            className={style.carousel_item}
            style={{width: width, height: height, ...styles}}
        >
            {children}
        </div>
    );
};

/**
 * @returns 轮播图 主体
 * @param props
 */
export const CarouselInfo: React.FC<CarouselItemInfo> = (props: { item: CarouselItemInfo; }) => {
    const {item} = props;
    return (
        <div className="carousel_info_container">
            {/*<div className="carousel_info_info">*/}
            {/*    <h1>{item.title}</h1>*/}
            {/*    <span>{item.describe}</span>*/}
            {/*</div>*/}
            <div className="carousel_info_image_container">
                <img src={item.image} alt="Jay" className="carousel_info_image"/>
            </div>
        </div>
    );
};

/**
 * @param {children} children ReactNode
 * @param {switchingTime} switchingTime 间隔时间 默认3秒 以毫秒为单位 3000ms = 3s
 * @returns 轮播图 容器
 */
const Carousel = ({
                      children = React.createElement("div"),
                      switchingTime = 3000,
                  }) => {
    const time = ((switchingTime % 60000) / 1000).toFixed(0); // 将毫秒转换为秒
    const [activeIndex, setActiveIndex] = useState(0); // 对应索引

    /**
     * 更新索引
     * @param {newIndex} newIndex 更新索引
     */
    const onUpdateIndex = (newIndex: React.SetStateAction<number>) => {
        if (newIndex < 0) {
            newIndex = React.Children.count(children) - 1;
        } else if (newIndex >= React.Children.count(children)) {
            newIndex = 0;
        }
        setActiveIndex(newIndex);
        replayAnimations();
    };

    /**
     * 重置动画
     */
    const replayAnimations = () => {
        document.getAnimations().forEach((anim) => {
            anim.cancel();
            anim.play();
        });
    };

    /**
     * 底部加载条点击事件
     * @param {index} index 跳转索引
     */
    const onClickCarouselIndex = (index: React.SetStateAction<number>) => {
        onUpdateIndex(index);
        replayAnimations();
    };

    useEffect(() => {
        const interval = setInterval(() => {
            onUpdateIndex(activeIndex + 1);
        }, switchingTime);

        return () => {
            if (interval) {
                clearInterval(interval);
            }
        };
    });

    /**
     * Renders the children elements with specific styles and transformations.
     *
     * @return {JSX.Element} The rendered JSX elements with modified styles.
     */
    const renderChildren = () => {
        return (
            <div
                className={style.inner}
                style={{transform: `translateX(-${activeIndex * 100}%)`}}
            >
                {React.Children.map(children, (child) => {
                    return React.cloneElement(child, {width: "100%", height: "100%"});
                })}
            </div>
        )
    }

    /**
     * Function to render loading indicators for each child component.
     *
     * @return {JSX.Element} The loading indicators JSX element
     */
    const renderLoading = () => {
        return (
            <div className={style.loading}>
                {React.Children.map(children, (child, index) => {
                    return (
                        <div
                            className={style.indicator_outer}
                            onClick={() => onClickCarouselIndex(index)}
                        >
                            <div
                                className={style.indicator_inside}
                                style={{
                                    animationDuration: index === activeIndex ? `${time}s` : "0s",
                                    backgroundColor: index === activeIndex ? "#041d81" : null,
                                }}
                            />
                        </div>
                    );
                })}
            </div>
        )
    }

    return (
        <div className={style.container}>
            {renderChildren()}
            {renderLoading()}
        </div>
    );
};

export default Carousel;

