package com.sbsct.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.model.SbsPrinterData;
import com.sbsct.common.CommonFunc;
import com.sbsct.config.EnumConstsSbs;
import com.tool.utils.utils.AidlUtils;
import com.tool.utils.utils.SPUtils;
import com.tool.utils.view.MyGridView;
import com.sbsct.R;
import com.sbsct.adapter.MyMenuAdapter;
import com.sbsct.config.Config;
import com.sbsct.model.Menu;

import java.util.ArrayList;
import java.util.List;


public class SaleMainActivity extends BaseActivity{


    private String TAG = "SaleMainActivity";

//    private RelativeLayout btnSale;
//    private RelativeLayout btnyxfSale;
//    private RelativeLayout btnyxfSaleManager;
//    private RelativeLayout btnhd;
//
//    private RelativeLayout btnRecord;
//    private RelativeLayout btnSaleManager;
//    private RelativeLayout btnSaleInfo;
//    private RelativeLayout btnGetInfo;
//    private RelativeLayout btnChangePass;
//    private RelativeLayout btnEndQuery;
//    private RelativeLayout btnShitRoom;
//    private RelativeLayout btnRicher_e_qb;
//    private RelativeLayout btnVerification;
//
//    private LinearLayout ll1;
//    private LinearLayout ll2;
//    private LinearLayout ll3;
//    private LinearLayout ll4;
//    private LinearLayout ll5;
//    private LinearLayout ll6;
//    private LinearLayout ll7;


    private List<View> views = null;

    public ViewPager mViewPager;
    public LinearLayout viewPoints;
    private SbsPrinterData printerData;


    private List<Menu> list = new ArrayList<Menu>();
    private MyGridView gridView;
    private MyMenuAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_main3);
        mContext = this;
//        AppManager.getAppManager().addActivity(this);
        initTitle("首页");
        initView();
//        addLinstener();

        initData();



    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AidlUtils.unBindService();
    }

    private void initData() {

    }


    private void initView() {


        for (int i = 0; i < EnumConstsSbs.MenuType.values().length; i++){
            Menu menu = new Menu();
            menu.setBg(EnumConstsSbs.MenuType.values()[i].getBg());
            menu.setName(EnumConstsSbs.MenuType.values()[i].getName());
            list.add(menu);
        }



        gridView = (MyGridView) findViewById(R.id.id_gridview);
        adapter = new MyMenuAdapter(mContext, list);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "onItemClick " + position);
                // 下拉刷新占据一个位置
                int index = EnumConstsSbs.MenuType.getCodeByName(list.get(position).getName());
                switch (index){
                    case 1:
                        SPUtils.put(SaleMainActivity.this, Config.APP_TYPE, Config.APP_SBS);

                        CommonFunc.startAction(SaleMainActivity.this, InputAmountActivity.class, false);

                        break;
                    case 2:
                        CommonFunc.startAction(SaleMainActivity.this, RecordInfoActivity.class, false);
                        break;
                    case 3:
                        CommonFunc.startAction(SaleMainActivity.this, CheckOperatorLoginActivity.class, false);
                        break;
                    case 4:
                        CommonFunc.startAction(SaleMainActivity.this, OpenCardActivity.class, false);
                        break;
                    case 5:
                        CommonFunc.startAction(SaleMainActivity.this, VerificationActivity.class, false);
//                        CommonFunc.startAction(SaleMainActivity.this, TestPopActivity.class, false);

                        break;
                    case 6:
                        CommonFunc.startAction(SaleMainActivity.this, SysMainActivity.class, false);
                        break;
                }
            }
        });

    }



}
