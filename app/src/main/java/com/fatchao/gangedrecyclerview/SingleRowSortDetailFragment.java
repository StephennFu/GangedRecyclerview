package com.fatchao.gangedrecyclerview;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fatchao.gangedrecyclerview.SortBean.CategoryOneArrayBean.CategoryTwoArrayBean;
import com.fatchao.gangedrecyclerview.adapter.SingleRowAdapter;
import com.fatchao.gangedrecyclerview.bean.SingleRowBean;
import com.fatchao.gangedrecyclerview.wrapper.HeaderAndFooterWrapper;
import java.util.ArrayList;
import java.util.List;

public class SingleRowSortDetailFragment extends BaseFragment<SortDetailPresenter, String> implements CheckListener {
  private RecyclerView mRv;
  private SingleRowAdapter mAdapter;
  private LinearLayoutManager mManager;
  private List<SingleRowBean> mDatas = new ArrayList<>();

  private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
  private ImageView leftArrowView;
  private ImageView rightArrowView;
  ArrayList<SortBean.CategoryOneArrayBean> rightList;


  @Override
  protected int getLayoutId() {
    return R.layout.fragment_sort_detail_single_row;
  }

  @Override
  protected void initCustomView(View view) {
    mRv = (RecyclerView) view.findViewById(R.id.rv_single_row);

  }

  @Override
  protected void initListener() {
    mRv.addOnScrollListener(new RecyclerViewListener());
  }

  @Override
  protected SortDetailPresenter initPresenter() {
    showRightPage(1);
    mManager = new LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false);
    mRv.setLayoutManager(mManager);
    initData();
    mAdapter = new SingleRowAdapter(mContext, mDatas, new RvListener() {
      @Override
      public void onItemClick(int id, int position) {
        String content = "content";
        Snackbar snackbar = Snackbar.make(mRv, "当前点击的是" + content + ":"
                + mDatas.get(position).getName(),
            Snackbar.LENGTH_SHORT);
        View mView = snackbar.getView();
        mView.setBackgroundColor(Color.BLUE);
        TextView text = (TextView) mView.findViewById(android.support.design.R.id.snackbar_text);
        text.setTextColor(Color.WHITE);
        text.setTextSize(25);
        snackbar.show();
      }
    });

    initHeaderAndFooter();
    mRv.setAdapter(mHeaderAndFooterWrapper);
    mAdapter.notifyDataSetChanged();
    return new SortDetailPresenter();
  }


  private void initHeaderAndFooter()
  {
     mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mAdapter);
     initHeaderView();
     initFooterView();

     mHeaderAndFooterWrapper.addHeaderView(leftArrowView);
     mHeaderAndFooterWrapper.addFootView(rightArrowView);

  }

  private void initHeaderView() {
    leftArrowView = new ImageView(mContext);
    leftArrowView.setImageResource(R.drawable.ic_arrow_left);
    leftArrowView.setBackgroundColor(Color.RED);
  }

  private void initFooterView() {
    rightArrowView = new ImageView(mContext);
    rightArrowView.setImageResource(R.drawable.ic_arrow_right);
    leftArrowView.setBackgroundColor(Color.RED);
  }


  private void initData() {
    rightList = getArguments().getParcelableArrayList("right");
    getSpecialSingleRowBean(2);
  }

  private void getSpecialSingleRowBean(int i) {
    mDatas.clear();
    List<CategoryTwoArrayBean> categoryTwoArray = rightList.get(i).getCategoryTwoArray();
    for (int j = 0; j < categoryTwoArray.size(); j++) {
      SingleRowBean body = new SingleRowBean(categoryTwoArray.get(j).getName());
      body.setTag(String.valueOf(i));
      mDatas.add(body);
    }
  }

  @Override
  public void onClick(View v) {

  }

  @Override
  public void refreshView(int code, String data) {

  }

  @Override
  protected void getData() {

  }

  @Override
  public void check(int position, boolean isScroll) {

  }


  private class RecyclerViewListener extends RecyclerView.OnScrollListener {

    public static final String TAG = "RecyclerViewListener";

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
      super.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      super.onScrolled(recyclerView, dx, dy);
//
    }
  }


}
