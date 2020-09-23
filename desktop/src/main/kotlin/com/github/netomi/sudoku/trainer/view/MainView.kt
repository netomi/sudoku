/*
 * Sudoku creator / solver / teacher.
 *
 * Copyright (c) 2020 Thomas Neidhart
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

// Workaround for https://youtrack.jetbrains.com/issue/KT-35343
@file:Suppress("JAVA_MODULE_DOES_NOT_READ_UNNAMED_MODULE")

package com.github.netomi.sudoku.trainer.view

import com.github.netomi.sudoku.model.PredefinedType
import com.github.netomi.sudoku.solver.DifficultyLevel
import com.github.netomi.sudoku.solver.GridRater
import com.github.netomi.sudoku.solver.Hint
import com.github.netomi.sudoku.trainer.controller.GridController
import com.github.netomi.sudoku.trainer.model.DisplayOptions
import com.github.netomi.sudoku.trainer.model.SudokuLibrary
import com.github.netomi.sudoku.trainer.model.TechniqueCategory
import com.github.netomi.sudoku.trainer.model.TechniqueCategoryOrLibraryEntry
import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.beans.binding.Bindings
import javafx.beans.binding.When
import javafx.beans.property.SimpleDoubleProperty
import javafx.collections.FXCollections
import javafx.css.PseudoClass
import javafx.geometry.Insets
import javafx.geometry.Side
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Priority
import org.controlsfx.control.StatusBar
import tornadofx.*
import tornadofx.controlsfx.statusbar


class MainView : View("Sudoku Trainer") {
    private val gridController: GridController by inject()

    private val gridView: GridView by inject()

    private val filterToggleGroup = ToggleGroup()

    private lateinit var hintListView:     ListView<Hint>
    private lateinit var gridTypeComboBox: ComboBox<PredefinedType>
    private lateinit var statusBar:        StatusBar

    override val root =
        vbox {
            menubar {
                menu("File") {
                    item("New sudoku").action {
                        log.info("Creating new sudoku")
                    }
                    separator()
                    item("Exit").action {
                        log.info("Exiting application")
                        Platform.exit()
                    }
                }
                menu("Edit") {
                    item("Paste values").action {
                        gridController.loadModelFromClipboard()
                    }
                }
                menu("Help") {
                    item("About...")
                }
            }

            hbox {
                padding = Insets(2.0, 2.0, 2.0, 2.0)
                spacing = 2.0

                togglebutton("1", filterToggleGroup) {
                    isFocusTraversable = false
                    userData = 1
                    setMinSize(40.0, 40.0)
                }
                togglebutton("2", filterToggleGroup) {
                    isFocusTraversable = false
                    userData = 2
                    setMinSize(40.0, 40.0)
                }
                togglebutton("3", filterToggleGroup) {
                    isFocusTraversable = false
                    userData = 3
                    setMinSize(40.0, 40.0)
                }
                togglebutton("4", filterToggleGroup) {
                    isFocusTraversable = false
                    userData = 4
                    setMinSize(40.0, 40.0)
                }
                togglebutton("5", filterToggleGroup) {
                    isFocusTraversable = false
                    userData = 5
                    setMinSize(40.0, 40.0)
                }
                togglebutton("6", filterToggleGroup) {
                    isFocusTraversable = false
                    userData = 6
                    setMinSize(40.0, 40.0)
                }
                togglebutton("7", filterToggleGroup) {
                    isFocusTraversable = false
                    userData = 7
                    setMinSize(40.0, 40.0)
                }
                togglebutton("8", filterToggleGroup) {
                    isFocusTraversable = false
                    userData = 8
                    setMinSize(40.0, 40.0)
                }
                togglebutton("9", filterToggleGroup) {
                    isFocusTraversable = false
                    userData = 9
                    setMinSize(40.0, 40.0)
                }
            }

            borderpane {
                useMaxSize = true

                center = gridView.root

                bottom = statusbar {
                    statusBar = this
                    text = ""
                }

                right = drawer(side = Side.RIGHT, multiselect = true) {
                    item("Layout", expanded = false) {
                        form {
                            fieldset("Layout settings") {
                                field("Grid Layout") {
                                    combobox<PredefinedType> {
                                        gridTypeComboBox = this

                                        items = FXCollections.observableArrayList(*PredefinedType.values())
                                        selectionModel.select(PredefinedType.CLASSIC_9x9)
                                    }
                                }

                                button("Reset") {
                                    action {
                                        gridController.resetModel(
                                            gridTypeComboBox.selectedItem
                                                ?: PredefinedType.CLASSIC_9x9
                                        )
                                    }
                                }
                            }

                            fieldset("Display settings") {
                                field("Show pencil marks") {
                                    checkbox {
                                        selectedProperty().set(true)
                                        selectedProperty().bindBidirectional(DisplayOptions.showPencilMarksProperty)
                                        action {
                                            gridView.refreshView()
                                        }
                                    }
                                }
                                field("Show computed values") {
                                    disableProperty().bind(DisplayOptions.showPencilMarksProperty.not())

                                    checkbox {
                                        selectedProperty().set(true)
                                        selectedProperty().bindBidirectional(DisplayOptions.showComputedValuesProperty)
                                        disableProperty().bind(DisplayOptions.showPencilMarksProperty.not())

                                        action {
                                            gridView.refreshView()
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item("Solver", expanded = true) {
                        vbox {
                            minWidth = 350.0

                            hbox {
                                padding = Insets(2.0, 2.0, 2.0, 2.0)
                                spacing = 4.0

                                button("Full") {
                                    action { gridController.findHints() }
                                }

                                button("Single Step") {
                                    action { gridController.findHintsSingleStep() }
                                }

                            }

                            listview<Hint> {
                                hintListView = this

                                useMaxSize = true
                                vgrow      = Priority.ALWAYS

                                contextmenu {
                                    item("Apply").action {
                                        selectedItem?.apply {
                                            gridController.applyHint(this)
                                            gridView.refreshView()
                                        }
                                    }
                                    item("Apply upto").action {
                                        selectedItem?.apply {
                                            val index = this@listview.selectionModel.selectedIndex
                                            gridController.applyHints(0, index)
                                            gridView.refreshView()
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item("Library", expanded = true) {
                        treeview<TechniqueCategoryOrLibraryEntry> {
                            minWidth = 350.0

                            root = TreeItem(TechniqueCategory.All)

                            cellFormat { text = it.toDisplayString() }

                            populate { parent ->
                                val node = parent.value
                                return@populate if (node.isCategory()) {
                                    val category = node as TechniqueCategory

                                    if (category.hasSubCategories()) {
                                        category.subCategories()
                                    } else {
                                        SudokuLibrary.entries[category]
                                    }
                                } else {
                                    null
                                }
                            }

                            onUserSelect {
                                it.getLibraryEntry()?.apply {
                                    gridController.loadModel(this)
                                }
                            }
                        }
                    }
                }
            }
        }

    override fun onBeforeShow() {
        gridController.loadModel()
    }

    private fun initializeFontSizeManager() {
        // Cf. https://stackoverflow.com/questions/13246211/javafx-how-to-get-stage-from-controller-during-initialization
        root.sceneProperty().addListener(ChangeListener { _, oldScene: Scene?, newScene: Scene? ->
            if (oldScene == null && newScene != null) {
                val fontSize = SimpleDoubleProperty(0.0)
                fontSize.bind(
                    newScene.widthProperty().add(newScene.heightProperty())
                        .divide(1400 + 900)
                        .multiply(100)
                )
                gridView.root.styleProperty()
                    .bind(Bindings.concat("-fx-font-size: ", fontSize.asString("%.0f")).concat("%;"))
            }
        })
    }

    init {
        hintListView.items = gridController.hintList

        hintListView.selectionModel.selectedItemProperty().addListener { _, _, newValue: Hint? ->
            gridController.hintProperty.set(newValue)
            gridView.refreshView()
        }

        hintListView.setCellFactory { lv ->
            val selectionModel = lv.selectionModel
            val cell = ListCell<Hint>()

            cell.textProperty().bind(
                When(cell.itemProperty().isNotNull).then(cell.itemProperty().asString()).otherwise(
                    ""
                )
            )
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED) { event ->
                if (event.isPrimaryButtonDown && !cell.isEmpty) {
                    val index = cell.index
                    if (selectionModel.selectedIndices.contains(index) && lv.isFocused) {
                        selectionModel.clearSelection(index)
                    } else {
                        selectionModel.select(index)
                    }
                    lv.requestFocus()
                    event.consume()
                }
            }

            val difficultyListener: InvalidationListener = InvalidationListener {
                for (difficultyLevel in DifficultyLevel.values()) {
                    // use pseudo classes to style list cells based on hint difficulty
                    val pseudoClass = PseudoClass.getPseudoClass(difficultyLevel.name.toLowerCase())

                    val active = cell.item != null &&
                                 cell.item.solvingTechnique.difficultyLevel == difficultyLevel

                    cell.pseudoClassStateChanged(pseudoClass, active)
                }
            }

            cell.itemProperty().addListener(difficultyListener)

            cell
        }

        filterToggleGroup.selectToggle(null)
        filterToggleGroup.selectedToggleProperty().addListener { _, _, newValue: Toggle? ->
            val filterValue = newValue?.let { newValue.userData as Int } ?: 0
            DisplayOptions.possibleValueFilterProperty.set(filterValue)
            gridView.refreshView()
        }

        gridController.modelProperty.onChange { grid ->
            grid?.apply {
                runAsync {
                    GridRater.rate(this@apply)
                } ui {
                    statusBar.text = "%s (%d)".format(it.first.toString().toLowerCase().capitalize(), it.second)
                }
            }
        }

        initializeFontSizeManager()
    }
}