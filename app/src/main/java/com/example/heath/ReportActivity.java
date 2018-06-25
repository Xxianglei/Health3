package com.example.heath;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;


import com.example.heath.Datebase.DataBaseManager;
import com.example.heath.Datebase.TizhongModle;
import com.example.heath.Datebase.UserModle;
import com.example.heath.Datebase.XueyaModle;
import com.example.heath.HttpUtils.OkNetRequest;
import com.example.heath.adpter.CardFragmentPagerAdapter;
import com.example.heath.view.ShadowTransformer;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

import static com.example.heath.utils.DisplayUtil.dpToPixels;

public class ReportActivity extends AppCompatActivity  {


    private ViewPager mViewPager;
    private CardFragmentPagerAdapter mFragmentCardAdapter;
    private ShadowTransformer mFragmentCardShadowTransformer;
    private String appcode = "e5245cb4f806431185e56bb2efd5eaf9";
    private String xueya_week = "http://bpweek.market.alicloudapi.com/alicloudapi/report/bloodPressureWeek";
    private DataBaseManager dataBaseManager;
    private  String tizhong_week="http://weightweek.market.alicloudapi.com/alicloudapi/report/weightWeek";
    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);
        initview();
       // String url=tizhongweb("13166991256");
    }



    private void initview() {
        dataBaseManager = new DataBaseManager();
        ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mFragmentCardAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager(),
                dpToPixels(2, this));
        mFragmentCardShadowTransformer = new ShadowTransformer(mViewPager, mFragmentCardAdapter);
        mViewPager.setAdapter(mFragmentCardAdapter);
        mViewPager.setPageTransformer(false, mFragmentCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setLongClickable(true);
        mViewPager.setClickable(true);
        mViewPager.setEnabled(true);
        mViewPager.setOnTouchListener(new View.OnTouchListener() {

            int flage = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        flage = 0;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        flage = 1;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (flage == 0) {
                            int item = mViewPager.getCurrentItem();
                            if (item == 0) {
                              ///  url = xueyaweb("13166991256");
                                Intent intent = new Intent(ReportActivity.this, Week_Report.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("url", "http://blog.sina.com.cn/s/blog_5de73d0b0100vby4.html");
                                intent.putExtras(bundle);
                                startActivity(intent);

                            } else if (item == 1) {
                                String url=tizhongweb("13166991256");
                                Intent intent = new Intent(ReportActivity.this, Week_Report.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("url", "");
                                intent.putExtras(bundle);
                                startActivity(intent);
                            } else if (item == 2) {
                                Intent intent = new Intent(ReportActivity.this, Week_Report.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("url", "");
                                intent.putExtras(bundle);
                                startActivity(intent);
                            } else if (item == 3) {
                                Intent intent = new Intent(ReportActivity.this, Week_Report.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("url", "");
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        }
                        break;
                }
                return false;
            }

        });

    }

    /**
     * memberId	STRING	必选	测量者唯一标识,用于关联既往数据
     * dataList	STRING	必选	血压数据列表，时间范围：当前日期00:00:00向前7天内
     *
     * @param "memberId: "2804ea8653eb4582b3705e576f154d7e",
     *                   "testTime": "2017-11-18 11:33:12",
     *                   "highValue": 146,
     *                   "lowValue": 80
     * @param memberId
     */
    private String xueyaweb(String memberId) {
        HashMap<String, String> params = new HashMap<>();
        // 添加请求参数
        final Gson gson = new Gson();
        List<XueyaModle> list = dataBaseManager.readxyList();
        List<xueya> xueyas = new ArrayList<xueya>();
        Log.e("list.size()", list.size() + "***");
        if (list.size() >= 7) {
            int b = 26;
            for (int a = list.size() - 1; a >= list.size() - 7; a--) {
                xueya xue = new xueya();
                xue.setId(String.valueOf(list.get(a).getId()));
                if (b == 32) {
                    xue.setTestTime("2018-06-01 21:17:12");
                } else
                    xue.setTestTime("2018-05-" + b + " 21:17:12");

                // xue.setTestTime(list.get(a).getDate());
                xue.setHighValue(list.get(a).getHigh_data());
                xue.setLowValue(list.get(a).getLow_data());
                Log.e("details", String.valueOf(list.get(a).getHigh_data()) + String.valueOf(list.get(a).getHigh_data()));
                xueyas.add(xue);
                b++;
            }
        }
        String str = gson.toJson(xueyas);
        params.put("memberId", "13166991256");
        params.put("dataList", str);
        Log.e("血压数据", str);
        OkNetRequest.alypostFormRequest(xueya_week, params, new OkNetRequest.DataCallBack() {
            @Override
            public void requestSuccess(Response response,String result) throws Exception {
                Log.e("成功", result.toString());
                back back = gson.fromJson(result.toString(), back.class);
                url = back.getData();
                Log.e("url", url);
            }

            @Override
            public void progressSuccess(Response response) throws Exception {

            }

            @Override
            public void requestFailure(Request request, IOException e) {
                // 请求失败的回调
                Log.e("血压测试失败", request.body().toString());
            }
        }, appcode);

        return url;

    }
    private String tizhongweb(String memberId) {
        HashMap<String, String> params = new HashMap<>();
        // 添加请求参数
        final Gson gson = new Gson();
        List<TizhongModle> list = dataBaseManager.readtzList();
        List<tizhong> tizhongs = new ArrayList<tizhong>();
        List<UserModle> list1=dataBaseManager.readuserList();
        Log.e("list.size()", list.size() + "***");
        if (list.size() >= 7) {
            int b = 7;
            for (int a = list.size() - 1; a >= list.size() - 7; a--) {
                tizhong tizhong=new tizhong();
                tizhong.setId(String.valueOf(list.get(a).getId()));
                tizhong.setTestTime("2018-06-" + b + " 21:17:12");
                // xue.setTestTime(list.get(a).getDate());
                tizhong.setWeightValue(list.get(a).getData());
                if (list1.size()>0) {
                    tizhong.setHightVlaue(list1.get(list1.size()-1).getHigh());
                }
                else tizhong.setHightVlaue(170);  // 默认170
                tizhongs.add(tizhong);
                b++;
            }
        }
        String str = gson.toJson(tizhongs);
        params.put("memberId", "13166991256");
        params.put("dataList", str);
        Log.e("体重数据", str);
        OkNetRequest.alypostFormRequest(tizhong_week, params, new OkNetRequest.DataCallBack() {
            @Override
            public void requestSuccess(Response response,String result) throws Exception {
                Log.e("成功", result.toString());
                back back = gson.fromJson(result.toString(), back.class);
                url = back.getData();
                Log.e("url", url);
            }

            @Override
            public void progressSuccess(Response response) throws Exception {

            }

            @Override
            public void requestFailure(Request request, IOException e) {
                // 请求失败的回调
                Log.e("体重测试失败", request.body().toString());
            }
        }, appcode);

        return url;

    }


class tizhong{
    private String id;
    private String testTime;
    private int hightVlaue;
    private int weightValue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTestTime() {
        return testTime;
    }

    public void setTestTime(String testTime) {
        this.testTime = testTime;
    }

    public int getHightVlaue() {
        return hightVlaue;
    }

    public void setHightVlaue(int hightVlaue) {
        this.hightVlaue = hightVlaue;
    }

    public int getWeightValue() {
        return weightValue;
    }

    public void setWeightValue(int weightValue) {
        this.weightValue = weightValue;
    }
}
    class xueya {
        private String id;
        private String testTime;
        private int highValue;
        private int lowValue;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTestTime() {
            return testTime;
        }

        public void setTestTime(String testTime) {
            this.testTime = testTime;
        }

        public int getHighValue() {
            return highValue;
        }

        public void setHighValue(int highValue) {
            this.highValue = highValue;
        }

        public int getLowValue() {
            return lowValue;
        }

        public void setLowValue(int lowValue) {
            this.lowValue = lowValue;
        }
    }


    class back {
        private String code;
        private String msg;
        private String data;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }


        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }


}
