package aos.com.maliha.locationfinder.httpClient;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.client.HttpClient;

public class MalihaHttpClient {
    private AsyncHttpClient client;
    private AsyncHttpResponseHandler responseHandler;
    private RequestParams params;
    private String url;
    RequestType requestType;

    public MalihaHttpClient(AsyncHttpResponseHandler responseHandler, RequestParams params, String url, RequestType requestType) {
        this.responseHandler = responseHandler;
        this.params = params;
        this.url = url;
        this.requestType = requestType;
        excute();
    }

    private void excute(){
        client=new AsyncHttpClient();
        if(requestType==RequestType.GET){
            client.get(url,responseHandler);
        }else if(requestType==RequestType.POST){
            client.post(url,params,responseHandler);
        }
    }

}
