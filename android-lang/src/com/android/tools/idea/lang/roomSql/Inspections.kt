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
package com.android.tools.idea.lang.roomSql

import com.android.tools.idea.lang.roomSql.psi.*
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.PsiTreeUtil

/**
 * Inspection for the RoomSql language that only does something when running on a PSI file that's injected into Room's `@Query`.
 */
abstract class RoomQueryOnlyInspection : LocalInspectionTool() {

  override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession): PsiElementVisitor {
    return if (isThisRoomQuery(session)) super.buildVisitor(holder, isOnTheFly, session) else PsiElementVisitor.EMPTY_VISITOR
  }

  private fun isThisRoomQuery(session: LocalInspectionToolSession) = (session.file as? RoomSqlFile)?.queryAnnotation != null
}

class RoomUnresolvedReferenceInspection : RoomQueryOnlyInspection() {

  override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
    return object : RoomVisitor() {

      override fun visitColumnName(columnName: RoomColumnName) {
        super.visitColumnName(columnName)
        checkReference(columnName)
      }

      override fun visitDefinedTableName(definedTableName: RoomDefinedTableName) {
        super.visitDefinedTableName(definedTableName)
        checkReference(definedTableName)
      }

      override fun visitSelectedTableName(selectedTableName: RoomSelectedTableName) {
        super.visitSelectedTableName(selectedTableName)
        checkReference(selectedTableName)
      }

      override fun visitBindParameter(bindParameter: RoomBindParameter) {
        super.visitBindParameter(bindParameter)
        checkReference(bindParameter)
      }

      private fun checkReference(referenceElement: PsiElement) {
        // Make sure we're inside Room's @Query annotation, otherwise we don't know the schema.
        if ((referenceElement.containingFile as? RoomSqlFile)?.queryAnnotation == null) return

        if (!(isWellUnderstood(PsiTreeUtil.findPrevParent(referenceElement.containingFile, referenceElement)))) return

        val reference = referenceElement.reference ?: return
        if (reference.resolve() == null) holder.registerProblem(reference)
      }

      /**
       * Checks if we have understand the given query type enough to highlight unresolved references.
       */
      private fun isWellUnderstood(stmt: PsiElement): Boolean = when (stmt) {
        is RoomSelectStatement, is RoomUpdateStatement, is RoomInsertStatement, is RoomDeleteStatement, is RoomWithClauseStatement -> true
        else -> false
      }
    }
  }
}

class RoomBindParameterSyntaxInspection : RoomQueryOnlyInspection() {
  override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
    return object : RoomVisitor() {
      override fun visitBindParameter(parameter: RoomBindParameter) {
        if (!parameter.isColonNamedParameter) {
          holder.registerProblem(parameter, "Room only supports named parameters with a leading colon, e.g. :argName.")
        }
      }
    }
  }
}
