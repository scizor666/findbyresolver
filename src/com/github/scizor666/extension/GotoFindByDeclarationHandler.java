/*
 * Copyright 2000-2017 JetBrains s.r.o.
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
 */
package findbyresolver.src.com.github.scizor666.extension;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Created by atepliashin on 4/18/17.
 */
public class GotoFindByDeclarationHandler implements GotoDeclarationHandler {

  public static final String FIND_BY_ANNOTATION = "some.package.with.custom.FindBy";

  @Nullable
  @Override
  public PsiElement[] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
    if (AnnotationUtil.isInsideAnnotation(sourceElement)) {
      PsiAnnotation annotation = getAnnotation(sourceElement);
      if (annotation.getQualifiedName() != null && annotation.getQualifiedName().equals(FIND_BY_ANNOTATION)) {
        return isClassAnnotation(annotation) ? getJsonPageElements(sourceElement) : getJsonPageFieldElements(sourceElement);
      }
    }
    return null;
  }

  @NotNull
  private PsiElement[] getJsonPageElements(@Nullable PsiElement sourceElement) {
    PsiFile[] files = FilenameIndex.getFilesByName(sourceElement.getProject(),
                                                   sourceElement.getText().replaceAll("\"", "") + ".json",
                                                   GlobalSearchScope.allScope(sourceElement.getProject()));
    PsiElement[] elements = new PsiElement[files.length];
    for (int i = 0; i < elements.length; i++) {
      elements[i] = files[i].getNavigationElement();
    }
    return elements;
  }

  @NotNull
  private PsiElement[] getJsonPageFieldElements(@Nullable PsiElement sourceElement) {
    return null;
  }

  @NotNull
  private static boolean isClassAnnotation(PsiAnnotation annotation) {
    return true;
  }

  @Nullable
  private static PsiAnnotation getAnnotation(PsiElement element) {
    for (int level = 0; level < 8; level++) {
      if (element instanceof PsiAnnotation) {
        return (PsiAnnotation)element;
      }
      element = element.getParent();
    }
    return null;
  }

  @Nullable
  @Override
  public String getActionText(DataContext context) {
    return null;
  }
}
