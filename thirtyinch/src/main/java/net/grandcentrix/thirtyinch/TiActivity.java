package net.grandcentrix.thirtyinch;

import net.grandcentrix.thirtyinch.internal.InterceptableViewBinder;
import net.grandcentrix.thirtyinch.internal.PresenterNonConfigurationInstance;
import net.grandcentrix.thirtyinch.internal.TiActivityDelegate;
import net.grandcentrix.thirtyinch.internal.TiActivityRetainedPresenterProvider;
import net.grandcentrix.thirtyinch.internal.TiAppCompatActivityProvider;
import net.grandcentrix.thirtyinch.internal.TiPresenterLogger;
import net.grandcentrix.thirtyinch.internal.TiPresenterProvider;
import net.grandcentrix.thirtyinch.internal.TiViewProvider;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

/**
 * Created by pascalwelsch on 9/8/15.
 */
public abstract class TiActivity<P extends TiPresenter<V>, V extends TiView>
        extends AppCompatActivity implements TiPresenterProvider<P>, TiViewProvider<V>,
        TiActivityRetainedPresenterProvider<P>, TiAppCompatActivityProvider, TiPresenterLogger,
        InterceptableViewBinder<V> {

    private final String TAG = this.getClass().getSimpleName()
            + ":" + TiActivity.class.getSimpleName()
            + "@" + Integer.toHexString(this.hashCode());

    private final TiActivityDelegate<P, V> mDelegate
            = new TiActivityDelegate<>(this, this, this, this, this);

    @NonNull
    @Override
    public Removable addBindViewInterceptor(final TiBindViewInterceptor interceptor) {
        return mDelegate.addBindViewInterceptor(interceptor);
    }

    @NonNull
    @Override
    public AppCompatActivity getAppCompatActivity() {
        return this;
    }

    @Nullable
    @Override
    public V getInterceptedViewOf(final TiBindViewInterceptor interceptor) {
        return mDelegate.getInterceptedViewOf(interceptor);
    }

    @NonNull
    @Override
    public List<TiBindViewInterceptor> getInterceptors(
            final Filter<TiBindViewInterceptor> predicate) {
        return mDelegate.getInterceptors(predicate);
    }

    @Nullable
    public P getRetainedPresenter() {
        // try recover presenter via lastNonConfigurationInstance
        // this works most of the time
        final Object nci = getLastCustomNonConfigurationInstance();
        if (nci instanceof PresenterNonConfigurationInstance) {
            final PresenterNonConfigurationInstance pnci = (PresenterNonConfigurationInstance) nci;
            //noinspection unchecked
            return (P) pnci.getPresenter();
        }
        return null;
    }

    /**
     * Invalidates the cache of the latest bound view. Forces the next binding of the view to run
     * through all the interceptors (again).
     */
    @Override
    public void invalidateView() {
        mDelegate.invalidateView();
    }

    @Override
    public void logTiMessages(final String msg) {
        Log.v(TAG, msg);
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDelegate.onConfigurationChanged_afterSuper(newConfig);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return new PresenterNonConfigurationInstance<>(mDelegate.getPresenter(),
                super.onRetainCustomNonConfigurationInstance());
    }

    /**
     * the default implementation assumes that the activity is the view and implements the {@link
     * TiView} interface. Override this method for a different behaviour.
     *
     * @return the object implementing the TiView interface
     */
    @NonNull
    @Override
    public V provideView() {
        return mDelegate.provideView();
    }

    @Override
    public String toString() {
        String presenter = mDelegate.getPresenter() == null ? "null" :
                mDelegate.getPresenter().getClass().getSimpleName()
                        + "@" + Integer.toHexString(mDelegate.getPresenter().hashCode());

        return getClass().getSimpleName()
                + ":" + TiActivity.class.getSimpleName()
                + "@" + Integer.toHexString(hashCode())
                + "{presenter=" + presenter + "}";
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDelegate.onCreate_afterSuper(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDelegate.onDestroy_afterSuper();
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        mDelegate.onSaveInstanceState_afterSuper(outState);
    }

    @Override
    protected void onStart() {
        mDelegate.onStart_beforeSuper();
        super.onStart();
        mDelegate.onStart_afterSuper();
    }

    @Override
    protected void onStop() {
        mDelegate.onStop_beforeSuper();
        super.onStop();
        mDelegate.onStop_afterSuper();
    }
}
