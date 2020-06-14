package com.zzz.webscanf.model;


import org.litepal.crud.DataSupport;

/**
 * Created by 懒鼠睡zzz on 2017/10/15.
 */

public class DataBaseItem extends DataSupport {
    public  String name;
    public  double price;
    public  String  scan_num;

    public DataBaseItem(){

    }
    public static DataBaseItem getInstant(Wares wares){
        DataBaseItem first = DataSupport.where("scan_num = ?", wares.scan_num).findFirst(DataBaseItem.class);
        if(first!=null){
            return first;
        }else {
            DataBaseItem dataBaseItem=new DataBaseItem();
            return dataBaseItem;
        }
    }
    public String getScan_num() {
        return scan_num;
    }

    public void setScan_num(String scan_num) {
        this.scan_num = scan_num;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
