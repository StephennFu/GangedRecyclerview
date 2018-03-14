package com.fatchao.gangedrecyclerview.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.fatchao.gangedrecyclerview.R;
import com.fatchao.gangedrecyclerview.RvAdapter;
import com.fatchao.gangedrecyclerview.RvHolder;
import com.fatchao.gangedrecyclerview.RvListener;
import com.fatchao.gangedrecyclerview.bean.SingleRowBean;
import java.util.List;

/**
 * Created by fuyuanyuan on 2018/3/10.
 */

public class SingleRowAdapter extends RvAdapter<SingleRowBean> {

  public SingleRowAdapter(Context context, List<SingleRowBean> list, RvListener listener) {
    super(context, list, listener);
  }

  @Override
  protected int getLayoutId(int viewType) {
    return R.layout.layout_single_row;
  }

  @Override
  public int getItemViewType(int position) {
    return 1;
  }

  @Override
  protected RvHolder getHolder(View view, int viewType) {
    return new SingleRowHolder(view, viewType, listener);
  }

  public class SingleRowHolder extends RvHolder<SingleRowBean> {
    TextView tvName;
    ImageView avatar;

    public SingleRowHolder(View itemView, int type, RvListener listener) {
      super(itemView, type, listener);
      avatar = (ImageView) itemView.findViewById(R.id.ivAvatar);

    }

    @Override
    public void bindHolder(SingleRowBean singleRowBean, int position) {
//      tvName.setText(singleRowBean.getName());
    }

  }

}
