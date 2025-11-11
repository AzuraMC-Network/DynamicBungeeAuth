package net.uraharanz.plugins.dynamicbungeeauth.utils.callback;

public interface CallbackMET<Reply> {
    void done(Reply var1);

    void error(Exception var1);
}
