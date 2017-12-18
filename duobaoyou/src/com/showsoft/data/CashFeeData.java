package com.showsoft.data;

public class CashFeeData {
	/**可提现金额**/
	private String avaimoney;
	/**免费提现金额**/
	private String mian_money;
	/**收取手续费的提现金额**/
	private String shou_money;
	/**收取的提现手续费**/
	private String fee;
	public String getAvaimoney() {
		return avaimoney;
	}
	public void setAvaimoney(String avaimoney) {
		this.avaimoney = avaimoney;
	}
	public String getMian_money() {
		return mian_money;
	}
	public void setMian_money(String mian_money) {
		this.mian_money = mian_money;
	}
	public String getShou_money() {
		return shou_money;
	}
	public void setShou_money(String shou_money) {
		this.shou_money = shou_money;
	}
	public String getFee() {
		return fee;
	}
	public void setFee(String fee) {
		this.fee = fee;
	}
	@Override
	public String toString() {
		return "CashFeeData [avaimoney=" + avaimoney + ", mian_money=" + mian_money + ", shou_money=" + shou_money
				+ ", fee=" + fee + "]";
	}
}
