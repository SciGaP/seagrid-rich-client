/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.modelling;

import java.io.Reader;

/**
 *
 * @author vvv900
 */
public interface DataParserInterface {

  void parseData(Reader in) throws Exception;

  void parseData(String filename) throws Exception;

  void parseDataAsString(String data) throws Exception;
}
