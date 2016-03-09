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
package nanocad;

import legacy.editor.commons.Settings;

import javax.swing.*;
import java.io.File;
/**
 * @author Xiaohai Li
 *
 * @see nanocadFrame2
 * @see MyLongTask
 * NanocadTask subclasses MyLongTask which uses a SwingWorker to
 * perform a time-consuming task.
 *
 */
class NanocadTask extends MyLongTask implements MyLongTaskInterface {

    public static boolean DEBUG = true;
    private JFrame frame;

    public NanocadTask(JFrame jf) {
        super();
        frame = jf;
    }

    public void go() {
        current = 0; // from MyLongTask
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                return new ActualTask();
            }
        };
        worker.start();
    }

}

/************************************************************
 Inner class ActualTask is need for the implementation of
 a subclass of MyLongTask.  It cannot be made static b/c of
 the design of MyLongTask
 */
final class ActualTask {
    ActualTask () {
        //Check if molecular database is available on local machine
        if (!((new File(Settings.defaultDirStr + Settings.fileSeparator + "common")).exists()))
        {
            //nanocadFrame2.progressLabel.setText("Retrieve Molecule Database Progress");
            String zipFileName =  Settings.getApplicationDataDir() + Settings.fileSeparator + "nanocad" + File.separator
                    + "nanocaddata.zip";
            if (!((new File(zipFileName)).exists()))
            {
                nanocadFrame2.progressLabel.setText("Progress: Retrieve Molecule database from remote system...");
                GetDataFile gf = new GetDataFile(zipFileName);
            }
            //nanocadFrame2.progressLabel.setText("Progress: unzipping molecule database");
            //ZipExtractor uz = new ZipExtractor(zipFileName);
            //System.err.println("Done with unzipping nanocaddata.zip");
        }

        //Check if txt files needed by nanocad is available on local machine
        if (!((new File(Settings.defaultDirStr + Settings.fileSeparator + "txt")).exists()))
        {
            //nanocadFrame2.progressLabel.setText("Retrieve .txt files Progress");
            String zipFileName = Settings.getApplicationDataDir() + Settings.fileSeparator + "nanocad" + File.separator
                    + "txt.zip";
            if (!((new File(zipFileName)).exists()))
            {
                nanocadFrame2.progressLabel.setText("Progress: Retrieve .txt files from remote system...");
                GetDataFile gf = new GetDataFile(zipFileName);
            }
            //nanocadFrame2.progressLabel.setText("Done Retrieving files");
            //nanocadFrame2.progressLabel.setText("Progress: unzipping .txt files");
            //ZipExtractor uz = new ZipExtractor(zipFileName);
            //System.err.println("Done with unzipping txt.zip");
        }

        nanocadFrame2.nanocadTask.stop();
        System.err.println("nanocadTask is done");

    }
}
