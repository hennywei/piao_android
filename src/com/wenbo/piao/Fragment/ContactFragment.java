package com.wenbo.piao.Fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.wenbo.androidpiao.R;
import com.wenbo.piao.dialog.LoginDialog;
import com.wenbo.piao.sqllite.domain.UserInfo;
import com.wenbo.piao.sqllite.service.UserInfoService;
import com.wenbo.piao.sqllite.util.SqlLiteUtil;
import com.wenbo.piao.task.GetPersonConstanct;
import com.wenbo.piao.util.HttpClientUtil;

public class ContactFragment extends Fragment {
	
	private Activity activity;
	
	private UserInfoService userInfoService;
	
	private View view;
	
	private ListView listView;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_info4, container, false);
		listView = (ListView) view.findViewById(R.id.listview);
		return view;
	}
	
	public void showView(){
		userInfoService = SqlLiteUtil.getUserInfoService(activity);
		Collection<UserInfo> userInfos;
		if(HttpClientUtil.getUserInfoMap() != null){
			userInfos = HttpClientUtil.getUserInfoMap().values();
		}else{
			userInfos = userInfoService.findAllInfos();
		}
		if(userInfos != null && !userInfos.isEmpty()){
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			for(UserInfo userInfo:userInfos){
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("name",userInfo.getPassenger_name());
				map.put("info",userInfo.getSex_name()+" "+userInfo.getPassenger_id_type_name()
						+" "+userInfo.getPassenger_id_no());
				list.add(map);
			}
			SimpleAdapter adapter = new SimpleAdapter(activity,list,R.layout.listview,new String[]{"name","info"},
					new int[]{R.id.textView1,R.id.textView2});
			listView.setAdapter(adapter);
		}else{
			LoginDialog.newInstance( "此账号还没有添加联系人！").show(activity.getFragmentManager(),"dialog"); 
		}
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		activity = getActivity();
		Button syncButton = (Button)activity.findViewById(R.id.sync);
		syncButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Map<String,UserInfo> userinfoMap = new HashMap<String, UserInfo>();
				userInfoService.delByAccountName();
				GetPersonConstanct getPersonConstanct = new GetPersonConstanct(activity,userinfoMap,ContactFragment.this);
				getPersonConstanct.execute("");
			}
		});
		showView();
		super.onActivityCreated(savedInstanceState);
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}


	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}


	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}


	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	
}
