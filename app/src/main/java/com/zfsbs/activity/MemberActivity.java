package com.zfsbs.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tool.utils.activityManager.AppManager;
import com.tool.utils.dialog.MemberDialog;
import com.tool.utils.dialog.PassWordDialog;
import com.tool.utils.utils.Arith;
import com.tool.utils.utils.Base64Utils;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.zfsbs.R;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.config.Config;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.model.Coupons;
import com.zfsbs.model.CouponsResponse;
import com.zfsbs.model.MemberTransAmountRequest;
import com.zfsbs.model.MemberTransAmountResponse;
import com.zfsbs.model.SetClientOrder;
import com.zfsbs.myapplication.MyApplication;
import com.zfsbs.view.ConponsDialog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.zfsbs.common.CommonFunc.getNewClientSn;
import static com.zfsbs.common.CommonFunc.startAction;

public class MemberActivity extends BaseActivity implements View.OnClickListener {

    private TextView tMemberName;
    private TextView tMemberCardNo;
    private TextView tMemberPhoneNo;
    private TextView tMemberStkNo;
    private TextView tDoPoint;
    private EditText etUsedPoint;
    private TextView tPointAmount;
    private TextView tIsUsedPoint;
    private TextView tUseCouponsNum;
    private TextView tCouponsInfo;
    private TextView tShowUsedCoupons;
    private Button btNext;
    private Button btNoUsed;

    private CouponsResponse couponResponse;
    private int amount;
    private int pointChangeRate;
    private int point;
    private int frequency_min; //积分最小使用下限
    private int pointMin; // 用来判断最小的使用积分
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_member_info);
//        AppManager.getAppManager().addActivity(this);
        initTitle("会员优惠信息");
        initView();
        initData();
        addListener();
    }

    private void initView() {
        tMemberName = (TextView) findViewById(R.id.id_member_name);
        tMemberCardNo = (TextView) findViewById(R.id.id_memberCardNo);
        tMemberPhoneNo = (TextView) findViewById(R.id.id_phoneNo);
        tMemberStkNo = (TextView) findViewById(R.id.id_stk_cardNo);
        tDoPoint = (TextView) findViewById(R.id.id_do_point);
        etUsedPoint = (EditText) findViewById(R.id.id_use_point);
        etUsedPoint.setCursorVisible(false);// 隐藏光标
        etUsedPoint.setEnabled(true);
        tPointAmount = (TextView) findViewById(R.id.id_point_amount);
        tIsUsedPoint = (TextView) findViewById(R.id.id_isUsed_point);
        tUseCouponsNum = (TextView) findViewById(R.id.id_coupon_num);
        tCouponsInfo = (TextView) findViewById(R.id.id_coupon_info);
        tShowUsedCoupons = (TextView) findViewById(R.id.id_showCouponNum);
        btNext = (Button) findViewById(R.id.id_next);
        btNoUsed = (Button) findViewById(R.id.id_no_used);
    }

    private void initData() {
        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        BigDecimal big = new BigDecimal(bundle.getString("amount"));
        amount = big.multiply(new BigDecimal(100)).intValue();
        couponResponse = (CouponsResponse) bundle.getSerializable("member");
        pass = ""; // 设置默认为空

        textView(R.id.id_order_amount).setText(bundle.getString("amount"));

        if (couponResponse != null) {
            if (!StringUtils.isBlank(couponResponse.getMemberName())) {
                rinearLayout(R.id.id_ll_member_name).setVisibility(View.VISIBLE);
                tMemberName.setText(couponResponse.getMemberName());
            }
            if (!StringUtils.isBlank(couponResponse.getIcCardNo())){
                rinearLayout(R.id.id_ll_stk_cardNo).setVisibility(View.VISIBLE);
                tMemberStkNo.setText(couponResponse.getIcCardNo());
            }
            tMemberCardNo.setText(couponResponse.getMemberCardNo());
//            if (StringUtils.isBlank(couponResponse.getIcCardNo())){
//                tMemberCardNo.setText(couponResponse.getMemberCardNo());
//            }else{
//                tMemberCardNo.setText(couponResponse.getIcCardNo());
//            }
            if (!StringUtils.isBlank(couponResponse.getMobile())) {
                rinearLayout(R.id.id_ll_phone).setVisibility(View.VISIBLE);
                tMemberPhoneNo.setText(couponResponse.getMobile());
            }
            tDoPoint.setText(couponResponse.getPoint() + "点积分");
            tUseCouponsNum.setText(couponResponse.getCouponNum() + "张");
            tPointAmount.setText("可抵扣0元");
            pointChangeRate = couponResponse.getPointChangeRate();
            frequency_min = couponResponse.getFrequency_min();

            if (couponResponse.getCantVerifyReason() == 1){
                rinearLayout(R.id.ll_show_do_point).setVisibility(View.GONE);
                linearLayout(R.id.ll_show_edit_point).setVisibility(View.GONE);
            }

            getMaxUsePoint(amount);

            showCouponsChecked();
        }
    }

    //计算最大使用积分数
    private void getMaxUsePoint(int amount){
        double amountBig = Arith.mul(amount, pointChangeRate);
        double amountToPoint = Arith.divide(amountBig, 100);
        LogUtils.e("amountToPoint:" + amountToPoint);

        pointMin = (int) Math.floor(StringUtils.min(amountToPoint, (double) couponResponse.getPoint(), (double) couponResponse.getPointUseMax()));

        LogUtils.e("pointMin:" + pointMin);
    }


    private void addListener() {
        btNext.setOnClickListener(MemberActivity.this);
        btNoUsed.setOnClickListener(this);
        tIsUsedPoint.setOnClickListener(this);
        etUsedPoint.addTextChangedListener(new TextWatcherImpl());
        tCouponsInfo.setOnClickListener(this);
    }

    private class TextWatcherImpl implements TextWatcher {
        private boolean isChanged = false;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            tIsUsedPoint.setSelected(false);
            point = 0;
        }

        @Override
        public void afterTextChanged(Editable s) {
            double showPoint = 0;
            if (isChanged) {// ----->如果字符未改变则返回
                return;
            }
            isChanged = true;
            if (pointChangeRate != 0 && s.toString().length() > 0 && StringUtils.isNumeric(s.toString())) {
                showPoint = (Double.parseDouble(s.toString()));
                LogUtils.e("showPoint:" + showPoint);
                if (showPoint > pointMin) {
                    ToastUtils.CustomShow(MemberActivity.this, "最大使用积分:" + pointMin + "积分");
                    etUsedPoint.setText(pointMin + "");
                    etUsedPoint.setSelection(etUsedPoint.getText().toString().length());

                    double temp = Arith.mul(pointMin, 100);
                    double temp1 = Arith.divide(temp, pointChangeRate);

                    tPointAmount.setText("可抵用" + StringUtils.formatIntMoney((int) temp1) + "元");

                    if (pointMin != 0) {
                        tIsUsedPoint.setSelected(true);
                    }
                    point = pointMin;
                    isChanged = false;
                    return;
                } else {

//                    if (showPoint == 0){
//                        return;
//                    }

                    double temp = Arith.mul(showPoint, 100);
                    double temp1 = Arith.divide(temp, pointChangeRate);

                    tPointAmount.setText("可抵用" + StringUtils.formatIntMoney((int) temp1) + "元");
                    if (showPoint != 0) {
                        tIsUsedPoint.setSelected(true);
                    }
                    point = (int) (Double.parseDouble(etUsedPoint.getText().toString()));
                }
            } else {
                tIsUsedPoint.setSelected(false);
                tPointAmount.setText("可抵用" + 0 + "元");
            }
            isChanged = false;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_next:
                if (tIsUsedPoint.isSelected()) {
                    if (point < frequency_min) {
                        ToastUtils.CustomShow(MemberActivity.this, "最小使用积分:" + frequency_min + "积分");
                        break;
                    }
                    if (couponResponse.getPoint()==0){
                        point = 0;
                    }
                }

                int money = 0;
                for (int i = 0; i < couponResponse.getCouponNum(); i++) {
                    if (couponResponse.getCoupons().get(i).getCouponType() ==1 &&
                            couponResponse.getCoupons().get(i).isChecked()){
                        money += couponResponse.getCoupons().get(i).getMoney();
                    }
                }
                if (amount <= money){
                    point = 0;
                }
                memberTransAmountAction();
                break;
            case R.id.id_no_used:

                //备份订单号
//                SetClientOrder order = new SetClientOrder();
//                order.setStatus(false);
//                CommonFunc.setMemberClientOrderNo(this, order);
//
//                MemberTransAmountResponse member = new MemberTransAmountResponse();
//                member.setRealMoney(amount);
//                member.setTradeMoney(amount);
//                member.setMemberCardNo(couponResponse.isMember() ? couponResponse.getMemberCardNo() : "");
//                member.setStkCardNo(couponResponse.getIcCardNo());
//                CommonFunc.setBackMemberInfo(this, member);
//                CommonFunc.startAction(this, ZfPayActivity.class, true);
                point = 0;
                memberTransAmountAction();
                break;
            case R.id.id_isUsed_point:
                if (tIsUsedPoint.isSelected()) {
                    tIsUsedPoint.setSelected(false);
                    point = 0;
                } else {
                    if (etUsedPoint.getText().toString().length() > 0) {
                        tIsUsedPoint.setSelected(true);
                        point = (int) (Double.parseDouble(etUsedPoint.getText().toString()));
                    }
                }
                break;
            case R.id.id_coupon_info:
                showCouponsInfo();
                break;
            default:
                break;
        }
    }


    private void memberTransAmountAction() {
        final MemberTransAmountRequest request = new MemberTransAmountRequest();
        request.setSid(MyApplication.getInstance().getLoginData().getSid());
        request.setMemberCardNo(couponResponse.getMemberCardNo());
        request.setPassword(pass);
        request.setTradeMoney(amount);
        request.setPoint(point);
        request.setCouponSn(getSn());
        request.setMemberName(couponResponse.getMemberName());
        request.setClientOrderNo(getNewClientSn(mContext));

        this.sbsAction.memberTransAmount(MemberActivity.this, request, new ActionCallbackListener<MemberTransAmountResponse>() {
            @Override
            public void onSuccess(MemberTransAmountResponse data) {


                //备份订单号
                SetClientOrder order = new SetClientOrder();
                order.setStatus(true);
                order.setClientNo(request.getClientOrderNo());
                CommonFunc.setMemberClientOrderNo(MemberActivity.this, order);


                data.setPoint(point);
                data.setPass(pass);
                data.setStkCardNo(couponResponse.getIcCardNo());
                CommonFunc.setBackMemberInfo(MemberActivity.this, data);

                startAction(MemberActivity.this, ZfPayActivity.class, true);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(MemberActivity.this, message);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {
                AppManager.getAppManager().finishAllActivity();

                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(MemberActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(MemberActivity.this, OperatorLoginActivity1.class, false);
                }
            }
        });
    }


    private Handler mhandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    for (int i = 0; i < couponResponse.getCouponNum(); i++) {
                        if (couponResponse.getCoupons().get(i).isChecked()) {
                            if (couponResponse.getCoupons().get(i).getCouponType() == 2) {

                                    tShowUsedCoupons.setText("当前默认使用了1张打折券，面额为："
                                            +couponResponse.getCoupons().get(i).getMoney()/100.0 + "折");

                            }else{
                                tShowUsedCoupons.setText("当前默认使用了1张优惠券，面额为："
                                        + StringUtils.formatIntMoney(couponResponse.getCoupons().get(i).getMoney()) + "元");
                            }
                            break;
                        }
                    }

                    break;
                case 1:

                    int num = 0;
                    int money = 0;

                    int count = 0;

                    tShowUsedCoupons.setText("");
                    etUsedPoint.setEnabled(true);

                    for (int i = 0; i < couponResponse.getCouponNum(); i++) {
                        if (couponResponse.getCoupons().get(i).isChecked()) {
                            if (couponResponse.getCoupons().get(i).getCouponType() == 1) {
                                money += couponResponse.getCoupons().get(i).getMoney();
                                num++;
                                tShowUsedCoupons.setText("当前使用了 " + num + " 张优惠券,面额为："+StringUtils.formatIntMoney(money) + "元");
                                if (amount <= money) {
                                    etUsedPoint.setEnabled(false);
                                    etUsedPoint.setText("");
                                    tIsUsedPoint.setSelected(false);
                                }else {
                                    getMaxUsePoint(amount-money);
                                    etUsedPoint.setText("");
                                    tIsUsedPoint.setSelected(false);
                                }
                            }else{
                                tShowUsedCoupons.setText("当前使用了1张打折券,面额为："+ couponResponse.getCoupons().get(i).getMoney()/100.0 + "折\n");
                                getMaxUsePoint((amount * couponResponse.getCoupons().get(i).getMoney())/1000);
                                etUsedPoint.setText("");
                                tIsUsedPoint.setSelected(false);
                            }

                            count++;
                        }
                    }

                    //当没有选中的时候返回清空编辑框
                    if (count == 0){
                        getMaxUsePoint(amount);
                        etUsedPoint.setEnabled(true);
                        etUsedPoint.setText("");
                        tIsUsedPoint.setSelected(false);
                    }

                    break;
                default:
                    break;
            }
        }


    };


    // 默认选中最大金额的
    private void CheckMaxAmount() {
        int max = 0;
        int min = 0;

        List<Integer> inputMax = new ArrayList<Integer>();
        List<Integer> inputMin = new ArrayList<Integer>();
        for (int i = 0; i < couponResponse.getCouponNum(); i++){
            if (couponResponse.getCoupons().get(i).getCouponType() == 1) {
                inputMax.add(couponResponse.getCoupons().get(i).getMoney());
            }else {
                inputMin.add(couponResponse.getCoupons().get(i).getMoney());
            }
        }
        if (inputMin.size() > 0) {
            min = Collections.min(inputMin);
        }
        if (inputMax.size() > 0) {
            max = Collections.max(inputMax);
        }
        LogUtils.e("max= " + max);
        LogUtils.e("min= " + min);
        // 判断是哪个选项
        for (int i = 0; i < couponResponse.getCouponNum(); i++) {

            if (inputMin.size() > 0){
                if (couponResponse.getCoupons().get(i).getMoney() == min){
                    couponResponse.getCoupons().get(i).setChecked(true);
                    getMaxUsePoint((amount * couponResponse.getCoupons().get(i).getMoney())/1000);
                    etUsedPoint.setText("");
                    tIsUsedPoint.setSelected(false);
                    break;
                }
            }
            if (inputMax.size() > 0){
                if (couponResponse.getCoupons().get(i).getMoney() == max){
                    couponResponse.getCoupons().get(i).setChecked(true);
                    if (couponResponse.getCoupons().get(i).getMoney() >= amount){
                        etUsedPoint.setEnabled(false);
                    }else{
                        getMaxUsePoint(amount-couponResponse.getCoupons().get(i).getMoney());
                        etUsedPoint.setText("");
                        tIsUsedPoint.setSelected(false);
                    }

                    break;
                }
            }
        }
    }

    // 有优惠券显示默认最大选中的优惠券
    private void showCouponsChecked() {
        if (couponResponse.getCouponNum() <= 0) {
            return;
        }
        CheckMaxAmount();
        mhandler.sendEmptyMessage(0);
    }

    private void showCouponsInfo() {
        if (couponResponse.getCouponNum() <= 0) {
            return;
        }


        ConponsDialog dialog = new ConponsDialog(this, R.layout.activity_coupons_list, couponResponse.getCoupons());
        dialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                LogUtils.e("setOnDismissListener");
                mhandler.sendEmptyMessage(1);
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private String getSn() {
        List<Coupons> list = couponResponse.getCoupons();
        StringBuilder sn = new StringBuilder();
        for (int i = 0; i < couponResponse.getCouponNum(); i++) {
            if (list.get(i).isChecked()) {
                sn.append(list.get(i).getSn());
                sn.append(",");
            }
        }
        return (sn.toString().length() > 0 ? sn.substring(0, sn.toString().length() - 1) : "");
    }



}
