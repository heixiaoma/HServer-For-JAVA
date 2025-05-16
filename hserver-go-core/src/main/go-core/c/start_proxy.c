typedef void (*EventCb)(char* event);
static void bridge_event_cb(EventCb cb,char* event)
{
	cb(event);
}