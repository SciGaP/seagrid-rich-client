package cct.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;

/**
 * <p>Title: Jamberoo - Computational Chemistry Toolkit</p>
 *
 * <p>Description: Collection of Computational Chemistry related code</p>
 *
 * <p>Copyright: Copyright (c) 2005-2009 Dr. Vladislav Vasilyev</p>
 *
 * <p>Company: The Australian National University</p>
 *
 * @author Dr. Vladislav Vasilyev
 * @version 1.0
 */

enum DX_KEYWORDS {
  TITLE, XUNITS, YUNITS, XFACTOR, YFACTOR, NPOINTS, DELTAX
}

public class DXData {
  private String title = "No title";
  private double xFactor = 1.0, yFactor = 1.0;
  private int nPoints = 0;
  private double[] x, y;

  public DXData() {
  }

  public int countItems() {
    return nPoints;
  }

  public double[] getXData() {
    return x;
  }

  public double[] getYData() {
    return y;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return this.title;
  }

  public void parseDXFile(String fileName) throws Exception {
    BufferedReader in = new BufferedReader(new FileReader(fileName));
    parseDXFile(in);
    in.close();
  }

  public void parseDXFile(BufferedReader in) throws Exception {
    String line;
    int nLines = 0;
    while ( (line = in.readLine()) != null) {
      line = line.trim();

      if (line.startsWith("##XYDATA=")) {
        break;
      }

      if (line.startsWith("##")) {
        String keyword = line.substring(2, line.indexOf("=")).trim();
        String value = "";
        if (line.indexOf("=") + 1 < line.length()) {
          value = line.substring(line.indexOf("=") + 1).trim();
        }

        DX_KEYWORDS key = null;
        try {
          key = DX_KEYWORDS.valueOf(keyword);
        }
        catch (Exception ex) {
          continue;
        }

        switch (key) {
          case TITLE:
            title = value;
            break;
          case XFACTOR:
            xFactor = Double.parseDouble(value);
            break;
          case YFACTOR:
            yFactor = Double.parseDouble(value);
            break;
          case NPOINTS:
            nLines = Integer.parseInt(value);
            break;

        }
      }
    }

    // --- Read data

    if (nLines < 1) {
      throw new Exception("Didn't find NPOINTS keyword");
    }

    x = new double[nLines];
    y = new double[nLines];
    StringTokenizer st;
    String token;
    int i;
    for (nPoints = 0, i = 0; i < nLines; i++, nPoints++) {
      line = in.readLine();
      if (line == null) {
        System.err.println("Warning: Expected " + nLines + " lines of data, got " + (i + 1));
        break;
      }

      if (line.startsWith("##END")) {
        break;
      }

      st = new StringTokenizer(line, " ");
      if (st.countTokens() < 2) {
        throw new Exception("Expected at least 2 tokens while reading lines of data, got: " + line);
      }

      token = st.nextToken();
      try {
        x[i] = Double.parseDouble(token) * xFactor;
      }
      catch (Exception ex) {
        throw new Exception("Error while parsing X value in line: " + line + " : " + ex.getMessage());
      }

      token = st.nextToken();
      try {
        y[i] = Double.parseDouble(token) * yFactor;
      }
      catch (Exception ex) {
        throw new Exception("Error while parsing Y value in line: " + line + " : " + ex.getMessage());
      }
    }

  }

}
