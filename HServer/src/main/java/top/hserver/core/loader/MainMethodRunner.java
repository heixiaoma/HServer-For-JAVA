/*
 * Copyright 2012-2013 the original author or authors.
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

package top.hserver.core.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hserver.core.server.util.ExceptionUtil;

import java.lang.reflect.Method;

/**
 * Utility class that used by {@link Launcher}s to call a main method. This class allows
 * methods to be executed within a thread configured with a specific context class loader.
 *
 * @author Phillip Webb
 */
public class MainMethodRunner implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(MainMethodRunner.class);

	private final String mainClassName;

	private final String[] args;

	/**
	 * Create a new {@link MainMethodRunner} instance.
	 * @param mainClass the main class
	 * @param args incoming arguments
	 */
	public MainMethodRunner(String mainClass, String[] args) {
		this.mainClassName = mainClass;
		this.args = (args == null ? null : args.clone());
	}

	@Override
	public void run() {
		try {
			Class<?> mainClass = Thread.currentThread().getContextClassLoader()
					.loadClass(this.mainClassName);
			Method mainMethod = mainClass.getDeclaredMethod("main", String[].class);
			if (mainMethod == null) {
				throw new IllegalStateException(
						this.mainClassName + " does not have a main method");
			}
			mainMethod.setAccessible(true);
			mainMethod.invoke(null, new Object[] { this.args });
		}
		catch (Exception ex) {
			log.error(ExceptionUtil.getMessage(ex));
			System.exit(1);
		}
	}

}
