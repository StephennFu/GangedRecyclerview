package com.fatchao.gangedrecyclerview;

import static com.fatchao.gangedrecyclerview.SortDetailFragment.isManualDrag;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager.LayoutParams;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import com.fatchao.gangedrecyclerview.Monitor.AppBlockCanaryContext;
import com.fatchao.gangedrecyclerview.custom.AnimView;
import com.fatchao.gangedrecyclerview.custom.FoldableLayout;
import com.fatchao.gangedrecyclerview.wrapper.HeaderAndFooterWrapper;
import com.github.moduth.blockcanary.BlockCanary;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CheckListener {
    private final static String TAG = "MainActivity";
    private RecyclerView rvSort;
    private SortAdapter mSortAdapter;
    private SortDetailFragment mSortDetailFragment;
    private Context mContext;
    private LinearLayoutManager mLinearLayoutManager;
    private int targetPosition;//点击左边某一个具体的item的位置
    private boolean isMoved;
    private SortBean mSortBean;
    public static boolean isSecondMove = false;
    private int mIndex = 0;

    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private AnimView leftArrowView;
    private AnimView rightArrowView;

    private SortAdapter mSingleRowAdapter;
    private SingleRowSortDetailFragment mSingleRowSortDetailFragment;


  private View mUnFoldView;
  private View mFoldView;

    public FoldableLayout foldableLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        BlockCanary.install(this, new AppBlockCanaryContext()).start();

//      mUnFoldView = findViewById(R.id.layout_unfold);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        initData();
    }

    private void initData() {
        //获取asset目录下的资源文件
        String assetsData = getAssetsData("sort.json");
        Gson gson = new Gson();
        mSortBean = gson.fromJson(assetsData, SortBean.class);
        List<SortBean.CategoryOneArrayBean> categoryOneArray = mSortBean.getCategoryOneArray();
        List<String> list = new ArrayList<>();
        //初始化左侧列表数据
        for (int i = 0; i < categoryOneArray.size(); i++) {
            list.add(categoryOneArray.get(i).getName());
        }
        mSortAdapter = new SortAdapter(mContext, list, new RvListener() {
            @Override
            public void onItemClick(int id, int position) {
                if (mSortDetailFragment != null) {
                    isMoved = false;
                    targetPosition = position;
                    setChecked(position, true);
                }
            }
        });


        initHeaderAndFooter();
        rvSort.setAdapter(mHeaderAndFooterWrapper);
        createFragment();
    }


  private void initHeaderAndFooter()
  {
    mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mSortAdapter);
    initHeaderView();
    initFooterView();

    mHeaderAndFooterWrapper.addHeaderView(leftArrowView);
    mHeaderAndFooterWrapper.addFootView(rightArrowView);

  }

  private void initHeaderView() {
    leftArrowView = new AnimView(mContext);
    leftArrowView.setImageResource(R.drawable.ic_arrow_left);
    leftArrowView.setBackgroundColor(Color.RED);
    LayoutParams lps = new LayoutParams();
    lps.width = 120;
    leftArrowView.setLayoutParams(lps);
  }

  private void initFooterView() {
    rightArrowView = new AnimView(mContext);
    rightArrowView.setImageResource(R.drawable.ic_arrow_right);
    rightArrowView.setBackgroundColor(Color.RED);
    LayoutParams lps = new LayoutParams();
    lps.width = 120;
    rightArrowView.setLayoutParams(lps);
  }

    //从资源文件中获取分类json
    private String getAssetsData(String path) {
        String result = "";
        try {
            //获取输入流
            InputStream mAssets = getAssets().open(path);
            //获取文件的字节数
            int lenght = mAssets.available();
            //创建byte数组
            byte[] buffer = new byte[lenght];
            //将文件中的数据写入到字节数组中
            mAssets.read(buffer);
            mAssets.close();
            result = new String(buffer);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Oops", e.getMessage());
            return result;
        }
    }


    public void createFragment() {

      createMutiRowsFragment();

      createrSingleRowFragment();


    }

  private void createrSingleRowFragment() {
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    mSingleRowSortDetailFragment = new SingleRowSortDetailFragment();
    Bundle bundle = new Bundle();
    bundle.putParcelableArrayList("right", mSortBean.getCategoryOneArray());
    mSingleRowSortDetailFragment.setArguments(bundle);
    fragmentTransaction.add(R.id.single_lin_fragment, mSingleRowSortDetailFragment);
    fragmentTransaction.commit();
  }

  private void createMutiRowsFragment() {
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    mSortDetailFragment = new SortDetailFragment();
    Bundle bundle = new Bundle();
    bundle.putParcelableArrayList("right", mSortBean.getCategoryOneArray());
    mSortDetailFragment.setArguments(bundle);
    mSortDetailFragment.setListener(this);
    fragmentTransaction.add(R.id.lin_fragment, mSortDetailFragment);
    fragmentTransaction.commit();
  }



  private void setChecked(int position, boolean isSelf) {
        Log.d("p-------->", String.valueOf(position));
        //1、self == true 自身点击触发的滚动；2、self == false 被动触发的滚动
        if (isSelf) {
            isSecondMove = false;
            Log.d(TAG, "setChecked: position = " + position);
            mSortAdapter.setCheckedPosition(position, 1);
            //此处的位置需要根据每个分类的集合来进行计算
            int count = 0;
            for (int i = 0; i < position; i++) {
                count += mSortBean.getCategoryOneArray().get(i).getCategoryTwoArray().size();
            }
            count += position;
            mSortDetailFragment.setData(count);
            ItemHeaderDecoration.setCurrentTag(String.valueOf(targetPosition));
//               Log.d(TAG, "setChecked: targetPosition = " + targetPosition);
        } else {
//               Log.d(TAG, "setChecked: position_tag = " + position);
            if (isMoved) {
//                Log.d(TAG, "setChecked: into if branch(" + position + ")");
                isMoved = false;
            } else
//                Log.d(TAG, "setChecked: into else branch(" + position + ")");
                mSortAdapter.setCheckedPosition(position, 2);
            ItemHeaderDecoration.setCurrentTag(String.valueOf(position));


        }
//        moveToCenter(position);
//      Log.d(TAG, "setChecked: isSecondMove = " + isSecondMove);
//      Log.d(TAG, "setChecked: isManualDrag = " + isManualDrag);
      if (!isSecondMove || isManualDrag){
        Log.d(TAG, "setChecked & moveToCenterFixed: into moveToCenter(" + position + ")");
        moveToCenterFixed(position);
      }
    }

    //将当前选中的item居中
    private void moveToCenter(int position) {
        //将点击的position转换为当前屏幕上可见的item的位置以便于计算距离顶部的高度，从而进行移动居中
        View childAt = rvSort.getChildAt(position - mLinearLayoutManager.findFirstVisibleItemPosition());
        if (childAt != null) {
//            int y = (childAt.getTop() - rvSort.getHeight() / 2);
//            rvSort.smoothScrollBy(0, y);

            int x = childAt.getLeft() - rvSort.getWidth() / 2;

            mLinearLayoutManager.findViewByPosition(position);

            rvSort.smoothScrollBy(x, 0);
        }
    }


    private void moveToCenterFixed(int position) {
      //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
      int firstItem = mLinearLayoutManager.findFirstVisibleItemPosition();
      int lastItem = mLinearLayoutManager.findLastVisibleItemPosition();
      
      //然后区分情况
      if (position <= firstItem ){
        //当要置中的项在当前显示的第一个项的前面时
        Log.d(TAG, "moveToCenterFixed & onScrolled: position <= firstItem (" + position + ")");
        rvSort.scrollToPosition(position);
        rvSort.smoothScrollBy(-rvSort.getWidth() / 2, 0);
        isMoved = false;
      }else if ( position <= lastItem ){
        //当要置中的项已经在屏幕上显示时
//        int top = rvSort.getChildAt(position - firstItem).getTop();
        int x = rvSort.getChildAt(position - firstItem).getLeft() - rvSort.getWidth() / 2;
        Log.d(TAG, "moveToCenterFixed & onScrolled: position <= lastItem (" + position + ")");
//        Log.d(TAG, "moveToCenterFixed: x = " + x + ", position = " + position);
        rvSort.smoothScrollBy(x, 0);
        isSecondMove = true;
      }else{
        Log.d(TAG, "moveToCenterFixed & onScrolled: position > lastItem (" + position + ")");
        //当要置中的项在当前显示的最后一项的后面时
        rvSort.scrollToPosition(position);
        //这里这个变量是用在RecyclerView滚动监听里面的
        isMoved = true;
        mIndex = position;
      }
    }

    private void initView() {
       rvSort = (RecyclerView) findViewById(R.id.rv_sort);

//        mLinearLayoutManager = new LinearLayoutManager(mContext);
//        rvSort.setLayoutManager(mLinearLayoutManager);
//        DividerItemDecoration decoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);

      mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false);
      rvSort.setLayoutManager(mLinearLayoutManager);
      DividerItemDecoration decoration = new DividerItemDecoration(mContext, DividerItemDecoration.HORIZONTAL);
      rvSort.addItemDecoration(decoration);

      initListener();

    }

   private void initListener() {
     rvSort.addOnScrollListener(new RvSortListener());
   }


  @Override
    public void check(int position, boolean isScroll) {
        setChecked(position, isScroll);

    }

  private class RvSortListener extends RecyclerView.OnScrollListener {

    public static final String TAG = "RvSortListener";

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
      super.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      super.onScrolled(recyclerView, dx, dy);
//            Log.d("n---->", "onScrolled(" + dx + "," + dy + ")");
      int firstItem = mLinearLayoutManager.findFirstVisibleItemPosition();
//      Log.d(TAG, "onScrolled,move = " + isMoved);
      if (isMoved) {
        isMoved = false;
        Log.d(TAG, "onScrolled: mIndex = " + mIndex + " ," + "firstItem" + firstItem);
        if (mIndex - firstItem < 0) return;

        View childAt = rvSort.getChildAt(mIndex - firstItem);
        if (childAt == null ) return;
        int x = childAt.getLeft() - rvSort.getWidth() / 2;
        rvSort.smoothScrollBy(x, 0);
        isSecondMove = true;
      }

//      if (isMoved) {
//        isMoved = false;
//        int n = mIndex - firstItem;
//        if (0 <= n && n < rvSort.getChildCount()) {
//          int left = rvSort.getChildAt(n).getLeft() - rvSort.getWidth() / 2 ;
//          rvSort.smoothScrollBy(left, 0);
//          isSecondMove = true;
//        }
//      }
    }
  }

  public void test() {
    Log.d(TAG, "test: 1111111111111");

  }
}
