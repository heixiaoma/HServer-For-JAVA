package context

import (
	"encoding/json"
	"io"
	"net/http"
)

type Req struct {
	Method     string            `json:"method"`
	URL        string            `json:"url"`
	Header     http.Header       `json:"header"`
	Host       string            `json:"host"`
	RequestURI string            `json:"request_uri"`
	RemoteAddr string            `json:"remote_addr"`
	Query      map[string]string `json:"query"`
	Body       interface{}       `json:"body"`
}

// RequestToJSON 将 http.Request 转换为 JSON 格式
func RequestToJSON(r *http.Request) ([]byte, error) {
	// 提取 URL 参数
	query := make(map[string]string)
	for key, values := range r.URL.Query() {
		if len(values) > 0 {
			query[key] = values[0]
		}
	}

	// 读取请求体
	var body interface{}
	if r.Body != nil {
		bodyBytes, err := io.ReadAll(r.Body)
		if err != nil {
			return nil, err
		}
		defer r.Body.Close()

		// 尝试解析为 JSON
		var jsonBody map[string]interface{}
		if err := json.Unmarshal(bodyBytes, &jsonBody); err == nil {
			body = jsonBody
		} else {
			// 若不是 JSON，尝试解析为表单数据
			if err := r.ParseForm(); err == nil {
				formData := make(map[string]string)
				for key, values := range r.PostForm {
					if len(values) > 0 {
						formData[key] = values[0]
					}
				}
				body = formData
			} else {
				// 若都不是，将原始数据作为字符串处理
				body = string(bodyBytes)
			}
		}
	}

	// 填充 RequestInfo 结构体
	info := Req{
		Method:     r.Method,
		URL:        r.URL.String(),
		Header:     r.Header,
		Host:       r.Host,
		RequestURI: r.RequestURI,
		RemoteAddr: r.RemoteAddr,
		Query:      query,
		Body:       body,
	}

	// 将结构体转换为 JSON 字节切片
	return json.MarshalIndent(info, "", "  ")
}
