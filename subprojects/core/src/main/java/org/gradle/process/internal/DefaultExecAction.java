/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.process.internal;

import org.gradle.initialization.BuildCancellationToken;
import org.gradle.internal.file.PathToFileResolver;
import org.gradle.process.ExecResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.concurrent.Executor;

/**
 * Use {@link ExecActionFactory} or {@link DslExecActionFactory} instead.
 */
public class DefaultExecAction extends DefaultExecHandleBuilder implements ExecAction {
    public DefaultExecAction(PathToFileResolver fileResolver, Executor executor, BuildCancellationToken buildCancellationToken) {
        super(fileResolver, executor, buildCancellationToken);
    }

    @Override
    public ExecResult execute() {
        ExecHandle execHandle = build();
        long t0 = System.currentTimeMillis();
        ExecResult execResult = execHandle.start().waitForFinish();
        try {
            if (System.getenv("EXEC_ACTION_LOG") != null) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(System.getenv("EXEC_ACTION_LOG"), true));
                writer.write("Exec action in thread " + Thread.currentThread().getId() + " " + System.getenv("ITERATION") + " costs " + (System.currentTimeMillis() - t0) + " ms\n");
                writer.close();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        if (!isIgnoreExitValue()) {
            execResult.assertNormalExitValue();
        }
        return execResult;
    }
}
