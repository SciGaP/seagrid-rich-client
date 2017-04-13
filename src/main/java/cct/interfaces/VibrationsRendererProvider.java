package cct.interfaces;

/**
 * <p>Title: Computational Chemistry Tookit</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
public interface VibrationsRendererProvider {

   int getMinimumDuration();

   int getMaximumDuration();

   int getDuration();

   void setDuration(int duration);

   void animateVibrations(int frequency, boolean show);

   void showDisplacementVectors(int frequency, boolean show);

   void saveScene(int frequency, String format) throws Exception;
}
