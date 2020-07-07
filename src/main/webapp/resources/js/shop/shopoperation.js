//@ sourceURL=shopoperation.js
/**
 * js代码分成两个功能，
 * 第一个从后台获取到店铺分类，以及区域等信息，将其填充到前台html控件里面去。
 * 第二步是将表单的信息获取到，将其转发到后台去注册店铺。
 */

//这个$表示执行里面的函数，是在JQuery中定义的，所以用之前要引入Jquery。
$(function () {
    console.log("1111111111");
    //根据url能否获得
    var shopId = getQueryString('shopId');
    var isEdit = shopId ? true : false;


    var initUrl = '/o2o/shopadmin/getshopinitinfo';

    var registerShopUrl = '/o2o/shopadmin/registershop';

    var shopInfoUrl = "/o2o/shopadmin/getshopbyid?shopId=" + shopId;

    var editShopUrl = '/o2o/shopadmin/modifyshop';


    //根据是否能获得shopId来决定网页用来做店铺注册还是做店铺信息修改
    if (!isEdit)
    {
        //获取区域列表以及店铺类别列表
        getShopInitInfo();
    }
    else
    {
        getShopInfo(shopId);
    }
    
    function getShopInfo(shopId)
    {
        $.getJSON(shopInfoUrl,function(data){
            if(data.success)
            {
                var shop = data.shop;
                $('#shop-name').val(shop.shopName);
                $('#shop-addr').val(shop.shopAddr);
                $('#shop-phone').val(shop.phone);
                $('#shop-desc').val(shop.shopDesc);
                var shopCategory = '<option data-id="'
                    +shop.shopCategory.shopCategoryId+'" selected>'
                    +shop.shopCategory.shopCategoryName+'</option>';
                var tempAreaHtml = '';
                data.areaList.map(function (item, index)
                {
                    tempAreaHtml+='<option data-id="'+item.areaId+'">'
                        +item.areaName+'</option>';

                });
                $('#shop-category').html(shopCategory);
                $('#shop-category').attr('disabled','disabled');
                $('#area').html(tempAreaHtml);
                $("#area option[data-id='"+shop.area.areaId+"']").attr("selected","selected");

            }
        });
        
    }



    //获取区域列表以及店铺类别列表
    function getShopInitInfo() {
        /**
         * jQuery中的$.getJSON( )方法函数主要用来从服务器加载json编码的数据，它使用的是GET HTTP请求。使用方法如下：

         getJSON(url,[data],[callback])

         url：string类型， 发送请求地址  data ：可选参数， 待发送 Key/value 参数 ，
         同get，post类型的data callback ：可选参数，载入成功时回调函数，同get，post类型的callback
         */
        $.getJSON(initUrl, function (data) {

            console.log(data);
            if (data.success) {
                var tempHtml = '';
                var tempAreaHtml = '';
                data.shopCategoryList.map(function (item, index) {
                    tempHtml += '<option data-id="' + item.shopCategoryId + '">' + item.shopCategoryName + '</option>';

                });
                console.log(data.areaList);
                data.areaList.map(function (item, index) {
                    tempAreaHtml += '<option data-id="' + item.areaId + '">' + item.areaName + '</option>';

                });
                //$选择
                $('#shop-category').html(tempHtml);
                $('#area').html(tempAreaHtml);

            }

        });
    }


    $('#submit').click(function () {
        var shop = {};
        if(isEdit)
        {
            shop.shopId = shopId;
        }
        //#表示id选择器
        shop.shopName = $('#shop-name').val();
        shop.shopAddr = $('#shop-addr').val();
        shop.phone = $('#shop-phone').val();
        shop.shopDesc = $('#shop-desc').val();
        shop.shopCategory = {
            shopCategoryId: $('#shop-category').find('option').not(function () {
                return !this.selected;
            }).data('id')

        };

        shop.area = {
            areaId: $('#area').find('option').not(function () {
                return !this.selected;
            }).data('id')
        };
        var shopImg = $('#shop-img')[0].files[0];
        var formData = new FormData();
        formData.append('shopImg',shopImg);
        formData.append('shopStr',JSON.stringify(shop));
        var verifyCodeActual = $('#j_captcha').val();
        if(!verifyCodeActual)
        {
            $.toast('请输入验证码！');
            return;
        }
        console.log("22222");

        formData.append('verifyCodeActual',verifyCodeActual);
        $.ajax({

            url:(isEdit?editShopUrl:registerShopUrl),
            type:'POST',
            data:formData,
            contentType:false,
            processData:false,
            cache:false,
            success:function(data)
            {
                if(data.success)
                {
                    $.toast('提交成功');
                }
                else
                {
                    $.toast('提交失败'+data.errMsg);

                }
                //更换验证码图片
                $("#captcha_img").click();
            }
        });


    });



})
