/*
 * Copyright 2012-2015 the original author or authors.
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

package cn.hserver.plugin.loader;

import java.net.URL;

/**
 * A strategy for detecting Java agents.
 *
 * @author Andy Wilkinson
 * @since 1.1.0
 */
public interface JavaAgentDetector {

	/**
	 * Returns {@code true} if {@code url} points to a Java agent jar file, otherwise
	 * {@code false} is returned.
	 * @param url The url to examine
	 * @return if the URL points to a Java agent
	 */
	boolean isJavaAgentJar(URL url);

}
