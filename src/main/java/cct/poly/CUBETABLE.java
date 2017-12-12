package cct.poly;

import java.util.ArrayList;

/**
 * <p>Title: Molecular Structure Viewer/Editor</p>
 *
 * <p>Description: Computational Chemistry Toolkit</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class CUBETABLE {

   /**** Cubical Polygonization (optional) ****/


   final static int LB = 0; /* left bottom edge	*/
   final static int LT = 1; /* left top edge	*/
   final static int LN = 2; /* left near edge	*/
   final static int LF = 3; /* left far edge	*/
   final static int RB = 4; /* right bottom edge */
   final static int RT = 5; /* right top edge	*/
   final static int RN = 6; /* right near edge	*/
   final static int RF = 7; /* right far edge	*/
   final static int BN = 8; /* bottom near edge	*/
   final static int BF = 9; /* bottom far edge	*/
   final static int TN = 10; /* top near edge	*/
   final static int TF = 11; /* top far edge	*/

   static int L = 0; /* left direction:	-x, -i */
   static int R = 1; /* right direction:	+x, +i */
   static int B = 2; /* bottom direction: -y, -j */
   static int T = 3; /* top direction:	+y, +j */
   static int N = 4; /* near direction:	-z, -k */
   static int F = 5; /* far direction:	+z, +k */
   static int LBN = 0; /* left bottom near corner  */
   static int LBF = 1; /* left bottom far corner   */
   static int LTN = 2; /* left top near corner     */
   static int LTF = 3; /* left top far corner      */
   static int RBN = 4; /* right bottom near corner */
   static int RBF = 5; /* right bottom far corner  */
   static int RTN = 6; /* right top near corner    */
   static int RTF = 7; /* right top far corner     */

   /*			edge: LB, LT, LN, LF, RB, RT, RN, RF, BN, BF, TN, TF */
   public static final int corner1[] = {
       LBN, LTN, LBN, LBF, RBN, RTN, RBN, RBF, LBN, LBF, LTN, LTF};
   public static final int corner2[] = {
       LBF, LTF, LTN, LTF, RBF, RTF, RTN, RTF, RBN, RBF, RTN, RTF};
   static int leftface[] = {
       B, L, L, F, R, T, N, R, N, B, T, F};
   /* face on left when going corner1 to corner2 */
   static int rightface[] = {
       L, T, N, L, B, R, R, F, B, F, N, T};
   /* face on right when going corner1 to corner2 */


   //vector<INTLISTS> ctable;
   static ArrayList ctable = new ArrayList(256);

   public CUBETABLE() {
      int i, e, c, done[] = new int[12], pos[] = new int[8];
      for (i = 0; i < 256; i++) {
         for (e = 0; e < 12; e++) {
            done[e] = 0;
         }
         for (c = 0; c < 8; c++) {
            pos[c] = BIT(i, c);
         }
         for (e = 0; e < 12; e++) {
            //if (!done[e] && (pos[corner1[e]] != pos[corner2[e]])) {
            if (done[e] == 0 && (pos[corner1[e]] != pos[corner2[e]])) {
               INTLIST ints = new INTLIST();
               int start = e, edge = e;

               /* get face that is to right of edge from pos to neg corner: */
               //int face = pos[corner1[e]] ? rightface[e] : leftface[e];
               int face = pos[corner1[e]] != 0 ? rightface[e] : leftface[e];
               while (true) {
                  edge = nextcwedge(edge, face);
                  done[edge] = 1;
                  if (pos[corner1[edge]] != pos[corner2[edge]]) {
                     ints.push_front(edge);
                     if (edge == start) {
                        break;
                     }
                     face = otherface(edge, face);
                  }
               }
               //ctable[i].push_front(ints);
               ctable.add(0, ints);
            }
         }
      }
   }

   static INTLISTS get_lists(int i) {
      //return (INTLISTS) ctable[i];
      return (INTLISTS) ctable.get(i);
   }

   public static INTLISTS get_cubetable_entry(int i) {
      //static CUBETABLE c;
      //return c.get_lists(i);
      return get_lists(i);
   }

   int BIT(int i, int bit) {
      return (i >> bit) & 1;
   }

   /* nextcwedge: return next clockwise edge from given edge around given face */

   int nextcwedge(int edge, int face) {
      switch (edge) {
         case LB:
            return (face == L) ? LF : BN;
         case LT:
            return (face == L) ? LN : TF;
         case LN:
            return (face == L) ? LB : TN;
         case LF:
            return (face == L) ? LT : BF;
         case RB:
            return (face == R) ? RN : BF;
         case RT:
            return (face == R) ? RF : TN;
         case RN:
            return (face == R) ? RT : BN;
         case RF:
            return (face == R) ? RB : TF;
         case BN:
            return (face == B) ? RB : LN;
         case BF:
            return (face == B) ? LB : RF;
         case TN:
            return (face == T) ? LT : RN;
         case TF:
            return (face == T) ? RT : LF;
      }
      System.err.println("nextcwedge: should not be here...");
      return 0;
   }

   /* otherface: return face adjoining edge that is not the given face */

   int otherface(int edge, int face) {
      int other = leftface[edge];
      return face == other ? rightface[edge] : other;
   }

}
