package com.codingame.game;

import java.util.*;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Random;
//import java.util.Queue;
//import java.util.PriorityQueue;
//import java.util.stream.Collectors;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.SoloGameManager;
import com.codingame.gameengine.module.entities.*;
import com.google.inject.Inject;

public class Referee extends AbstractReferee {
    @Inject private SoloGameManager<Player> gameManager;
    @Inject private GraphicEntityModule graphicEntityModule;

    public static final int nl = 10, bl = 80, ancY = -360;
    public static final int eYG = 10, eXG = 10;
    public static final int iniYG = (World.DEFAULT_HEIGHT - (bl * nl + (nl - 1) * eYG)) / 2 + bl / 2 + 50,
                            iniXG = (World.DEFAULT_WIDTH - (bl * nl + (nl - 1) * eXG)) / 2 + bl / 2;

    //public static final int iniYG = 0 + bl / 2,
    //                        iniXG = 0 + bl / 2;

    public int SEED = 0;

    public char[][] wmapC;

    private Sprite[][] wint;

    private int[][] mapVis;

    private int plrY = -1, plrX = -1, eneY = -1, eneX = -1;
    private char plrDT = '-', eneDT = '-';

    Sprite PlrR, EneR;

    private void refreshGrid()
    {
        mapVis[plrY][plrX] = 2;
        if (mapVis[eneY][eneX] != 2)
            mapVis[eneY][eneX] = 1;

        for (int y = 0; y <= nl - 1; ++y)
        {
            for (int x = 0; x <= nl - 1; ++x)
            {
                if (mapVis[y][x] == 2)      wint[y][x].setImage("FloorPlr.png");
                else if (mapVis[y][x] == 1) wint[y][x].setImage("FloorEne.png");
            }
        }
    }

    private void setImPlr(char ch)
    {
        if (ch == 'L' || ch == 'l')
        {
            PlrR.setImage("Pro_LR.png");
            PlrR.setScaleX(1, Curve.IMMEDIATE);
        }
        else if (ch == 'R' || ch == 'r')
        {
            PlrR.setImage("Pro_LR.png");
            PlrR.setScaleX(-1, Curve.IMMEDIATE);
        }
        else if (ch == 'U' || ch == 'u')
        {
            PlrR.setImage("Pro_U.png");
            //PlrR.setScaleX(1, Curve.IMMEDIATE);
        }
        else
        {
            PlrR.setImage("Pro_D.png");
            //PlrR.setScaleX(1, Curve.IMMEDIATE);
        }
    }

    private void setImEne(char ch)
    {
        if (ch == 'L' || ch == 'l')
        {
            EneR.setImage("Ant_LR.png");
            EneR.setScaleX(1, Curve.IMMEDIATE);
        }
        else if (ch == 'R' || ch == 'r')
        {
            EneR.setImage("Ant_LR.png");
            EneR.setScaleX(-1, Curve.IMMEDIATE);
        }
        else if (ch == 'U' || ch == 'u')
        {
            EneR.setImage("Ant_U.png");
            //EneR.setScaleX(1, Curve.IMMEDIATE);
        }
        else
        {
            EneR.setImage("Ant_D.png");
            //EneR.setScaleX(1, Curve.IMMEDIATE);
        }
    }

    private void movPlr(char ch)
    {
        setImPlr(ch);
        graphicEntityModule.commitEntityState(0.0, PlrR);

        if (ch == 'L' || ch == 'l')
        {
            --plrX;
        }
        else if (ch == 'R' || ch == 'r')
        {
            ++plrX;
        }
        else if (ch == 'U' || ch == 'u')
        {
            --plrY;
        }
        else
        {
            ++plrY;
        }

        PlrR.setY(iniYG + plrY * (bl + eYG), Curve.EASE_IN_AND_OUT);
        PlrR.setX(iniXG + plrX * (bl + eXG), Curve.EASE_IN_AND_OUT);
        PlrR.setZIndex(plrY * 2 + 1);
        graphicEntityModule.commitEntityState(0.5, PlrR);
    }

    private void movEne(char ch)
    {
        //graphicEntityModule.commitEntityState(0.1, EneR);
        setImEne(ch);
        graphicEntityModule.commitEntityState(0.5, EneR);

        if (ch == 'L' || ch == 'l')
        {
            --eneX;
        }
        else if (ch == 'R' || ch == 'r')
        {
            ++eneX;
        }
        else if (ch == 'U' || ch == 'u')
        {
            --eneY;
        }
        else
        {
            ++eneY;
        }

        EneR.setY(iniYG + eneY * (bl + eYG), Curve.EASE_IN_AND_OUT);
        EneR.setX(iniXG + eneX * (bl + eXG), Curve.EASE_IN_AND_OUT);
        EneR.setZIndex(eneY * 2 + 1);
        graphicEntityModule.commitEntityState(1, EneR);
    }


    int MaxCD = 0;
    int nowCD = 1;

    @Override
    public void init()
    {
        nowCD = 1;

        gameManager.setFrameDuration(1000);
        gameManager.setMaxTurns(100);

        List<String> testInput = gameManager.getTestCaseInput();

        MaxCD = Integer.parseInt(testInput.get(0));

        PlrR = graphicEntityModule.createSprite()
                .setImage("Pro_LR.png")
                .setScale(1)
                .setVisible(true)
                .setAnchorX(0.5)
                .setAnchorY(0.9);

        EneR =  graphicEntityModule.createSprite()
                .setImage("Ant_LR.png")
                .setScale(1)
                .setVisible(true)
                .setAnchorX(0.5)
                .setAnchorY(0.9);

        mapVis = new int[nl][nl];
        for (int y = 0; y <= nl - 1; ++y)
            for (int x = 0; x <= nl - 1; ++x)
                mapVis[y][x] = 0;

        wmapC = new char[nl][nl];
        wint = new Sprite[nl][nl];
        for (int y = 0; y <= nl - 1; ++y)
        {
            for (int x = 0; x <= nl - 1; ++x)
            {
                wmapC[y][x] = testInput.get(y + 1).charAt(x);
                wint[y][x] = graphicEntityModule.createSprite()
                            .setY(iniYG + y * (bl + eYG))
                            .setX(iniXG + x * (bl + eXG))
                            .setZIndex(y * 2)
                            .setScale(1)
                            .setVisible(true);
                wint[y][x].setAnchorX(0.5);
                wint[y][x].setAnchorY(0.9);
                if (wmapC[y][x] == '*')
                {
                    wint[y][x].setImage("WallShort.png");
                    wint[y][x].setAlpha(1.0);
                }
                else
                {
                    wint[y][x].setImage("Floor.png");

                    if (wmapC[y][x] == 'L' || wmapC[y][x] == 'R' || wmapC[y][x] == 'U' || wmapC[y][x] == 'D')
                    {
                        plrY = y; plrX = x; plrDT = wmapC[y][x];
                    }
                    else if (wmapC[y][x] == 'l' || wmapC[y][x] == 'r' || wmapC[y][x] == 'u' || wmapC[y][x] == 'd')
                    {
                        eneY = y; eneX = x; eneDT = wmapC[y][x];
                    }
                }
            }
        }

        gameManager.getPlayer().sendInputLine(Integer.toString(MaxCD));
        for (int y = 0; y <= nl - 1; ++y)
        {
            String tmpS2 = "";
            for (int x = 0; x <= nl - 1; ++x)
            {
                char ch = wmapC[y][x];

                if (ch == '*')
                {

                }
                else
                {
                    if (ch == 'L' || ch == 'R' || ch == 'U' || ch == 'D')
                    {
                        ch = 'P';
                    }
                    else if (ch == 'l' || ch == 'r' || ch == 'u' || ch == 'd')
                    {
                        ch = 'E';
                    }
                }

                tmpS2 += ch;
            }

            gameManager.getPlayer().sendInputLine(tmpS2);
        }

        PlrR.setY(iniYG + plrY * (bl + eYG)).setX(iniXG + plrX * (bl + eXG)).setZIndex(plrY * 2 + 1);
        EneR.setY(iniYG + eneY * (bl + eYG)).setX(iniXG + eneX * (bl + eXG)).setZIndex(eneY * 2 + 1);

        setImPlr(plrDT); setImEne(eneDT);

        SEED = Integer.parseInt(testInput.get(testInput.size() - 1));

//        int iniLSX = iniXG + bl / 2 + eXG / 2, iniLSY = iniYG + bl / 2 + eYG / 2;
//        for (int c = -1; c <= nl - 1; ++c)
//        {
//            graphicEntityModule.createLine()
//                               .setX(iniLSX + c * (bl + eXG)).setY(iniLSY + (-1) * (bl + eYG))
//                               .setX2(iniLSX + c * (bl + eXG)).setY2(iniLSY + (nl - 1) * (bl + eYG))
//                               .setLineColor(0x1BFE00)
//                               .setVisible(true)
//                               .setLineWidth(3)
//                               .setAlpha(0.4)
//                               .setZIndex(1000000);
//        }
//        for (int c = -1; c <= nl - 1; ++c)
//        {
//            graphicEntityModule.createLine()
//                    .setX(iniLSX + (-1) * (bl + eXG)).setY(iniLSY + c * (bl + eYG))
//                    .setX2(iniLSX + (nl - 1) * (bl + eXG)).setY2(iniLSY + c * (bl + eYG))
//                    .setLineColor(0x1BFE00)
//                    .setVisible(true)
//                    .setLineWidth(3)
//                    .setAlpha(0.4)
//                    .setZIndex(1000000);
//        }
        refreshGrid();
    }

    //private boolean eneCanMov = false;

    private class Mpoint
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

    private class Element
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

    final int infD = nl * nl * 2;

    private int[] dy = {-1, 1, 0, 0};
    private int[] dx = {0, 0, -1, 1};
    final private char[] allPs = {'U', 'D', 'L', 'R'};
    private ArrayList<Character> getPath(int sttY, int sttX, int endY, int endX)
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
                    if (wmapC[nxtE.mp.y][nxtE.mp.x] != '*')
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

    String tmpS = "-";
    @Override
    public void gameTurn(int turn)
    {
        tmpS = "";
        tmpS += Integer.toString(eneY);
        tmpS += " ";
        tmpS += Integer.toString(eneX);

        gameManager.getPlayer().sendInputLine(tmpS);

        Random rn = new Random(SEED);
        gameManager.getPlayer().execute();

        char plrStps;

        try {
//            int a = gameManager.getPlayer().getExpectedOutputLines();
            List<String> outputs = gameManager.getPlayer().getOutputs();

            if (outputs.size() != 1)
            {
                gameManager.loseGame("Invalid Input");
                return;
            }
            else if (outputs.get(0).length() != 1)
            {
                gameManager.loseGame("Invalid Input");
                return;
            }

            char nowIn = outputs.get(0).charAt(0);
            if (nowIn == 'L' || nowIn == 'R' || nowIn == 'U' || nowIn == 'D' || nowIn == 'l' || nowIn == 'r' || nowIn == 'u' || nowIn == 'd')
            {
                plrStps = nowIn;
            }
            else
            {
                gameManager.loseGame("Invalid Input");
                return;
            }

        } catch (TimeoutException e) {
            gameManager.loseGame("Timeout");
            return;
        }

        movPlr(plrStps);

        Mpoint tmptestPLrOB = new Mpoint(plrY, plrX);

        if (tmptestPLrOB.isVali() == false)
        {
            Sprite Cover
                    = graphicEntityModule.createSprite()
                    .setY(0)
                    .setX(0)
                    .setZIndex(Integer.MAX_VALUE)
                    .setScale(1)
                    .setImage("LOSE_FALL.png")
                    .setVisible(true);
            graphicEntityModule.commitEntityState(1, Cover);

            graphicEntityModule.createRectangle()
                    .setLineWidth(0)
                    .setFillColor(0xd4efdf)
                    .setZIndex(Integer.MAX_VALUE-5)
                    .setWidth(Constants.VIEWER_WIDTH)
                    .setHeight(96)
                    .setAlpha(0.25)
                    .setX(0)
                    .setY(Constants.VIEWER_HEIGHT/2-96/2);

            graphicEntityModule.createText("Fall")
                    .setStrokeThickness(2) // Adding an outline
                    .setStrokeColor(0xffffff) // a white outline
                    .setFontSize(75)
                    .setFillColor(0xd1f2eb) // Setting the text color to black
                    .setX(Constants.VIEWER_WIDTH/2,Curve.EASE_IN_AND_OUT)
                    .setY(Constants.VIEWER_HEIGHT/2,Curve.EASE_IN_AND_OUT)
                    .setAnchor(0.5)
                    .setZIndex(Integer.MAX_VALUE);

            graphicEntityModule.commitEntityState(1, Cover);

            gameManager.loseGame("Fall");
            return;
        }
        else if (wmapC[plrY][plrX] == '*')
        {
            Sprite Cover
                     = graphicEntityModule.createSprite()
                    .setY(0)
                    .setX(0)
                    .setZIndex(Integer.MAX_VALUE)
                    .setScale(1)
                    .setImage("LOSE_HITWALL.png")
                    .setVisible(true);
            graphicEntityModule.commitEntityState(0.9, Cover);

            graphicEntityModule.createRectangle()
                    .setLineWidth(0)
                    .setFillColor(0xd4efdf)
                    .setZIndex(Integer.MAX_VALUE-5)
                    .setWidth(Constants.VIEWER_WIDTH)
                    .setHeight(96)
                    .setAlpha(0.25)
                    .setX(0)
                    .setY(Constants.VIEWER_HEIGHT/2-96/2);

            graphicEntityModule.createText("Hit Wall")
                    .setStrokeThickness(2) // Adding an outline
                    .setStrokeColor(0xffffff) // a white outline
                    .setFontSize(75)
                    .setFillColor(0xd1f2eb) // Setting the text color to black
                    .setX(Constants.VIEWER_WIDTH/2,Curve.EASE_IN_AND_OUT)
                    .setY(Constants.VIEWER_HEIGHT/2,Curve.EASE_IN_AND_OUT)
                    .setAnchor(0.5)
                    .setZIndex(Integer.MAX_VALUE);

            graphicEntityModule.commitEntityState(1, Cover);

            gameManager.loseGame("HitWall");
            return;
        }

        int flg = -1;
        for (int p = 0; p <= 3; ++p)
        {
            int tmpPlrY = plrY + dy[p], tmpPlrX = plrX + dx[p];
            Mpoint tmpEneP = new Mpoint(tmpPlrY, tmpPlrX);

            if (tmpEneP.isVali())
            {
                if (wmapC[tmpPlrY][tmpPlrX] != '*')
                {
                    if (tmpPlrY == eneY && tmpPlrX == eneX)
                    {
                        flg = p;
                        break;
                    }
                }
            }
        }

        if (flg >= 0)
        {
            refreshGrid();

            setImPlr(allPs[flg]);

            Sprite Cover
                    = graphicEntityModule.createSprite()
                    .setY(0)
                    .setX(0)
                    .setZIndex(Integer.MAX_VALUE-10)
                    .setScale(1)
                    .setImage("WIN.png")
                    .setVisible(true);
            graphicEntityModule.commitEntityState(0.9, Cover);


            graphicEntityModule.createRectangle()
                    .setLineWidth(0)
                    .setFillColor(0xd4efdf)
                    .setZIndex(Integer.MAX_VALUE-5)
                    .setWidth(Constants.VIEWER_WIDTH)
                    .setHeight(96)
                    .setAlpha(0.25)
                    .setX(0)
                    .setY(Constants.VIEWER_HEIGHT/2-96/2);

            graphicEntityModule.createText("You Catch Up")
                    .setStrokeThickness(2) // Adding an outline
                    .setStrokeColor(0xffffff) // a white outline
                    .setFontSize(75)
                    .setFillColor(0xd1f2eb) // Setting the text color to black
                    .setX(Constants.VIEWER_WIDTH/2,Curve.EASE_IN_AND_OUT)
                    .setY(Constants.VIEWER_HEIGHT/2,Curve.EASE_IN_AND_OUT)
                    .setAnchor(0.5)
                    .setZIndex(Integer.MAX_VALUE)
                    ;

            gameManager.winGame("You Catch Up");
            return;
        }

        if (MaxCD != nowCD)
        {
            ++nowCD;
        }
        else
        {
            nowCD = 1;

            int maxD = getPath(plrY, plrX, eneY, eneX).size(); char firstMove = '-';

            for (int p = 0; p <= 3; ++p)
            {
                int tmpEneY = eneY + dy[p], tmpEneX = eneX + dx[p];
                Mpoint tmpEneP = new Mpoint(tmpEneY, tmpEneX);

                if (tmpEneP.isVali())
                {
                    if (wmapC[tmpEneY][tmpEneX] != '*')
                    {
                        ArrayList<Character> tmpPath = getPath(plrY, plrX, tmpEneY, tmpEneX);
                        int nowD = tmpPath.size();
                        if (nowD > maxD && nowD > 0)
                        {
                            maxD = nowD;
                            firstMove = allPs[p];
                        }
                    }
                }
            }

            if (firstMove != '-') movEne(firstMove);
        }

        refreshGrid();

        if (turn == 100)
        {
            Sprite Cover
                    = graphicEntityModule.createSprite()
                    .setY(0)
                    .setX(0)
                    .setZIndex(Integer.MAX_VALUE)
                    .setScale(1)
                    .setImage("LOSE_FALL.png")
                    .setVisible(true);
            graphicEntityModule.commitEntityState(1, Cover);

            graphicEntityModule.createRectangle()
                    .setLineWidth(0)
                    .setFillColor(0xd4efdf)
                    .setZIndex(Integer.MAX_VALUE-5)
                    .setWidth(Constants.VIEWER_WIDTH)
                    .setHeight(96)
                    .setAlpha(0.25)
                    .setX(0)
                    .setY(Constants.VIEWER_HEIGHT/2-96/2);

            graphicEntityModule.createText("Turn Limit Exceeded")
                    .setStrokeThickness(2) // Adding an outline
                    .setStrokeColor(0xffffff) // a white outline
                    .setFontSize(75)
                    .setFillColor(0xd1f2eb) // Setting the text color to black
                    .setX(Constants.VIEWER_WIDTH/2,Curve.EASE_IN_AND_OUT)
                    .setY(Constants.VIEWER_HEIGHT/2,Curve.EASE_IN_AND_OUT)
                    .setAnchor(0.5)
                    .setZIndex(Integer.MAX_VALUE);

            graphicEntityModule.commitEntityState(1, Cover);

            gameManager.loseGame("Fall");
            return;
        }
    }
}
