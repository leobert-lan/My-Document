package com.lht.creationspace.base.keyback;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.vsocyy.clazz.keyback
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> IKeyBackHandlerChain
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2017/1/20.
 */

public interface IKeyBackHandlerChain {
    IKeyBackHandlerChain next(IKeyBackHandler nextHandler);

    boolean onBackPressed();

    class KeyBackHandlerChainImpl implements IKeyBackHandlerChain{
        ArrayList<IKeyBackHandler> keyBackHandlers;

        private KeyBackHandlerChainImpl() {
            keyBackHandlers = new ArrayList<>();
        }

        public static IKeyBackHandlerChain newInstance() {
            return new KeyBackHandlerChainImpl();
        }


        @Override
        public IKeyBackHandlerChain next(@NonNull IKeyBackHandler nextHandler) {
            keyBackHandlers.add(nextHandler);
            return this;
        }


        @Override
        public boolean onBackPressed() {
            for (int i = 0;i<keyBackHandlers.size();i++) {
                boolean hasHandled = keyBackHandlers.get(i).handle();
                if (hasHandled)
                    return true;
            }
            return false;
        }
    }
}
