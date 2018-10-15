package megvii.testfacepass.adapter;

/**
 * Created by Administrator on 2018/7/3.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import java.util.List;


import megvii.testfacepass.MyApplication;
import megvii.testfacepass.R;
import megvii.testfacepass.view.GlideRoundTransform;

/**
 * Created  2018/1/15.
 */


public class MoBanAdapter extends RecyclerView.Adapter<MoBanAdapter.ViewHolder> {
    private RequestOptions myOptions2 = new RequestOptions()
            .fitCenter()
            .error(R.drawable.erroy_bg)
            //   .transform(new GlideCircleTransform(MyApplication.myApplication, 2, Color.parseColor("#ffffffff")));
            .transform(new GlideRoundTransform(MyApplication.myApplication, 10));

    private List<Integer> list;
    private Context context;
    private int dw,dh;
    private boolean heng;
    private OnRvItemClick mOnRvItemClick;

    public MoBanAdapter(List<Integer> list, Context context, int dw, int dh,boolean heng,OnRvItemClick onRvItemClick)
    {
        this.list = list;
        this.context=context;
        this.dw=dw;
        this.dh=dh;
        this.heng=heng;
        this.mOnRvItemClick=onRvItemClick;
      }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mobanitem, parent, false);
             ScreenAdapterTools.getInstance().loadView(view);


           return new ViewHolder(view);
      }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.name.setText("模版:"+(position+1));

        try {

            Glide.with(context)
                    .load(list.get(position))
                    .apply(myOptions2)
                    .into(holder.touxiang);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (heng){
            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) holder.root_rl.getLayoutParams();
            params2.width=dh/2+30;
            params2.height=dh/2-65;
            holder. root_rl.setLayoutParams(params2);
            holder. root_rl.invalidate();

        }else {

            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) holder.root_rl.getLayoutParams();
            params2.width=dw/2;
            params2.height=dw/2+50;
            holder. root_rl.setLayoutParams(params2);
            holder. root_rl.invalidate();
        }


     }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        ImageView touxiang;
        RelativeLayout root_rl;

        ViewHolder(View itemView) {
            super(itemView);
             touxiang =itemView.findViewById(R.id.touxiang);
             root_rl = itemView .findViewById(R.id.root_rl2);
             name = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnRvItemClick != null)
                mOnRvItemClick.onItemClick(v, getAdapterPosition());
        }
    }
    /**
     * item点击接口
     */
    public interface OnRvItemClick {
        void onItemClick(View v, int position);
    }
}