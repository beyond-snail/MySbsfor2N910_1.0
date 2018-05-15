package com.zfsbs.model;

import java.io.Serializable;
import java.util.List;

public class CouponsResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String memberName; // 用户名
	private String memberCardNo; // 会员卡号
	private String mobile; // 会员手机号
	private String icCardNo; //实体卡号
	private int pointUseMax; // 积分最大使用数
	private int frequency_min; //积分最小使用数
	private int point; // 当前积分数
	private int pointChangeRate; // 积分金额兑换比例
	private int couponNum; // 优惠券总数
	private List<Coupons> coupons; // 优惠券列表
	private boolean isMember; // 是否为会员
	private boolean freePassword; // 是否免密
	private int cantVerifyReason; //1门店不可核销 2未设置积分核销规则 3当前时段不可核销 4 积分数量不符合核销风控规则 5当日核销积分数已超限

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getMemberCardNo() {
		return memberCardNo;
	}

	public void setMemberCardNo(String memberCardNo) {
		this.memberCardNo = memberCardNo;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getIcCardNo() {
		return icCardNo;
	}

	public void setIcCardNo(String icCardNo) {
		this.icCardNo = icCardNo;
	}

	public int getFrequency_min() {
		return frequency_min;
	}

	public void setFrequency_min(int frequency_min) {
		this.frequency_min = frequency_min;
	}

	public int getPointUseMax() {
		return pointUseMax;
	}

	public void setPointUseMax(int pointUseMax) {
		this.pointUseMax = pointUseMax;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public int getPointChangeRate() {
		return pointChangeRate;
	}

	public void setPointChangeRate(int pointChangeRate) {
		this.pointChangeRate = pointChangeRate;
	}

	public int getCouponNum() {
		return couponNum;
	}

	public void setCouponNum(int couponNum) {
		this.couponNum = couponNum;
	}

	public List<Coupons> getCoupons() {
		return coupons;
	}

	public void setCoupons(List<Coupons> coupons) {
		this.coupons = coupons;
	}

	public boolean isMember() {
		return isMember;
	}

	public void setMember(boolean isMember) {
		this.isMember = isMember;
	}

	public boolean isFreePassword() {
		return freePassword;
	}

	public void setFreePassword(boolean freePassword) {
		this.freePassword = freePassword;
	}

	public int getCantVerifyReason() {
		return cantVerifyReason;
	}

	public void setCantVerifyReason(int cantVerifyReason) {
		this.cantVerifyReason = cantVerifyReason;
	}

	@Override
	public String toString() {
		return "CouponsResponse{" +
				"memberName='" + memberName + '\'' +
				", memberCardNo='" + memberCardNo + '\'' +
				", mobile='" + mobile + '\'' +
				", icCardNo='" + icCardNo + '\'' +
				", pointUseMax=" + pointUseMax +
				", frequency_min=" + frequency_min +
				", point=" + point +
				", pointChangeRate=" + pointChangeRate +
				", couponNum=" + couponNum +
				", coupons=" + coupons +
				", isMember=" + isMember +
				", freePassword=" + freePassword +
				", cantVerifyReason=" + cantVerifyReason +
				'}';
	}
}
