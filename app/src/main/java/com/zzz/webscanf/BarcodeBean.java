package com.zzz.webscanf;

public class BarcodeBean {

    /**
     * code : 1
     * msg : 数据返回成功！非app_id请求方式将于2020年3月1日停止对外服务，请替换成app_id请求的方式，详情请访问：https://github.com/MZCretin/RollToolsApi#%E8%A7%A3%E9%94%81%E6%96%B0%E6%96%B9%E5%BC%8F
     * data : {"goodsName":"红荔牌红米酒","barcode":"6912398302113","price":"27.00","brand":"红荔","supplier":"广东顺德酒厂有限公司","standard":"29%vol 500ml"}
     */

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * goodsName : 红荔牌红米酒
         * barcode : 6912398302113
         * price : 27.00
         * brand : 红荔
         * supplier : 广东顺德酒厂有限公司
         * standard : 29%vol 500ml
         */

        private String goodsName;
        private String barcode;
        private String price;
        private String brand;
        private String supplier;
        private String standard;

        public String getGoodsName() {
            return goodsName;
        }

        public void setGoodsName(String goodsName) {
            this.goodsName = goodsName;
        }

        public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getSupplier() {
            return supplier;
        }

        public void setSupplier(String supplier) {
            this.supplier = supplier;
        }

        public String getStandard() {
            return standard;
        }

        public void setStandard(String standard) {
            this.standard = standard;
        }
    }
}
