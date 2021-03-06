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
package com.android.tools.idea.resourceExplorer.view

import com.android.tools.idea.resourceExplorer.model.DesignAssetSet
import com.android.tools.idea.resourceExplorer.viewmodel.ResourceBrowserViewModel
import com.intellij.util.ui.UIUtil
import java.awt.BorderLayout
import java.awt.Image
import javax.swing.*

/**
 * A JList that display [DesignAssetSet].
 */
class DesignAssetsList(private val browserViewModel: ResourceBrowserViewModel)
  : JList<DesignAssetSet>(browserViewModel.designAssetListModel)
{

  init {
    layoutOrientation = JList.HORIZONTAL_WRAP
    visibleRowCount = 0
    fixedCellWidth = 100
    cellRenderer = ListCellRenderer<DesignAssetSet>
    { list, assetSet, index, isSelected, cellHasFocus ->
      getDesignAssetView(
          browserViewModel.getSourcePreview(assetSet.getHighestDensityAsset()),
          assetSet.name,
          isSelected
      )
    }
  }

  private fun getDesignAssetView(
      preview: Image,
      name: String,
      selected: Boolean = false

  ): JPanel {
    val panel = JPanel(BorderLayout())
    panel.border = if (selected) {
      BorderFactory.createCompoundBorder(
          BorderFactory.createEmptyBorder(2, 2, 2, 2),
          BorderFactory.createLineBorder(UIUtil.getListSelectionBackground(), 2, true))
    }
    else {
      BorderFactory.createEmptyBorder(4, 4, 4, 4)
    }

    val icon = JLabel(ImageIcon(preview))
    icon.border = BorderFactory.createEmptyBorder(18, 18, 18, 18)
    panel.add(icon)
    panel.add(JLabel(name, JLabel.CENTER), BorderLayout.SOUTH)
    return panel
  }
}