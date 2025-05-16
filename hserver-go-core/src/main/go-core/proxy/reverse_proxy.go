package proxy

import (
	"go-core/context"
	"net/http"
	"net/http/httputil"
	"net/url"
	"time"
)

func NewProxy(ip, port, host string) *httputil.ReverseProxy {
	target, err := url.Parse("http://" + ip + ":" + port)
	if err != nil {
		return nil
	}
	proxy := httputil.NewSingleHostReverseProxy(target)
	proxy.ErrorHandler = func(w http.ResponseWriter, r *http.Request, err error) {
		w.WriteHeader(http.StatusBadGateway)
	}
	proxy.Transport = &http.Transport{
		MaxIdleConns:        1000,
		MaxIdleConnsPerHost: 500,
		IdleConnTimeout:     30 * time.Second,
	}
	proxy.ModifyResponse = func(response *http.Response) error {
		return nil
	}
	//log.Println("化代理地址:" + host)
	return proxy
}

func Handler(w http.ResponseWriter, r *http.Request, req func(req string)) {
	host := r.Host
	json, err := context.RequestToJSON(r)
	if err == nil {
		req(string(json))
	}
	proxy := NewProxy("127.0.0.1", "8888", host)
	if proxy == nil {
		http.Error(w, "错误URL地址", http.StatusInternalServerError)
		return
	}
	proxy.ServeHTTP(w, r)
}
