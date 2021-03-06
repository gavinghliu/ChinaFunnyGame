package com.anjoyo.meituan.ui;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import com.anjoyo.mlgy.R;
import com.anjoyo.meituan.Parser.ProductParser;
import com.anjoyo.meituan.adapter.HistoryAdapter;
import com.anjoyo.meituan.adapter.ProductAdapter;
import com.anjoyo.meituan.adapter.SellerAdapter;
import com.anjoyo.meituan.adapter.ServiceAdapter;
import com.anjoyo.meituan.app.AppContext;
import com.anjoyo.meituan.domain.Product;
import com.anjoyo.meituan.domain.Seller;
import com.anjoyo.meituan.myview.ExpandTabView;
import com.anjoyo.meituan.myview.ViewLeft;
import com.anjoyo.meituan.myview.ViewRight;
import com.anjoyo.meituan.myview.ViewService;
import com.anjoyo.meituan.utils.Logger;
import com.anjoyo.meituan.utils.ScrollLayout;
import com.anjoyo.meituan.utils.ScrollLayout.OnViewChangeListener;
import com.anjoyo.meituan.utils.XListView;
import com.anjoyo.meituan.utils.XListView.IXListViewListener;

@SuppressLint("HandlerLeak")
public class HistoryActivity extends BaseActivity implements
		OnViewChangeListener ,OnClickListener{
	private RadioButton button_allmerchant, button_salemerchant,
			button_service;
	private ScrollLayout scrollLayout;
	private int viewCount;
	private int mCurSel;
	private RadioButton[] buttons;
	private ExpandTabView expandTabView, expandTabViewSeller,
			expandTabViewService;
	private ArrayList<View> mViewArray = new ArrayList<View>();
	private ArrayList<View> mViewArraySellr = new ArrayList<View>();
	private ArrayList<View> mViewArrayService = new ArrayList<View>();
	private ViewLeft viewLeft;
	// private ViewMiddle viewMiddle;
	private ViewRight viewRight;
	private ViewService viewService;
	public static ArrayList<Product> allList = new ArrayList<Product>();
	public static ArrayList<Seller> saleList = new ArrayList<Seller>();
	public static ArrayList<Product> serviceList = new ArrayList<Product>();
	private SellerAdapter merchantSaleAdapter;
	private HistoryAdapter productAdapter;
	private HistoryAdapter serviceAdapter;
	public static XListView aListView, sListView, serviceListView;
	int currenViewPostion;
	private RelativeLayout backLayout;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.history_activity);
		super.onCreate(savedInstanceState);
		initMerchantHeader();
		initProductListView();
		initMerchantSaleListView();
		initServiceListView();
		initPageScroll();
		initClassifyView();
		backLayout = (RelativeLayout) findViewById(R.id.back_layout);
		backLayout.setOnClickListener(this);
	}

	@Override
	void init() {

	}

	/*
	 * 初始化分类栏
	 */
	private void initClassifyView() {
		expandTabView = (ExpandTabView) findViewById(R.id.expandtab_view_product);
		viewLeft = new ViewLeft(this);
		viewRight = new ViewRight(this);

		mViewArray.add(viewLeft);
		ArrayList<String> mTextArray = new ArrayList<String>();
		mTextArray.add("全部");
		int[] photo = { R.drawable.ic_category_all };
		expandTabView.setValue(mTextArray, mViewArray, photo);
		expandTabView.setTitle(viewLeft.getShowText(), 1);
		expandTabView.setPhoto(photo[0], 0);

		expandTabViewService = (ExpandTabView) findViewById(R.id.expandtab_view_service);
		viewService = new ViewService(this);
		mViewArrayService.add(viewService);
		mTextArray = new ArrayList<String>();
		mTextArray.add("全部");
		int[] photo3 = { R.drawable.ic_category_all };
		expandTabViewService.setValue(mTextArray, mViewArrayService, photo3);
		expandTabViewService.setTitle(viewService.getShowText(), 1);
		expandTabViewService.setPhoto(photo3[0], 0);

		viewService.setOnSelectListener(new ViewService.OnSelectListener() {

			public void getValue(String distance, String showText) {
				onRefreshService(viewRight, distance, showText);
			}
		});

		expandTabViewSeller = (ExpandTabView) findViewById(R.id.expandtab_view_seller);

		mViewArraySellr.add(viewRight);
		mTextArray = new ArrayList<String>();
		mTextArray.add("全部");
		int[] photo2 = { R.drawable.ic_category_all };
		expandTabViewSeller.setValue(mTextArray, mViewArraySellr, photo2);
		expandTabViewSeller.setTitle(viewRight.getShowText(), 1);
		expandTabViewSeller.setPhoto(photo2[0], 0);

		viewLeft.setOnSelectListener(new ViewLeft.OnSelectListener() {

			public void getValue(String distance, String showText) {
				onRefreshProduct(viewLeft, distance, showText);
			}
		});

		viewRight.setOnSelectListener(new ViewRight.OnSelectListener() {

			public void getValue(String distance, String showText) {
				onRefreshSeller(viewRight, distance, showText);
			}
		});

		expandTabView.setVisibility(View.VISIBLE);
		expandTabViewSeller.setVisibility(View.GONE);
		expandTabViewService.setVisibility(View.GONE);
	}

	/*
	 * 刷新分类栏左侧的图片
	 */
	private void onRefreshPhoto(View view, int showPhoto) {
		expandTabView.onPressBack();
		int position = getPositon(view);
		if (position >= 0
				&& !expandTabView.getPhoto(position).equals(showPhoto)) {
			expandTabView.setPhoto(showPhoto, position);
		}
	}

	private void onRefreshProduct(View view, String kind, String showText) {

		expandTabView.onPressBack();
		int position = getPositon(view);
		if (position >= 0 && !expandTabView.getTitle(position).equals(showText)) {
			expandTabView.setTitle(showText, position);
		}
		ArrayList<Product> productList = new ArrayList<Product>();
		for (int i = 0; i < allList.size(); i++) {
			Product product = allList.get(i);
			if (kind.equals("1") || product.getKind().equals(kind)) {
				productList.add(product);
			}
		}
		productAdapter.setList(productList);
		productAdapter.notifyDataSetChanged();
	}

	private void onRefreshSeller(View view, String kind, String showText) {

		expandTabViewSeller.onPressBack();
		int position = getPositon(view);
		if (position >= 0
				&& !expandTabViewSeller.getTitle(position).equals(showText)) {
			expandTabViewSeller.setTitle(showText, position);
		}

		ArrayList<Seller> sellerList = new ArrayList<Seller>();
		for (int i = 0; i < saleList.size(); i++) {
			Seller seller = saleList.get(i);
			if (kind.equals("1") || seller.getKind().equals(kind)) {
				sellerList.add(seller);
			}
		}
		merchantSaleAdapter.setList(sellerList);
		merchantSaleAdapter.notifyDataSetChanged();
	}

	private void onRefreshService(View view, String kind, String showText) {

		expandTabViewService.onPressBack();
		int position = getPositon(view);
		if (position >= 0
				&& !expandTabViewService.getTitle(position).equals(showText)) {
			expandTabViewService.setTitle(showText, position);
		}
		ArrayList<Product> productList = new ArrayList<Product>();
		for (int i = 0; i < serviceList.size(); i++) {
			Product product = serviceList.get(i);
			if (kind.equals("1") || product.getKind().equals(kind)) {
				productList.add(product);
			}
		}
		serviceAdapter.setList(productList);
		serviceAdapter.notifyDataSetChanged();
	}

	private int getPositon(View tView) {
		for (int i = 0; i < mViewArray.size(); i++) {
			if (mViewArray.get(i) == tView) {
				return i;
			}
		}

		for (int i = 0; i < mViewArraySellr.size(); i++) {
			if (mViewArraySellr.get(i) == tView) {
				return i;
			}
		}
		return -1;
	}

	public void onBackPressed() {

		if (!expandTabView.onPressBack()) {
			finish();
		}
	}

	// 初始化textView,button控件
	private void initMerchantHeader() {
		button_allmerchant = (RadioButton) findViewById(R.id.button_allmerchant);
		button_salemerchant = (RadioButton) findViewById(R.id.button_salemerchant);
		button_service = (RadioButton) findViewById(R.id.button_service);
	}

	private void initProductListView() {
		aListView = (XListView) findViewById(R.id.listview_merchantall);
		ProductParser productParer = new ProductParser();
		String res = "{'products' : [{product_id:1,product_name:清润葡萄籽晚安膜力晶露,is_fav:0,product_like_count:3,product_islike:0,comment_count:3,product_kind:2,product_picture:'',product_des:'即刻，肌肤水感润泽，柔滑饱满；日复一日，肌肤鲜活亮采，水润弹滑；完美水光肌：肌肤吹弹可破，仿佛喝饱水。',product_rank:4,product_price:160},"
				+ "{product_id:2,product_name:清润葡萄籽泡沫洁面膏,is_fav:0,product_like_count:100,product_islike:1,comment_count:2,product_kind:3,product_picture:'',product_des:'蕴含肌肤所需的基本矿物质与保湿精华，能彻底洗去妆容，油脂和污垢，并滋润肌肤，无紧绷感，使肌肤徜徉在清新舒爽的愉悦感觉中。使用4周后，肤色暗沉明显得到改善，肌肤质地更细腻。',product_rank:3,product_price:90}]}";

		ArrayList<Product> product = productParer.getMerchant(res);

		if (product != null && !product.isEmpty()) {
			allList.clear();
			allList.addAll(product);
		}
		AppContext appContext = (AppContext) getApplicationContext();
		appContext.setProduct(allList);
		productAdapter = new HistoryAdapter(HistoryActivity.this, allList);
		productAdapter.setAction("HistoryProduct");
		aListView.setAdapter(productAdapter);

		aListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 测试
				Logger.i("点击了ListView");
			}
		});

		aListView.setPullLoadEnable(false);
		aListView.setPullRefreshEnable(false);
	}

	private void initServiceListView() {
		serviceListView = (XListView) findViewById(R.id.listview_service);
		serviceList = new ArrayList<Product>();
		ProductParser productParer = new ProductParser();
//		String res = "{'products' : [{product_id:1,product_name:历史服务1,is_fav:0,product_like_count:100,product_islike:0,has_comment:1,service_time:2014-08-19,comment_count:5,product_kind:2,product_picture:'',product_des:产品描述~~,product_rank:4,product_price:1000},"
//				+ "{product_id:1,product_name:历史服务2,product_like_count:100,is_fav:0,product_islike:0,has_comment:0,comment_count:2,service_time:2014-08-11,product_kind:3,product_picture:'',product_des:产品描述~~,product_rank:3,product_price:1500},"
//				+ "{product_id:1,product_name:历史服务3,product_like_count:100,is_fav:0,product_islike:0,has_comment:1,comment_count:123,service_time:2014-08-09,product_kind:4,product_picture:'',product_des:产品描述~~,product_rank:1,product_price:1400}]}";

		String res = "{'products' : [{product_id:21,product_name:肩颈护理,is_fav:1,product_like_count:100,product_islike:0,comment_count:5,product_kind:2,product_picture:'',product_des:'去除肩颈疲劳，身姿活力再现！\n为您精选德国kneipp肌肉舒缓精油和草本能量热膜，配以肩颈养疗手法，畅通气血，改善因肩周、颈椎等引起的头痛、头晕和肩颈肌肉酸痛等状况，让您的身姿自由舒展。',product_rank:4,product_price:100},"
				+ "{product_id:22,product_name:淋巴净化护理,is_fav:0,comment_count:2,product_like_count:100,product_islike:0,product_kind:3,product_picture:'',product_des:'身心舒畅的排毒养疗之旅！\n为您精选德国Kneipp薰衣草安神平衡精油和天然植物精华，配以精油沐足和淋巴净化手法，加速全身淋巴循环，悄然间排除毒素、净化身心，让您体态之美更显轻盈。',product_rank:3,product_price:1500},"
				+ "{product_id:23,product_name:酵素SPA护理,is_fav:0,product_like_count:100,product_islike:1,comment_count:123,product_kind:4,product_picture:'',product_des:'精选120年树龄的香柏木粉，添加水果、蔬菜、药草、野草、海藻等380余种原料合成提取的天然酵素，利用自然发酵的热量、酵素及发酵代谢物的作用，从内而外的提高体表温度，提升人体自我免疫力。',product_rank:1,product_price:1400}]}";

		ArrayList<Product> product = productParer.getMerchant(res);

		if (product != null && !product.isEmpty()) {
			serviceList.clear();
			serviceList.addAll(product);
		}
		serviceAdapter = new HistoryAdapter(HistoryActivity.this, serviceList);
		serviceAdapter.setAction("HistoryService");
		serviceListView.setAdapter(serviceAdapter);

		serviceListView.setPullLoadEnable(false);
		serviceListView.setPullRefreshEnable(false);
	}

	private void onLoad(XListView listView) {
		listView.stopRefresh();
		listView.stopLoadMore();
		listView.setRefreshTime("刚刚");
	}

	private void initMerchantSaleListView() {
//		sListView = (XListView) findViewById(R.id.listview_merchantsale);
//		sListView.setPullLoadEnable(true);
//		String res = "{\"sellers\" : [{seller_id:1,seller_name:测试门店1,seller_like_count:100,seller_islike:0,comment_count:5,seller_address:XXX市XXX区XXX路XXX号,seller_phone:12356752312,seller_picture:test_pic,seller_rank:2,seller_kind:3},"
//				+ "{seller_id:1,seller_name:测试门店2,seller_like_count:100,seller_islike:0,comment_count:31,seller_address:XXX市XXX区XXX路XXX号,seller_phone:12375675756,seller_picture:test_pic,seller_rank:4,seller_kind:4},"
//				+ "{seller_id:1,seller_name:测试门店3,seller_like_count:100,seller_islike:0,comment_count:23,seller_address:XXX市XXX区XXX路XXX号,seller_phone:12375675756,seller_picture:test_pic,seller_rank:4,seller_kind:4}]}";
//
//		ArrayList<Seller> merchant = Seller.parseSeller(res);
//
//		if (merchant != null && !merchant.isEmpty()) {
//			saleList.clear();
//			saleList.addAll(merchant);
//		}
//
//		merchantSaleAdapter = new SellerAdapter(HistoryActivity.this, saleList);
//		sListView.setAdapter(merchantSaleAdapter);
//		/*
//		 * itmes 设置监听
//		 */
//		sListView.setOnItemClickListener(new OnItemClickListener() {
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				// 测试
//				Logger.i("点击了ListView");
//			}
//		});
//
//		sListView.setXListViewListener(new IXListViewListener() {
//
//			public void onRefresh() {
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				onLoad(sListView);
//
//			}
//
//			public void onLoadMore() {
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				onLoad(sListView);
//
//			}
//		});
//		sListView.setPullLoadEnable(false);
//		sListView.setPullRefreshEnable(false);

	}

	/**
	 * 初始化水平滚动翻页 RadioButton
	 */
	private void initPageScroll() {
		currenViewPostion = 0;
		scrollLayout = (ScrollLayout) findViewById(R.id.scrollView);
		RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.merchant_header);

		viewCount = scrollLayout.getChildCount();
		Logger.i("MerActivity:   viewCount" + viewCount);
		buttons = new RadioButton[viewCount];

		for (int i = 0; i < viewCount; i++) {
			buttons[i] = (RadioButton) relativeLayout.getChildAt(i * 2 + 1);
			buttons[i].setTag(i);
			buttons[i].setChecked(false);
			buttons[i].setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					int pos = (Integer) (v.getTag());
					currenViewPostion = pos;
					switch (pos) {
					case 0:
						expandTabView.setVisibility(View.VISIBLE);
						expandTabViewSeller.setVisibility(View.GONE);
						expandTabViewService.setVisibility(View.GONE);
						// 刷新全部商家和优惠商家的radioButton下端的图片
						button_allmerchant
								.setCompoundDrawablesWithIntrinsicBounds(0, 0,
										0, R.drawable.bottom_divider);
						button_salemerchant
								.setCompoundDrawablesWithIntrinsicBounds(0, 0,
										0, R.color.green);
						button_service.setCompoundDrawablesWithIntrinsicBounds(
								0, 0, 0, R.color.green);
						break;
					case 1:
						expandTabViewSeller.setVisibility(View.VISIBLE);
						expandTabView.setVisibility(View.GONE);
						expandTabViewService.setVisibility(View.GONE);
						button_salemerchant
								.setCompoundDrawablesWithIntrinsicBounds(0, 0,
										0, R.color.green);
						button_allmerchant
								.setCompoundDrawablesWithIntrinsicBounds(0, 0,
										0, R.color.green);
						button_service.setCompoundDrawablesWithIntrinsicBounds(
								0, 0, 0, R.drawable.bottom_divider);
						break;
					case 2:
						expandTabViewSeller.setVisibility(View.GONE);
						expandTabView.setVisibility(View.GONE);
						expandTabViewService.setVisibility(View.VISIBLE);
						button_salemerchant
								.setCompoundDrawablesWithIntrinsicBounds(0, 0,
										0, R.color.green);
						button_allmerchant
								.setCompoundDrawablesWithIntrinsicBounds(0, 0,
										0, R.drawable.bottom_divider);
						button_service.setCompoundDrawablesWithIntrinsicBounds(
								0, 0, 0, R.color.green);
						break;
					}
					scrollLayout.snapToScreen(pos);
				}
			});
		}

		// 设置第一显示屏
		mCurSel = 0;
		buttons[mCurSel].setChecked(true);
		scrollLayout
				.SetOnViewChangeListener(new ScrollLayout.OnViewChangeListener() {
					public void OnViewChange(int viewIndex) {
						// 切换列表视图-如果列表数据为空：加载数据
						currenViewPostion = viewIndex;
						switch (viewIndex) {
						case 0:
							expandTabView.setVisibility(View.VISIBLE);
							expandTabViewSeller.setVisibility(View.GONE);
							expandTabViewService.setVisibility(View.GONE);
							button_allmerchant
									.setCompoundDrawablesWithIntrinsicBounds(0,
											0, 0, R.drawable.bottom_divider);
							button_salemerchant
									.setCompoundDrawablesWithIntrinsicBounds(0,
											0, 0, R.color.green);
							button_service
									.setCompoundDrawablesWithIntrinsicBounds(0,
											0, 0, R.color.green);
							break;
						case 1:
							expandTabViewService.setVisibility(View.VISIBLE);
							expandTabView.setVisibility(View.GONE);
							expandTabViewSeller.setVisibility(View.GONE);
							button_salemerchant
									.setCompoundDrawablesWithIntrinsicBounds(0,
											0, 0, R.color.green);
							button_allmerchant
									.setCompoundDrawablesWithIntrinsicBounds(0,
											0, 0, R.color.green);
							button_service
									.setCompoundDrawablesWithIntrinsicBounds(0,
											0, 0, R.drawable.bottom_divider);
							break;
						case 2:
							expandTabViewSeller.setVisibility(View.VISIBLE);
							expandTabView.setVisibility(View.GONE);
							expandTabViewService.setVisibility(View.GONE);
							button_salemerchant
									.setCompoundDrawablesWithIntrinsicBounds(0,
											0, 0, R.drawable.bottom_divider);
							button_allmerchant
									.setCompoundDrawablesWithIntrinsicBounds(0,
											0, 0, R.color.green);
							button_service
									.setCompoundDrawablesWithIntrinsicBounds(0,
											0, 0, R.color.green);
							break;
						}
					}
				});
	}

	public void OnViewChange(int view) {
	}

	@Override
	protected void onResume() {
		super.onResume();
		serviceAdapter.notifyDataSetChanged();
		productAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.back_layout:
			finish();
			break;

		default:
			break;
		}
	}
}
