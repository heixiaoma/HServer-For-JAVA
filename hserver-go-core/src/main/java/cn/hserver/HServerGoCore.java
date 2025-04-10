package cn.hserver;

import jnr.ffi.annotations.Delegate;

public interface HServerGoCore {

    void StartProxy(int port,CallBack call);

}