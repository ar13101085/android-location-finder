package aos.com.maliha.locationfinder.builder;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import aos.com.maliha.locationfinder.httpClient.MalihaHttpClient;
import aos.com.maliha.locationfinder.httpClient.RequestType;

public class MalihaHttpClientBuilder {
    private AsyncHttpResponseHandler responseHandler;
    private RequestParams params;
    private String url;
    private RequestType requestType;

    public MalihaHttpClientBuilder setResponseHandler(AsyncHttpResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
        return this;
    }

    public MalihaHttpClientBuilder setParams(RequestParams params) {
        this.params = params;
        return this;
    }

    public MalihaHttpClientBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public MalihaHttpClientBuilder setRequestType(RequestType requestType) {
        this.requestType = requestType;
        return this;
    }

    public MalihaHttpClient build() {
        return new MalihaHttpClient(responseHandler, params, url, requestType);
    }
}