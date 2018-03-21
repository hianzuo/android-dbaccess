package com.flyhand.core.dto;

/**
 * Created by IntelliJ IDEA.
 * User: Ryan
 * Date: 11-12-30
 * Time: A.M. 11:37
 */
public class OrderRecord {
    public int id;  //主键
    public String appkey;  //应用Key
    public int appVCode;  //应用版本代码
    public String productName;  //产品名称
    public String user;  //用户
    public String mobileCode;  //用户手机唯一码
    public float price;  //单价
    public int count;  //数量
    public float totalPrice;  //总价
    public String qq;  //联系QQ
    public String email;  //联系邮件
    public String address;  //联系地址
    public String orderTime;  //下单日期
    public String dealTime;  //处理日期
    public String userNote;  //用户备注
    public String appNote;  //应用备注
    public String orderStatus;  //订单状态
}
