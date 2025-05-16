package cn.hserver;

import cn.hserver.context.Req;
import jnr.ffi.annotations.Delegate;

public interface CallBack{
        @Delegate
        void Call(String a);
}
