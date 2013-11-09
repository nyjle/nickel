/*
 * Copyright (c) 2013 Numerate, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nickelproject.util.testUtil;

import java.text.DecimalFormat;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public final class LoggingTestWatcher extends TestWatcher {
    private static final double millisPerSecond = 1000;
    private long mStartTime;

    @Override
    protected void succeeded(final Description description) {
        finished("PASSED", description);
    }

    @Override
    protected void failed(final Throwable e, final Description description) {
        finished("FAILED", description);
    }

    private void finished(final String pPrefix, final Description description) {
        final double vDurationSec = (System.currentTimeMillis() - mStartTime) / millisPerSecond;
        System.err.println("[TEST " + pPrefix + " " + new DecimalFormat("0.000").format(vDurationSec) + "s] "
                + description.getDisplayName());
    }

    @Override
    protected void starting(final Description description) {
        System.err.println("\n[TEST START] " + description.getDisplayName());
        mStartTime = System.currentTimeMillis();
    }
}
