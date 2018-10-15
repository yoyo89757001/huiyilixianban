package megvii.testfacepass.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.sdsmdg.tastytoast.TastyToast;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;
import megvii.testfacepass.MyApplication;
import megvii.testfacepass.R;
import megvii.testfacepass.adapter.MoBanAdapter;
import megvii.testfacepass.beans.BaoCunBean;

public class MoBanActivity extends AppCompatActivity implements MoBanAdapter.OnRvItemClick {
    @BindView(R.id.recycerview)
    RecyclerView recycerview;
    private Box<BaoCunBean> baoCunBeanDao = MyApplication.myApplication.getBoxStore().boxFor(BaoCunBean.class);
    private BaoCunBean baoCunBean = null;
    private List<Integer> integerList = new ArrayList<>();
    private MoBanAdapter adapter = null;

    private GridLayoutManager gridLayoutManager = new GridLayoutManager(MoBanActivity.this, 2, LinearLayoutManager.HORIZONTAL, false);
    private GridLayoutManager gridLayoutManager2 = new GridLayoutManager(MoBanActivity.this, 2, LinearLayoutManager.VERTICAL, false);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mo_ban);
        ScreenAdapterTools.getInstance().loadView(getWindow().getDecorView());
        ButterKnife.bind(this);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int dw = dm.widthPixels;
        int dh = dm.heightPixels;
        baoCunBean = baoCunBeanDao.get(123456L);


        if (baoCunBean.isHengOrShu()){
            //横
            recycerview.setLayoutManager(gridLayoutManager);
//            integerList.add(R.drawable.moban201);
//            integerList.add(R.drawable.moban201);
//            integerList.add(R.drawable.moban201);

        }else {
            //竖
            recycerview.setLayoutManager(gridLayoutManager2);
           // integerList.add(R.drawable.moban201);
            //integerList.add(R.drawable.mb202);
            integerList.add(R.drawable.disanbanfff);
        }

        adapter = new MoBanAdapter(integerList, MoBanActivity.this, dw, dh,baoCunBean.isHengOrShu(),this);
        recycerview.setAdapter(adapter);

    }

    @Override
    public void onItemClick(View v, int position) {
        Log.d("MoBanActivity", "position:" + position);
        if (baoCunBean.isHengOrShu()){
            //横屏
            baoCunBean.setMoban(100+position+1);
            baoCunBeanDao.put(baoCunBean);
        }else {
            baoCunBean.setMoban(200+position+1);
            baoCunBeanDao.put(baoCunBean);
        }
        Toast tastyToast = TastyToast.makeText(MoBanActivity.this, "已设置成模版:"+(position+1), TastyToast.LENGTH_LONG, TastyToast.INFO);
        tastyToast.setGravity(Gravity.CENTER, 0, 0);
        tastyToast.show();
    }
}
