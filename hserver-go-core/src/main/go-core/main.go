package main

/*
typedef void (*EventCb)(char* event);
static void bridge_event_cb(EventCb cb,char* event)
{
	cb(event);
}
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
