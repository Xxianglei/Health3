package com.example.heath.Main_Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.heath.Check_tizhong;
import com.example.heath.Datebase.DataBaseManager;
import com.example.heath.Datebase.TizhongModle;
import com.example.heath.R;
import com.example.heath.utils.LineChartManager;
import com.example.heath.view.MyScrollView;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class ScrollFragment4 extends HeaderViewPagerFragment {

    @BindView(R.id.scrollView)
    NestedScrollView mScrollView;
    private List<TizhongModle> list;
    private TextView zhi;
    private TextView date;
    private MyScrollView end;

    public static ScrollFragment4 newInstance() {
        return new ScrollFragment4();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scroll4, container, false);
        initView(view);
        initData();
        return view;
    }

    private void initData() {
        if (list.size()>0){
            zhi.setText(list.get(list.size()-1).getData()+"");
            date.setText(list.get(list.size()-1).getDate()+"");
        }

    }

    private void initView(View view) {
        LinearLayout ll=view.findViewById(R.id.cry_nodata);
        LineChart lineChart = view.findViewById(R.id.lineChart);
        DataBaseManager dataBaseManager=new DataBaseManager();
        list = dataBaseManager.readtzList();
        if (list.size()>=7){
            ll.setVisibility(View.GONE);
            lineChart.setVisibility(View.VISIBLE);
        }
        end = view.findViewById(R.id.end);
        RelativeLayout relativeLayout=view.findViewById(R.id.his_data);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(getActivity(), Check_tizhong.class));
            }
        });

        zhi = view.findViewById(R.id.zhi);
        date = view.findViewById(R.id.date);
        //绑定控件

        LineChartManager lineChartManager1 = new LineChartManager(lineChart);
        NestedScrollView scrollview = view.findViewById(R.id.scrollView);
        //设置x轴的数据
        if (list.size()>=7) {
            ArrayList<Float> xValues = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                xValues.add((float) i);
            }
            //设置y轴的数据()
            List<List<Float>> yValues = new ArrayList<>();
            for (int i = 0; i < 1; i++) {
                List<Float> yValue = new ArrayList<>();
                for (int j = list.size()-7; j <=list.size()-1; j++) {
                    yValue.add((float) list.get(j).getData());
                }
                yValues.add(yValue);
            }

            //颜色集合
            List<Integer> colours = new ArrayList<>();
            colours.add(Color.RED);
            //线的名字集合
            List<String> names = new ArrayList<>();
            names.add("体重（kg）");

            //创建多条折线的图表
            lineChartManager1.showLineChart(xValues, yValues, names, colours, true, false);
            lineChartManager1.setDescription("体重值");
            lineChartManager1.setYAxis(120, 0, 11);
            lineChartManager1.setLowLimitLine(70, "理想体重", Color.GREEN);

        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialViewPagerHelper.registerScrollView(getActivity(), mScrollView);
    }

    @Override
    public View getScrollableView() {
        return end;
    }
}
