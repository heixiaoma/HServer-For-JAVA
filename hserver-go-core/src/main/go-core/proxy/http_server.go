package proxy

import (
	"net/http"
	"strconv"
)

func StartHttpServer(port int, req func(req string)) {
	mux := http.NewServeMux()
	mux.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		Handler(w, r, req)
	})
	err := http.ListenAndServe(":"+strconv.Itoa(port), mux)
	if err != nil {
		return
	}
}
