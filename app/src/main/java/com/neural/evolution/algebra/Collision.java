package com.neural.evolution.algebra;

import com.graphics.glcanvas.engine.structures.Line;

public class Collision {


    public static float detect_line_collision(float startAx,float startAy,float stopAx,float stopAy,
                                       float startBx,float startBy,float stopBx,float stopBy){
        float ua=0f;
        float ub;
        float ud=(stopBy-startBy)*(stopAx-startAx)-(stopBx-startBx)*(stopAy-startAy);


        if(ud!=0){
            ua=((stopBx-startBx)*(startAy-startBy)-(stopBy-startBy)*(startAx-startBx))/ud;
            ub=((stopAx-startAx)*(startAy-startBy)-(stopAy-startAy)*(startAx-startBx))/ud;

            if(ua<0.0f||ua>1.0f||ub<0.0f||ub>1.0f)ua=0.0f;
        }

        return ua;
    }

    public static float detect_line_collision(Line a,Line b){


        return detect_line_collision(a.getStartX(),a.getStartY(),a.getStopX(),a.getStopY(),
                     b.getStartX(),b.getStartY(),b.getStopX(),b.getStopY());
    }

    public static boolean do_lines_intersect(float startAx,float startAy,float stopAx,float stopAy,
                                     float startBx,float startBy,float stopBx,float stopBy){
        return do_lines_intersect(detect_line_collision(startAx, startAy, stopAx, stopAy, startBx, startBy, stopBx, stopBy));
    }

    public static boolean do_lines_intersect(float determinant){
        return determinant>0;
    }

    public static void setInterSectionPoint(float determinant, Line line){
        float px1=line.getStartX();
        float px2=line.getStopX();
        float py1=line.getStartY();
        float py2=line.getStopY();
        line.setStopX(px1+determinant*(px2-px1));
        line.setStopY(py1+determinant*(py2-py1));
    }


}
