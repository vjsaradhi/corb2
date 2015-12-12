/*
 * Copyright 2005-2015 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The use of the Apache License does not indicate that this project is
 * affiliated with the Apache Software Foundation.
 */
package com.marklogic.developer.corb;

import java.util.Properties;

import com.marklogic.xcc.ContentSource;

public interface UrisLoader {

	public void setOptions(TransformOptions options);

	public void setContentSource(ContentSource cs);

	public void setCollection(String collection);

	public void setProperties(Properties properties);

	public void open() throws CorbException;

	public String getBatchRef();

	public int getTotalCount();

	public boolean hasNext() throws CorbException;

	public String next() throws CorbException;

	public void close();
}
