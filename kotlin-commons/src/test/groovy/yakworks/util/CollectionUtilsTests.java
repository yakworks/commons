/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package yakworks.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Rick Evans
 */
class CollectionUtilsTests {

	@Test
	void isEmpty() {
		assertThat(CollectionUtils.isEmpty((Set<Object>) null)).isTrue();
		assertThat(CollectionUtils.isEmpty((Map<String, String>) null)).isTrue();
		assertThat(CollectionUtils.isEmpty(new HashMap<String, String>())).isTrue();
		assertThat(CollectionUtils.isEmpty(new HashSet<>())).isTrue();

		List<Object> list = new ArrayList<>();
		list.add(new Object());
		assertThat(CollectionUtils.isEmpty(list)).isFalse();

		Map<String, String> map = new HashMap<>();
		map.put("foo", "bar");
		assertThat(CollectionUtils.isEmpty(map)).isFalse();
	}

	private static final class Instance {

		private final String name;

		public Instance(String name) {
			this.name = name;
		}

		@Override
		public boolean equals(Object rhs) {
			if (this == rhs) {
				return true;
			}
			if (rhs == null || this.getClass() != rhs.getClass()) {
				return false;
			}
			Instance instance = (Instance) rhs;
			return this.name.equals(instance.name);
		}

		@Override
		public int hashCode() {
			return this.name.hashCode();
		}
	}

}
