package com.ice.restring.shadow

import android.os.AsyncTask

import org.robolectric.annotation.Implements
import org.robolectric.shadows.ShadowAsyncTask

import java.util.concurrent.Executor

@Implements(AsyncTask<*, *, *>::class)
class MyShadowAsyncTask<Params, Progress, Result> : ShadowAsyncTask<Params, Progress, Result>() {

    override fun executeOnExecutor(executor: Executor, vararg params: Params): AsyncTask<Params, Progress, Result> {
        return super.execute(*params)
    }
}
