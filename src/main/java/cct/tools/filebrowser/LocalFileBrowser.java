/* ***** BEGIN LICENSE BLOCK *****
   Version: Apache 2.0/GPL 3.0/LGPL 3.0

   CCT - Computational Chemistry Tools
   Jamberoo - Java Molecules Editor

   Copyright 2008-2015 Dr. Vladislav Vasilyev

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Contributor(s):
     Dr. Vladislav Vasilyev <vvv900@gmail.com>       (original author)

  Alternatively, the contents of this file may be used under the terms of
  either the GNU General Public License Version 2 or later (the "GPL"), or
  the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  in which case the provisions of the GPL or the LGPL are applicable instead
  of those above. If you wish to allow use of your version of this file only
  under the terms of either the GPL or the LGPL, and not to allow others to
  use your version of this file under the terms of the Apache 2.0, indicate your
  decision by deleting the provisions above and replace them with the notice
  and other provisions required by the GPL or the LGPL. If you do not delete
  the provisions above, a recipient may use your version of this file under
  the terms of any one of the Apache 2.0, the GPL or the LGPL.

 ***** END LICENSE BLOCK *****/

package cct.tools.filebrowser;

import java.awt.Component;

import javax.swing.JTable;

import cct.interfaces.FileBrowserGUIInterface;
import cct.interfaces.FileBrowserInterface;
import cct.tools.FileFilterImpl;

import com.sshtools.j2ssh.FileTransferProgress;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class LocalFileBrowser
    implements FileBrowserInterface {

   JTable table = new JTable(5, 5);
   FileBrowserGUIInterface topGUI = null;

   public LocalFileBrowser() {
   }

   @Override
  public String[] getFileNames() {
      return null;
   }

   @Override
  public int getFileCount() {
      return 0;
   }

   @Override
  public void listUsingFilePattern(String pattern) throws Exception {

   }

   @Override
  public Component getComponent() {
      return table;
   }

   @Override
  public void setSelectionInfoInterface(SelectionInfoInterface selection_info) {

   }

   @Override
  public void get(String remote, String local) throws Exception {

   }

   @Override
  public void get(String remote, String local, FileTransferProgress progress) throws Exception {

   }

   @Override
  public void setFileFilter(FileFilterImpl filter) {

   }

   @Override
  public void disconnect() {

   }

   @Override
  public String[] getSelectedFiles() {
      return null;
   }

   @Override
  public void downloadSelected() {

   }

   @Override
  public String pwd() {
      return null;
   }

   @Override
  public String[] getSelectedFolders() {
      return null;
   }

   @Override
  public void setSelectionMode(int mode) {

   }

   @Override
  public void setTopGUI(FileBrowserGUIInterface guiInterface) {
      topGUI = guiInterface;
   }

   @Override
  public void connect(String hostname) throws Exception {

   }

   @Override
  public void connect(String hostname, String username, char[] password,
                       int port) throws Exception {

   }

   @Override
  public boolean isAuthenticated() {
      return false;
   }

   @Override
  public void mkdir() {

   }

   @Override
  public void mkdir(String dir) {

   }

   @Override
  public void cd(String dir) {

   }

   @Override
  public void refresh() {

   }

   @Override
  public void removeSelectedPaths() {

   }

   @Override
  public void rm(String path) {

   }

   @Override
  public void upDirectory() {

   }

   public static void main(String[] args) {
      LocalFileBrowser localfilebrowser = new LocalFileBrowser();
   }
}
