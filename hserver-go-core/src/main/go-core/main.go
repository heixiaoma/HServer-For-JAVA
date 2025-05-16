package main

/*
#include "./c/start_proxy.c"
*/
import "C"
import (
	"go-core/proxy"
)

func main() {
}

//export StartProxy
func StartProxy(port int, cb C.EventCb) {
	go func() {
		proxy.StartHttpServer(port, func(req string) {
			C.bridge_event_cb(cb, C.CString(req))
		})
	}()
}
