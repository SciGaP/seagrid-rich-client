/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/
package org.seagrid.desktop.home;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Callback;
import org.apache.airavata.model.error.AiravataClientException;
import org.apache.airavata.model.experiment.ExperimentSearchFields;
import org.apache.airavata.model.workspace.Project;
import org.seagrid.desktop.apis.airavata.AiravataManager;
import org.seagrid.desktop.experiment.list.ExperimentListController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/** Controls the home screen */
public class HomeController {

    @FXML
    private Button breadcrumbButton;

    @FXML
    private AnchorPane mainContentPane;

    @FXML
    private ListView<Project> projectsListView;

    ObservableList observableProjectList = FXCollections.observableArrayList();

    private static final double ARROW_WIDTH = 50;

    private static final double ARROW_HEIGHT = 20;

    public void initialize() {
        projectsListView.setCellFactory(new Callback<ListView<Project>, ListCell<Project>>() {
            @Override
            public ListCell<Project> call(ListView<Project> param) {
                ListCell<Project> cell = new ListCell<Project>() {
                    @Override
                    protected void updateItem(Project t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t.getName());
                        }
                    }
                };
                return cell;
            }
        });

        projectsListView.setOnMouseClicked(event -> {
            updateExperimentList();
        });

        updateProjectList();
        updateExperimentList();
//        updateBreadCrumbs();
    }

    //update the breadcrumbs
    public void updateBreadCrumbs(){
        // build the following shape
//   --------
//  \         \
//  /         /
//   --------
        Path path = new Path();

// begin in the upper left corner
        MoveTo e1 = new MoveTo(0, 0);

// draw a horizontal line that defines the width of the shape
        HLineTo e2 = new HLineTo();
// bind the width of the shape to the width of the button
        e2.xProperty().bind(breadcrumbButton.widthProperty().subtract(ARROW_WIDTH));

// draw upper part of right arrow
        LineTo e3 = new LineTo();
// the x endpoint of this line depends on the x property of line e2
        e3.xProperty().bind(e2.xProperty().add(ARROW_WIDTH));
        e3.setY(ARROW_HEIGHT / 2.0);

// draw lower part of right arrow
        LineTo e4 = new LineTo();
// the x endpoint of this line depends on the x property of line e2
        e4.xProperty().bind(e2.xProperty());
        e4.setY(ARROW_HEIGHT);

// draw lower horizontal line
        HLineTo e5 = new HLineTo(0);

// draw lower part of left arrow
        LineTo e6 = new LineTo(ARROW_WIDTH, ARROW_HEIGHT / 2.0);

// close path
        ClosePath e7 = new ClosePath();

        path.getElements().addAll(e1, e2, e3, e4, e5, e6, e7);
// this is a dummy color to fill the shape, it won't be visible
        path.setFill(Color.BLACK);

// set path as button shape
        breadcrumbButton.setClip(path);
    }

    //update the left pane with project list
    public void updateProjectList(){
        try {
            List<Project> projectList = AiravataManager.getInstance().getProjects();
            observableProjectList = FXCollections.observableArrayList();
            observableProjectList.add(new Project("$$$$$$$$","dummy-user","last 100 experiments"));
            observableProjectList.addAll(projectList);
            projectsListView.setItems(observableProjectList);
            projectsListView.getSelectionModel().select(0);
        } catch (AiravataClientException e) {
            e.printStackTrace();
        }
    }

    //updates the right pane with experiment list
    public void updateExperimentList(){
        try {
            Project project = projectsListView.getSelectionModel().getSelectedItem();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../experiment/list/experiment-list.fxml"));
            Node experimentList = (Node)loader.load();
            ExperimentListController controller = loader.getController();
            mainContentPane.getChildren().setAll(experimentList);
            HashMap<ExperimentSearchFields,String> filters = new HashMap<>();
            filters.put(ExperimentSearchFields.PROJECT_ID, project.getProjectID());
            if(project.getProjectID().startsWith("$$$$")){
                controller.updateExperimentList(new HashMap<>(), 100, 0);
            }else{
                controller.updateExperimentList(filters, -1, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}