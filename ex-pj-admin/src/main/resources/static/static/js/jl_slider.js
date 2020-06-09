/**
 * Created by cfs on 2017/4/24.
 */
//;(function(){
    "use strict";
    //slider('.pic>div','.circle>span','.prev','.next');
    //slider('.picSmall li','.circleSmall>span','.prevSmall','.nextSmall');
    function slider(pic,cir,pre,nex){
        var index = 0;
        var img=$(pic);
        var span=$(cir);

        //初始化
        img.eq(0).siblings().css({'opacity':0});
        //图片的自动轮播函数
        var timer = setInterval(slider, 4000);
        function slider(){
            //初始化opacity为0
            img.stop().animate({'opacity': 0}, 1000);
            span.animate({'width':10},500);

            index = ++index == img.length ? 0 : index;
            //为当前索引设置opacity为 1
            img.eq(index).stop().animate({'opacity': 1}, 1000);
            span.eq(index).stop().animate({'width':40},500);
        }

        //小点点击事件
        span.click(function () {
            //先清除图片的自动轮播函数
            clearInterval(timer);
            //为当前span节点移除span-active，隐藏其他banner图片
            $(this).siblings().animate({'width': 10}, 500);
            index = $(this).index();
            img.stop().animate({'opacity': 0}, 800);
            //为当前span节点添加span-active，显示当前索引的图片
            $(this).stop().animate({'width': 40}, 500);
            img.eq(index).stop().animate({'opacity': 1}, 800);
            timer = setInterval(slider, 4000);
        });

        //箭头调用函数
        function prevNext(x){
            //先清除图片的自动轮播函数
            clearInterval(timer);
            img.stop().animate({'opacity': 0}, 800);
            span.eq(x).siblings().animate({'width': 10}, 500);
            span.eq(x).animate({'width': 40}, 500);
            img.eq(x).stop().animate({'opacity': 1}, 800);
            timer = setInterval(slider, 4000);
        }
        //箭头点击事件
        var prev=$(pre);
        prev.click(function(){
            if(index == 0){
                index=img.length;
            }
            index -=1;
            prevNext(index);
        });

        var next=$(nex);
        next.click(function(){
            index +=1;
            if(index == img.length){
                index=0;
            }
            prevNext(index);
        })

    }
//})();