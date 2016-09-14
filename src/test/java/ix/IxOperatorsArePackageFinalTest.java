/*
 * Copyright 2011-2016 David Karnok
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ix;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;

import org.junit.*;

public class IxOperatorsArePackageFinalTest {

    @Test
    public void checkOperatorsFinal() throws Exception {

        URL u = IxOperatorsArePackageFinalTest.class.getResource(IxOperatorsArePackageFinalTest.class.getSimpleName() + ".class");

        File f = new File(u.toURI());

        File g = f.getParentFile();

        File[] files = g.listFiles();

        StringBuilder b = new StringBuilder();

        if (files != null && files.length != 0) {
            for (File h : files) {
                if (h.getName().contains("Test")
                        || h.getName().contains("Perf")
                        || h.getName().contains("$")) {
                    continue;
                }

                Class<?> clazz = Class.forName("ix." + h.getName().replace(".class", ""));

                if ((clazz.getModifiers() & Modifier.FINAL) == 0
                        && (clazz.getModifiers() & Modifier.ABSTRACT) == 0
                        && (clazz.getModifiers() & Modifier.INTERFACE) == 0) {
                    b.append("java.lang.RuntimeException: " + h.getName() + " is not final\r\n")
                    .append(" at ").append(clazz.getName()).append(" (").append(h.getName()).append(":1)\r\n\r\n");
                }
            }
        }

        if (b.length() != 0) {
            System.out.println(b);

            Assert.fail(b.toString());
        }
    }
}
