/*
 * Copyright (C) 2017 The Android Open Source Project
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
package com.android.tools.idea.gradle.structure.model;

import com.android.tools.idea.gradle.structure.configurables.PsContext;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static com.android.tools.idea.gradle.structure.model.PsPath.TexType.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PsModulePathTest {

  private PsModulePath myModulePath;
  @Mock private PsModule myModule;
  @Mock private PsContext myContext;

  @Before
  public void before() {
    initMocks(this);
    when(myModule.getName()).thenReturn("TestModuleName");
    myModulePath = new PsModulePath(myModule);
  }

  @Test
  public void getModuleName() throws Exception {
    assertEquals("TestModuleName", myModulePath.getModuleName());
  }

  @Test
  public void toText() throws Exception {
    assertEquals("TestModuleName", myModulePath.toText(FOR_COMPARE_TO));
    assertEquals("TestModuleName", myModulePath.toText(PLAIN_TEXT));
    assertEquals("", myModulePath.getHtml(myContext));
  }

  @Test
  public void equalsAndHashCode() {
    EqualsVerifier
      .forClass(PsModulePath.class)
      .withPrefabValues(PsPath.class, new TestPath("a"), new TestPath("b"))
      .withRedefinedSuperclass()
      .verify();
  }
}