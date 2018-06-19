/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.example.lckt.myairhockeywithimprovedmallets6.objects;

import com.example.lckt.myairhockeywithimprovedmallets6.data.VertexArray;
import com.example.lckt.myairhockeywithimprovedmallets6.programs.ColorShaderProgram;
import com.example.lckt.myairhockeywithimprovedmallets6.util.Geometry;

import java.util.List;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;
import static com.example.lckt.myairhockeywithimprovedmallets6.Constants.BYTES_PER_FLOAT;

//public class Mallet {
//    private static final int POSITION_COMPONENT_COUNT = 2;
//    private static final int COLOR_COMPONENT_COUNT = 3;
//    private static final int STRIDE =
//        (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT)
//        * BYTES_PER_FLOAT;
//    private static final float[] VERTEX_DATA = {
//        // Order of coordinates: X, Y, R, G, B
//        0f, -0.4f, 0f, 0f, 1f,
//        0f,  0.4f, 1f, 0f, 0f };
//    private final VertexArray vertexArray;
//
//    public Mallet() {
//        vertexArray = new VertexArray(VERTEX_DATA);
//    }
//
//    public void bindData(ColorShaderProgram colorProgram) {
//        vertexArray.setVertexAttribPointer(
//                0,
//                colorProgram.getPositionAttributeLocation(),
//                POSITION_COMPONENT_COUNT,
//                STRIDE
//        );
//        vertexArray.setVertexAttribPointer(
//                POSITION_COMPONENT_COUNT,
//                colorProgram.getColorAttributeLocation(),
//                COLOR_COMPONENT_COUNT,
//                STRIDE
//        );
//    }
//
//    public void draw() {
//        glDrawArrays(GL_POINTS, 0, 2);
//    }
//}

public class Mallet {
    private static final int POSITION_COMPONENT_COUNT = 3;

    public final float radius;
    public final float height;

    private final VertexArray vertexArray;
    private final List<ObjectBuilder.DrawCommand> drawList;

    public Mallet(float radius, float height, int numPointsAroundMallet) {
        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createMallet(new Geometry.Point(0f,
                0f, 0f), radius, height, numPointsAroundMallet);

        this.radius = radius;
        this.height = height;

        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }
    public void bindData(ColorShaderProgram colorProgram) {
        vertexArray.setVertexAttribPointer(0,
                colorProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, 0);
    }
    public void draw() {
        for (ObjectBuilder.DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }
}