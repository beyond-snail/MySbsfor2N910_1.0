package com.zfsbs.common;

////////////////////////////////////////////////////////////////////
//                          _ooOoo_                               //
//                         o8888888o                              //
//                         88" . "88                              //
//                         (| ^_^ |)                              //
//                         O\  =  /O                              //
//                      ____/`---'\____                           //
//                    .'  \\|     |//  `.                         //
//                   /  \\|||  :  |||//  \                        //
//                  /  _||||| -:- |||||-  \                       //
//                  |   | \\\  -  /// |   |                       //
//                  | \_|  ''\---/''  |   |                       //
//                  \  .-\__  `-`  ___/-. /                       //
//                ___`. .'  /--.--\  `. . ___                     //
//              ."" '<  `.___\_<|>_/___.'  >'"".                  //
//            | | :  `- \`.;`\ _ /`;.`/ - ` : | |                 //
//            \  \ `-.   \_ __\ /__ _/   .-` /  /                 //
//      ========`-.____`-.___\_____/___.-`____.-'========         //
//                           `=---='                              //
//      ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^        //
//              佛祖保佑       永无BUG     永不修改                  //
//                                                                //
//          佛曰:                                                  //
//                  写字楼里写字间，写字间里程序员；                   //
//                  程序人员写程序，又拿程序换酒钱。                   //
//                  酒醒只在网上坐，酒醉还来网下眠；                   //
//                  酒醉酒醒日复日，网上网下年复年。                   //
//                  但愿老死电脑间，不愿鞠躬老板前；                   //
//                  奔驰宝马贵者趣，公交自行程序员。                   //
//                  别人笑我忒疯癫，我笑自己命太贱；                   //
//                  不见满街漂亮妹，哪个归得程序员？                   //
////////////////////////////////////////////////////////////////////

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.model.SbsPrinterData;
import com.model.ShiftRoomSave;
import com.tool.utils.utils.EncryptMD5Util;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.SPUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToolNewLand;
import com.zfsbs.config.Constants;
import com.zfsbs.core.action.ZfQbAction;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.model.FailureData;
import com.zfsbs.model.MemberTransAmountResponse;
import com.zfsbs.model.SetClientOrder;
import com.zfsbs.model.TransUploadRequest;
import com.zfsbs.model.TransUploadResponse;
import com.zfsbs.myapplication.MyApplication;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**********************************************************
 * *
 * Created by wucongpeng on 2016/11/15.        *
 **********************************************************/


public class CommonFunc {


    /**
     * // 判断当前日期是否和保存的时间相同，不同那么就去签到 ,true 表示去签到
     * @param context
     * @param file_time
     * @param defaultValue
     * @return
     */
    public static boolean isLogin(Context context, String file_time, String defaultValue) {


        String old_login_time = (String) SPUtils.get(context, file_time, defaultValue);
        String current_time = StringUtils.getCurDate();
        if (!old_login_time.equals(current_time)) {
            return true;
        }

        return false;
    }




    /**
     * 排序
     * @param a
     * @return
     */
    public static ArrayList<String> getSortAsc(ArrayList<String> a) {
        String temp = "";
        int i;
        for (i = 0; i < a.size(); i++) {
            for (int j = a.size() - 1; j > i; j--) {
                if (a.get(j).compareTo(a.get(j - 1)) < 0) {
                    temp = a.get(j);
                    a.set(j, a.get(j - 1));
                    a.set(j - 1, temp);
                }
            }
        }
        return a;
    }

    /**
     * 获取订单号
     * @return
     */
    public static String getNewClientSn(Context context) {
        String device = "POS"; // 1:手机 2:Pos机
        String timestamp = StringUtils.getFormatCurTime();
        String randomNum = StringUtils.createRandomNumStr(3);
        String serial_no = StringUtils.getTerminalNo(ToolNewLand.getToolNewLand().getSerialNo());

        return device + timestamp  + serial_no + randomNum;
    }



    public static String getActivateCode(Context context, int payType) {
        String activateCode = "";
        String sm_type = MyApplication.getInstance().getLoginData().getScanPayType();
        switch (payType) {
            case Constants.PAY_WAY_CASH:
            case Constants.PAY_WAY_FLOT:
            case Constants.PAY_WAY_UNDO:
                activateCode = MyApplication.getInstance().getLoginData().getTerminalNo();
                break;
            case Constants.PAY_WAY_QB:
            case Constants.PAY_WAY_REFUND_QB:
                activateCode = ToolNewLand.getToolNewLand().getSerialNo();
                break;
            case Constants.PAY_WAY_ALY:
            case Constants.PAY_WAY_BFB:
            case Constants.PAY_WAY_JD:
            case Constants.PAY_WAY_WX:
            case Constants.PAY_WAY_REFUND_ALY:
            case Constants.PAY_WAY_REFUND_WX:
                if (!StringUtils.isEmpty(sm_type) && StringUtils.isEquals(sm_type, Constants.SM_TYPE_SQB)) {
                    activateCode = MyApplication.getInstance().getLoginData().getActiveCode();
                }else if (!StringUtils.isEmpty(sm_type) && StringUtils.isEquals(sm_type, Constants.SM_TYPE_FY)){
                    activateCode = MyApplication.getInstance().getLoginData().getActiveCode();
                }
                break;
            default:
                break;
        }
        return activateCode;
    }


    /**
     * 备份上送的交易流水
     *
     * @param request
     */
    public static void TransUploadDataBack(TransUploadRequest request) {
        // 先删除7天之前的数据
        DataSupport.deleteAll(TransUploadRequest.class, "t <= ?", StringUtils.getDateFromMillis(new Date(), -7) + "");
        if (request.save()) {
            LogUtils.e("上送流水备份存储成功");
        } else {
            LogUtils.e("上送流水备份存储失败");
        }
    }


    /**
     * 删除前7天的数据，保存最新7天数据
     */
    public static void PrinterDataDelete() {
        Date t = StringUtils.getDateFromDate(new Date(), -7);
        LogUtils.e(StringUtils.getDateFormate(t));
        List<SbsPrinterData> list = DataSupport.findAll(SbsPrinterData.class);
        for (int i = 0; i < list.size(); i++) {

            if (!StringUtils.isEmpty(list.get(i).getDateTime())) {
//				LogUtils.e(list.get(i).getDateTime());
                Date t1 = StringUtils.getDateFromString(list.get(i).getDateTime(), "yyyy-MM-dd");
                if (t1 != null) {
//					LogUtils.e(StringUtils.getDateFormate(t1));
                    if (t1.compareTo(t) < 0) {
                        int deleteCount = DataSupport.deleteAll(SbsPrinterData.class, "dateTime = ?",
                                list.get(i).getDateTime());
                        LogUtils.e(deleteCount + "删除数据成功");
                    }
                }

            }

        }
    }

    // 删除前7天的数据，保存最新7天数据
    public static void ShiftRoomDataDelete() {
        Date t = StringUtils.getDateFromDate(new Date(), -7);
        LogUtils.e(StringUtils.getDateFormate(t));
        List<ShiftRoomSave> list = DataSupport.findAll(ShiftRoomSave.class);
        for (int i = 0; i < list.size(); i++) {

            if (!StringUtils.isEmpty(StringUtils.timeStamp2Date(list.get(i).getEnd_time()+""))) {
                LogUtils.e(StringUtils.timeStamp2Date(list.get(i).getEnd_time()+""));
                Date t1 = StringUtils.getDateFromString(StringUtils.timeStamp2Date(list.get(i).getEnd_time()+""), "yyyy-MM-dd");
                if (t1 != null) {
                    LogUtils.e(StringUtils.getDateFormate(t1));
                    if (t1.compareTo(t) < 0) {
                        int deleteCount = DataSupport.deleteAll(ShiftRoomSave.class, "end_time = ?",
                                list.get(i).getEnd_time()+"");
                        LogUtils.e(deleteCount + "删除数据成功");
                    }
                }

            }

        }
    }

    /**
     * 获取到JSON字串
     * @param cmd
     * @param paramsMap
     * @return
     */
    public static String getJsonStr(String cmd, Map<String, Object> paramsMap, String sign, String key) {
        JSONObject jsonParams = null;

        ArrayList<String> keys = new ArrayList<String>();
        for (Iterator<String> it = paramsMap.keySet().iterator(); it.hasNext(); ) {
            keys.add(it.next());
        }
        String tmp = "";
        keys = CommonFunc.getSortAsc(keys);
        for (int i = 0; i < keys.size(); i++) {
            Object obj = paramsMap.get(keys.get(i));
            if (obj != null) {
                tmp = tmp + obj.toString();
            } else {
                LogUtils.e("getJsonStr", "getSortAsc obj is null");
            }
        }
        LogUtils.e(tmp);
        String verify = EncryptMD5Util.MD5(tmp + key);
        paramsMap.put(sign, verify);


        Map<String, Object> final_params = new HashMap<String, Object>();
        final_params.put("cmd", cmd);
        final_params.put("params", paramsMap);

        jsonParams = new JSONObject(final_params);

        return jsonParams.toString();
    }



    public static void startAction(Activity context, Class<?> cls, boolean flag) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        context.startActivity(intent);
        if (flag){
            context.finish();
        }
    }

    public static void startAction(Activity context, Class<?> cls, Bundle bundle, boolean flag) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtras(bundle);
        context.startActivity(intent);
        if (flag){
            context.finish();
        }
    }

    public static void startResultAction(Activity context, Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        if (bundle != null){
            intent.putExtras(bundle);
        }

        context.startActivityForResult(intent, requestCode);
    }


    /**
     * 设置会员的订单号
     * @param context
     * @param orderNo
     */
    public static void setMemberClientOrderNo(Context context, SetClientOrder orderNo){
        //清空
        SPUtils.remove(context, "clientNo");
        String data = new Gson().toJson(orderNo);
        SPUtils.put(context, "clientNo", data);
    }


    /**
     * 获取订单号
     * @param context
     * @return
     */
    public static SetClientOrder recoveryClientOrderNo(Context context){
        String data = (String) SPUtils.get(context, "clientNo", "");
        return StringUtils.isEmpty(data) ? null : new Gson().fromJson(data, SetClientOrder.class);
    }


//    public static String getSerialNo(Context context){
//        String serialNo;
//        ToolNewLand toolNewLand = new ToolNewLand();
//        toolNewLand.deviceBindService(context, ToolNewLand.serialNo, null);
//        serialNo = "";//toolNewLand.getSerialNo();
////        toolNewLand.deviceUnBindService();
//        return serialNo;
//    }





    /**
     * 备份当前会员信息
     * @param context
     * @param data
     */
    public static void setBackMemberInfo(Context context, MemberTransAmountResponse data){
        //先移除再添加
        ClearMemberInfo(context);
        String backpu_currentMember = new Gson().toJson(data);
        SPUtils.put(context, Constants.CURRENT_MEMBER_BACK, backpu_currentMember);
    }

    /**
     * 获取当前会员信息
     * @param context
     * @return
     */
    public static MemberTransAmountResponse recoveryMemberInfo(Context context) {
        String back_currentMember = (String) SPUtils.get(context, Constants.CURRENT_MEMBER_BACK, "");
        return StringUtils.isEmpty(back_currentMember) ? null : new Gson().fromJson(back_currentMember, MemberTransAmountResponse.class);
    }

    /**
     * 移除当前会员的信息
     * @param context
     */
    public static void ClearMemberInfo(Context context){
        SPUtils.remove(context, Constants.CURRENT_MEMBER_BACK);
    }


    /**
     * 备份当前会员信息
     * @param context
     * @param data
     */
    public static void setBackFailureInfo(Context context, FailureData data){
        //先移除再添加
        ClearFailureInfo(context);
        String backpu_currentMember = new Gson().toJson(data);
        SPUtils.put(context, Constants.CURRENT_Failure_BACK, backpu_currentMember);
    }

    /**
     * 获取当前会员信息
     * @param context
     * @return
     */
    public static FailureData recoveryFailureInfo(Context context) {
        String backpu_currentMember = (String) SPUtils.get(context, Constants.CURRENT_Failure_BACK, "");
        return StringUtils.isEmpty(backpu_currentMember) ? null : new Gson().fromJson(backpu_currentMember, FailureData.class);
    }

    /**
     * 移除当前会员的信息
     * @param context
     */
    public static void ClearFailureInfo(Context context){
        SPUtils.remove(context, Constants.CURRENT_Failure_BACK);
    }


    /**
     * 设置流水上送参数
     * @param printerData
     * @param memberData
     * @param clientOrderNo
     * @param transNo
     * @param authCode
     * @return
     */
    public static TransUploadRequest setTransUploadData(Context context, SbsPrinterData printerData, MemberTransAmountResponse memberData, String clientOrderNo, String transNo, String authCode) {
        final TransUploadRequest request = new TransUploadRequest();

        request.setSid(MyApplication.getInstance().getLoginData().getSid());
        request.setCardNo(memberData.getMemberCardNo());
        request.setPassword(memberData.getPass());

        if (printerData.getPayType() == Constants.PAY_WAY_CASH) {
            request.setCash(memberData.getRealMoney());
            request.setBankAmount(0);
        } else {
            request.setPayCardNo(printerData.getCardNo());
            request.setCash(0);
            request.setBankAmount(memberData.getRealMoney());
        }
        request.setCouponCoverAmount(memberData.getCouponCoverMoney());
        request.setPointCoverAmount(memberData.getPointCoverMoney());
        request.setCouponSns(memberData.getCouponSns());
        request.setClientOrderNo(clientOrderNo);
        request.setActivateCode(getActivateCode(context, printerData.getPayType()));
        request.setMerchantNo(printerData.getMerchantNo());
        request.setT(StringUtils.getdate2TimeStamp(printerData.getDateTime()));//(StringUtils.getCurTimeMills());
        LogUtils.e("time = " + printerData.getDateTime());
        long t = StringUtils.getdate2TimeStamp(printerData.getDateTime());
        LogUtils.e("t = " + t);
        String t1 = StringUtils.timeStamp2Date(t + "");
        LogUtils.e("t1 = " + t1);

        request.setTransNo(transNo);
        request.setAuthCode(authCode);
        request.setSerialNum(ToolNewLand.getToolNewLand().getSerialNo());
        request.setPayType(printerData.getPayType());
        request.setPointAmount(memberData.getPoint());
        request.setPhone(memberData.getPhone());
        request.setOld_trade_order_num(clientOrderNo);
        return request;
    }


    /**
     * 钱包失败查询
     * @param context
     * @param listener
     */
    public static void ZfQbFailQuery(Context context, ActionCallbackListener<TransUploadResponse> listener){
        Long sid = MyApplication.getInstance().getLoginData().getSid();
        final String orderNo = recoveryFailureInfo(context).getOrderNo();
        final String time = recoveryFailureInfo(context).getTime();

        ZfQbAction qb = new ZfQbAction(context);
        qb.query1(context, sid, orderNo, time, listener);
    }



    /**
     * 获取到JSON字串
     * @param cmd
     * @param paramsMap
     * @return
     */
    public static String Richer_getJsonStr(String cmd, Map<String, Object> paramsMap, String sign, String key) {
        JSONObject jsonParams = null;

        ArrayList<String> keys = new ArrayList<String>();
        for (Iterator<String> it = paramsMap.keySet().iterator(); it.hasNext(); ) {
            keys.add(it.next());
        }
        String tmp = "";
        keys = CommonFunc.getSortAsc(keys);
        for (int i = 0; i < keys.size(); i++) {
            Object obj = paramsMap.get(keys.get(i));
            if (obj != null) {
                tmp = tmp + obj.toString();
            } else {
                LogUtils.e("getJsonStr", "getSortAsc obj is null");
            }
        }
        LogUtils.e(tmp);
        String verify = EncryptMD5Util.MD5(tmp + key).toUpperCase();
//        paramsMap.put(sign, verify);


        Map<String, Object> final_params = new HashMap<String, Object>();
        final_params.put(sign, verify);
        final_params.put("params", paramsMap);

        jsonParams = new JSONObject(final_params);

        return jsonParams.toString();
    }


    /**
     * 新大陆支付调用
     * @param context
     * @param payTp
     * @param procCd
     * @param amt
     * @param orderNo
     */
    public static void pay(Activity context, int payTp, String procCd, String amt, String orderNo){
        try {
            ComponentName component = new ComponentName("com.newland.caishen", "com.newland.caishen.ui.activity.MainActivity");
            final Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            bundle.putString("msg_tp", "0200");
            bundle.putString("pay_tp", payTp+"");
            bundle.putString("proc_tp", "00");
            bundle.putString("proc_cd", procCd);
            bundle.putString("amt", amt);
            bundle.putString("order_no", orderNo);
            bundle.putString("appid", "com.zfsbs");
            bundle.putString("time_stamp", "");
            bundle.putString("print_info", "");
            bundle.putString("batchbillno", "");
            bundle.putString("systraceno", "");
            bundle.putString("merid","");
            bundle.putString("termid","");
            showBundleData(bundle);
            intent.putExtras(bundle);
            context.startActivityForResult(intent, 1);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "请安装收单应用", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void settle(Activity context){
        try {
            ComponentName component = new ComponentName("com.newland.caishen", "com.newland.caishen.ui.activity.MainActivity");
            final Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            bundle.putString("msg_tp", "0200");
            bundle.putString("pay_tp", "");
            bundle.putString("proc_tp", "00");
            bundle.putString("proc_cd", "900000");
            bundle.putString("amt", "");
            bundle.putString("order_no", "");
            bundle.putString("appid", "com.zfsbs");
            bundle.putString("time_stamp", "");
            bundle.putString("print_info", "");
            bundle.putString("batchbillno", "");
            bundle.putString("systraceno", "");
            bundle.putString("merid","");
            bundle.putString("termid","");
            showBundleData(bundle);
            intent.putExtras(bundle);
            context.startActivityForResult(intent, 1);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "请安装收单应用", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void undo(Activity context, int payTp, String procCd, String amt, String orderNo, String traceNo){
        try {
            ComponentName component = new ComponentName("com.newland.caishen", "com.newland.caishen.ui.activity.MainActivity");
            final Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            bundle.putString("msg_tp", "0200");
            bundle.putString("pay_tp", payTp+"");
            bundle.putString("proc_tp", "00");
            bundle.putString("proc_cd", procCd);
            bundle.putString("amt", amt);
            bundle.putString("order_no", orderNo);
            bundle.putString("appid", "com.zfsbs");
            bundle.putString("time_stamp", "");
            bundle.putString("print_info", "");
            bundle.putString("batchbillno", "");
            bundle.putString("systraceno", traceNo);
            bundle.putString("merid","");
            bundle.putString("termid","");

            showBundleData(bundle);
            intent.putExtras(bundle);
            context.startActivityForResult(intent, 1);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "请安装收单应用", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void query(Activity context, int payTp, String orderNo, String batchbillno){
        try {
            ComponentName component = new ComponentName("com.newland.caishen", "com.newland.caishen.ui.activity.MainActivity");
            final Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            bundle.putString("msg_tp", "0300");
            bundle.putString("pay_tp", payTp+"");
            bundle.putString("order_no", orderNo);
            bundle.putString("appid", "com.zfsbs");
            bundle.putString("batchbillno", batchbillno);

            showBundleData(bundle);
            intent.putExtras(bundle);
            context.startActivityForResult(intent, 1);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "请安装收单应用", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public static void showBundleData(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        String string = "Bundle{";
        for (String key : bundle.keySet()) {
            string += " " + key + " => " + bundle.get(key) + ";";
        }
        string += " }Bundle";

        Log.e("Bundle", string);
    }



}
