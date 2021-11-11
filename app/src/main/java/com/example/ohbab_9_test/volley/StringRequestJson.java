package com.example.ohbab_9_test.volley;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.example.ohbab_9_test.template.core.App;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.ErrorListener;

public class StringRequestJson extends com.android.volley.toolbox.StringRequest {

    Context context;
    String encoding;
    String url;
    boolean noCache = false;
    public StringRequestJson(String url, Map param, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(param == null? Method.GET : Method.POST,  url, listener, errorListener);
        encoding = "utf-8";
        this.url = url;
        this.context = VolleyInstance.getContext();
        this.param = param;
        setRetryPolicy(VolleyInstance.getInstance().getPolicy());
    }
    Map<String, String> param = new HashMap<String, String>();
    @Override
    public Map<String, String> getParams() throws AuthFailureError {
        return param;
    }

    public void setParam(Map<String, String> param) {
        this.param = param;
    }

    public void putParam(String key, String value)
    {
        param.put(key, value);
    }

    byte[] body;

    @Override
    public byte[] getBody() throws AuthFailureError {
        if(body != null)
            return body;
        return super.getBody();
    }

    public void setBody(String body) {
        if(body != null && body.length() > 0)
            this.body = body.getBytes();
    }

    Map<String, String> header = new HashMap<>();
    public Map<String, String> getHeader(){
        return header;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.android.volley.Request#getHeaders()
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        header.put("Accept", "application/json");
        header.put("Content-Type", "application/json");
        header.put("x-access-token", App.getInstance().getLoginToken());

        return header;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, encoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            parsed = new String(response.data);
        }
        return Response.success(parsed, null);
    }

}