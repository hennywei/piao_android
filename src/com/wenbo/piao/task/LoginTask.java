package com.wenbo.piao.task;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSONObject;
import com.wenbo.androidpiao.R;
import com.wenbo.piao.activity.UserActivity;
import com.wenbo.piao.enums.UrlEnum;
import com.wenbo.piao.util.HttpClientUtil;
import com.wenbo.piao.util.JsoupUtil;

/**
 * 登录task
 * @author wenbo
 *
 */
public class LoginTask extends AsyncTask<String,Integer,Integer> {
	
	private HttpClient httpClient;
	
	private Activity activity;
	
	private ProgressBar progressBar = null;
	
	public LoginTask(Activity activity){
		this.httpClient = HttpClientUtil.getHttpClient();
		this.activity = activity;
		progressBar = (ProgressBar)activity.findViewById(R.id.progressBar1);
	}

	@Override
	protected Integer doInBackground(String... arg0) {
		return getLoginRand();
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onCancelled(Integer result) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onPostExecute(Integer result) {
		switch (result) {
		case 0:
			Log.i("Login","登录成功!");
			Intent intent = new Intent();
            intent.setClass(activity,UserActivity.class);
			activity.startActivity(intent);
			activity.finish();
			break;
		case 1:
			Log.w("Login","用户名不存在!");
			break;
		case 2:
			Log.w("Login","密码错误!");
			break;
		case 3:
			Log.w("Login","验证码错误!");
			break;
		default:
			Log.w("Login","系统错误");
			break;
		}
		progressBar.setVisibility(View.GONE);
	}

	@Override
	protected void onPreExecute() {
		progressBar.setVisibility(View.VISIBLE);
		Log.i("Login","开始准备登录...");
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		Log.i("Login","正在登录...");
	}
	
	/**
	 * 获取登录码
	 */
	private int getLoginRand() {
		HttpResponse response = null;
		try {
			HttpGet httpget = HttpClientUtil.getHttpGet(UrlEnum.LOGIN_INIT_URL);
			httpget.getParams().setParameter("method","loginAysnSuggest");
			response = httpClient.execute(httpget);
			if (response.getStatusLine().getStatusCode() == 200) {
				String str = EntityUtils.toString(response.getEntity());
				JSONObject object = JSONObject.parseObject(str);
				Log.i("Login",object.getString("loginRand")+":"+object.getString("randError"));
				return login(object.getString("loginRand"),
						object.getString("randError"));
			}else if(response.getStatusLine().getStatusCode() == 404){
				Log.w("Login","404");
				return 4;
			}
		} catch (Exception e) {
			Log.e("Login","获取登录随机码失败!",e);
			return 4;
		} finally {

		}
		return 0;
	}
	
	private int login(String loginRand, String randError) {
		HttpResponse response = null;
		// 获取验证码
		try {
			EditText userNameEditText = (EditText)activity.findViewById(R.id.editText1);
			EditText passwordEditText = (EditText)activity.findViewById(R.id.editText2);
			EditText rangCodeEditText = (EditText)activity.findViewById(R.id.editText3);
			String userName = userNameEditText.getText().toString();
			String password = passwordEditText.getText().toString();
			String randCode = rangCodeEditText.getText().toString();
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("method", "login"));
			parameters.add(new BasicNameValuePair("loginRand", loginRand));
			parameters.add(new BasicNameValuePair("refundLogin", "N"));
			parameters.add(new BasicNameValuePair("refundFlag", "Y"));
			parameters.add(new BasicNameValuePair("loginUser.user_name",userName));
			parameters.add(new BasicNameValuePair("nameErrorFocus", ""));
			parameters.add(new BasicNameValuePair("user.password",password));
			parameters.add(new BasicNameValuePair("randCode", randCode));
			parameters.add(new BasicNameValuePair("randErrorFocus", ""));
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,
					"UTF-8");
			URI uri = new URI(UrlEnum.DO_MAIN.getPath()+UrlEnum.LONGIN_CONFIM.getPath());
			HttpPost httpPost = HttpClientUtil.getHttpPost(uri,
					UrlEnum.LONGIN_CONFIM);
			httpPost.setEntity(uef);
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 302) {
			} else if (response.getStatusLine().getStatusCode() == 404) {
			} else if (response.getStatusLine().getStatusCode() == 200) {
				String info = EntityUtils.toString(response.getEntity());
				Document document = Jsoup.parse(info);
				// 判断登录状态
				if(StringUtils.contains(info,"系统维护中")){
					Log.i("Login","系统维护中，请明天订票!");
					return 4;
				}else{
					return JsoupUtil.validateLogin(document);
				}
			}
		}catch (Exception e) {
			Log.i("Login","登录出错!",e);
		}
		return 4;
	}

}