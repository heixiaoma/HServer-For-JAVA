package cn.hserver;

import jnr.ffi.annotations.Delegate;

public interface CallBack{
        @Delegate
        void Call(String a);
    }
