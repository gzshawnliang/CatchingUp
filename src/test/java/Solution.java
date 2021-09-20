import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;


class MBFS
{
    final int nl = 10;

    private  class Mpoint
    {
        public int y;
        public int x;

        public Mpoint(int my, int mx)
        {
            y = my; x = mx;
        }

        public boolean isVali()
        {
            return y >= 0 && y <= nl - 1 && x >= 0 && x <= nl - 1;
        }
    }

    private  class Element
    {
        public int d;
        Mpoint mp;

        public Element(int md, int my, int mx)
        {
            d = md;
            mp = new Mpoint(my, mx);
        }

        public Element(int md, Mpoint mmp)
        {
            d = md;
            mp = mmp;
        }
    }

    final  int infD = nl * nl * 2;

    private int[] dy = {-1, 1, 0, 0};
    private int[] dx = {0, 0, -1, 1};
    public ArrayList<Character> getPath(int sttY, int sttX, int endY, int endX, char [][] mp)
    {
        Mpoint f[][] = new Mpoint[nl][nl];
        for (int y = 0; y <= nl - 1; ++y)
            for (int x = 0; x <= nl - 1; ++x)
                f[y][x] = new Mpoint(-1, -1);

        int[][] dp = new int[nl][nl];
        for (int y = 0; y <= nl - 1; ++y)
            for (int x = 0; x <= nl - 1; ++x)
                dp[y][x] = infD;

        f[sttY][sttX] = new Mpoint(sttY, sttX);
        dp[sttY][sttX] = 0;

        Queue<Element> pq = new PriorityQueue<Element>(
                new Comparator<Element>() {
                    public int compare(Element e1, Element e2) {
                        return e1.d - e2.d;
                    }
                });
        pq.offer(new Element(0, sttY, sttX));
        while (!pq.isEmpty())
        {
            Element topE = pq.peek(); pq.poll();

            if (dp[topE.mp.y][topE.mp.x] < topE.d) continue;
            dp[topE.mp.y][topE.mp.x] = topE.d;

            for (int p = 0; p <= 3; ++p)
            {
                Element nxtE = new Element(topE.d + 1, topE.mp.y + dy[p], topE.mp.x + dx[p]);
                if (nxtE.mp.isVali())
                {
                    if (mp[nxtE.mp.y][nxtE.mp.x] != '*')
                    {
                        if (dp[nxtE.mp.y][nxtE.mp.x] > nxtE.d)
                        {
                            dp[nxtE.mp.y][nxtE.mp.x] = nxtE.d;
                            f[nxtE.mp.y][nxtE.mp.x] = topE.mp;
                            pq.offer(nxtE);
                        }
                    }
                }
            }
        }

        ArrayList<Character> ans = new ArrayList<Character>();

        if (dp[endY][endX] == infD)
            return ans;

        int lstY = endY, lstX = endX;
        while (true)
        {
            int nowY = f[lstY][lstX].y, nowX = f[lstY][lstX].x;
            if (nowY == lstY && nowX == lstX) break;

            if (nowY == lstY)
            {
                if (nowX == lstX - 1) ans.add('R');
                else                  ans.add('L');
            }
            else
            {
                if (nowY == lstY - 1) ans.add('D');
                else                  ans.add('U');
            }

            lstY = nowY; lstX = nowX;
        }

        Collections.reverse(ans);

        return ans;
    }
}


public class Solution {

    final static int nl = 10;


    public static void main(String[] args) throws IOException
    {
        //test data
        /*
         **********
         *----P---*
         *--------*
         *--------*
         *-*****--*
         *-----E--*
         *--------*
         *--------*
         *------S-*
         **********
        * */

        //System.setIn(new FileInputStream(new File("C:\\code\\codingame\\codingame-template\\src\\test\\java\\test.in")));
        //System.setOut(new PrintStream(new File("test.in")));
        Scanner sc = new Scanner(System.in);

        //int MaxCD = sc.nextInt();
        int MaxCD = Integer.parseInt(sc.nextLine());
//        System.err.println(MaxCD);


        int eneY = -1, eneX = -1, plrY = -1, plrX = -1;
        char [][] mp = new char[nl][nl];
        for (int y = 0; y <= nl - 1; ++y)
        {
            //System.err.println("OK");
            String tmpS = sc.nextLine();
//            if(tmpS.isEmpty())
//                System.err.println("OK2");
//            else
//                System.err.println("OK3");

            for (int x = 0; x <= nl - 1; ++x)
            {
                mp[y][x] = tmpS.charAt(x);

                if (mp[y][x] == 'P')
                {
                    plrY = y; plrX = x;
                }
                else if (mp[y][x] == 'E')
                {
                    eneY = y; eneX = x;
                }
            }
        }

        while (true)
        {
            eneY = sc.nextInt(); eneX = sc.nextInt();

            MBFS SolveTurn = new MBFS();
            ArrayList<Character> nowPth = SolveTurn.getPath(plrY, plrX, eneY, eneX, mp);

            char nxtD = nowPth.get(0);

            if (nxtD == 'L' || nxtD == 'l')
            {
                --plrX;
            }
            else if (nxtD == 'R' || nxtD == 'r')
            {
                ++plrX;
            }
            else if (nxtD == 'U' || nxtD == 'u')
            {
                --plrY;
            }
            else
            {
                ++plrY;
            }

            System.out.println(nxtD);
        }
    }
}



