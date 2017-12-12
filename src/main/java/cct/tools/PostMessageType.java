package cct.tools;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public enum PostMessageType {
   SUGGESTION, BUG_REPORT;

   static final String suggestionKey = "suggest";
   static final String bugKey = "bug";

   PostMessageType() { }

   /*
   public String getKey() {
      switch (this) {
         case SUGGESTION:
            return suggestionKey;
         case BUG_REPORT:
            return bugKey;
      }
      return null;
   }
   */
}
