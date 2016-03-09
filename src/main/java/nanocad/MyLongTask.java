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

public abstract class MyLongTask {

    public int lengthOfTask;
    public int current = 0;

    public MyLongTask() {
        //Compute length of task...
        lengthOfTask = 1;
    }

    /**
     Called from implemented class to start the task.
     ================================================
     public void go() {
     current = 0;
     final SwingWorker worker = new SwingWorker() {
     public Object construct() {
     return new ActualTask();
     }
     };
     worker.start();
     }
     */

    /**
     Called from parent class to find out how much work needs
     to be done.                                             */
    public int getLengthOfTask() {
        return lengthOfTask;
    }

    /**
     Called from parent class to find out how much has been done.  */
    public int getCurrent() {
        return current;
    }

    /* this entire class is the basis for how timers were
     * used in the original client.  There are still some
     * artificats of this around due to exactly the point
     * you are making here: tight coupling of otherwise
     * unrelated tasks.
     *
     * as of right now, i think the only reason this is here
     * is because mylongtask is primarily used by the login
     * routines.  thus, for whatever reason, when a task is
     * stopped, it should update the client's authenticated
     * status.  this was probably put here rather than in every
     * subclass to reduce code.  i might have done this, i'm
     * not sure...sorry.  anyway, the nanocad issues you're
     * experiencing are due to the sudden decision to bypass
     * our authentication framework in favor of providing
     * nanocad from the main page.  i expect that there
     * will be quite a few more problems due to this decision
     * as time goes by.
     *
     * Rion Dooley Sept 26, 2006.
     */
    public void stop() {

//        LoginPanel.enableButtons(true);  // Why is this here ?

        current = lengthOfTask;
    }

    /**
     Called from parent class to find out if the task has completed.  */
    public boolean done() {
        if (current >= lengthOfTask)
            return true;
        else
            return false;
    }

}// end public abstract class MyLongTask
