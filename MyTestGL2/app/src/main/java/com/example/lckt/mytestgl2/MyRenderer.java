/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 ***/
package com.example.lckt.mytestgl2;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import com.example.lckt.mytestgl2.util.LoggerConfig;
import com.example.lckt.mytestgl2.util.ShaderHelper;
import com.example.lckt.mytestgl2.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.orthoM;


public class MyRenderer implements Renderer {
    private static final String U_MATRIX = "u_Matrix";

    private static final String A_POSITION = "a_Position";
    private static final String A_COLOR = "a_Color";

    /*
    private static final int POSITION_COMPONENT_COUNT = 4;
    */
    private static final int POSITION_COMPONENT_COUNT = 2;

    private static final int COLOR_COMPONENT_COUNT = 3;

    private static final int BYTES_PER_FLOAT = 4;

    private static final int STRIDE =
            (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private final FloatBuffer vertexData;
    private final Context context;

    private final float[] projectionMatrix = new float[16];

    private final float[] modelMatrix = new float[16];

    private int program;
    private int uMatrixLocation;
    private int aPositionLocation;
    private int aColorLocation;

    public MyRenderer(Context context) {
        this.context = context;

        // shape 1
        final float offsetx1 = -0.9f;
        final float offsety1 = -0.9f;
        final float offsetx2 = -0.75f;
        final float offsety2 = -0.875f;
        final float offsetx3 = -0.6f;
        final float offsety3 = -0.9f;

        // shape 2
        final float offsetx21 = offsetx1 + 0.5f; //-0.4f;
        final float offsety21 = -0.9f;
        final float offsetx22 = offsetx21 + 0.15f;
        final float offsety22 = -0.875f;
        final float offsetx23 = offsetx21 + 0.3f;
        final float offsety23 = -0.9f;

        // shape 3
        final float offsetx31 = offsetx21 + 0.5f;
        final float offsety31 = -0.9f;
        final float offsetx32 = offsetx31 + 0.15f;
        final float offsety32 = -0.875f;
        final float offsetx33 = offsetx31 + 0.3f;
        final float offsety33 = -0.9f;

        // shape 4
        final float offsetx41 = offsetx31 + 0.5f;
        final float offsety41 = -0.9f;
        final float offsetx42 = offsetx41 + 0.15f;
        final float offsety42 = -0.875f;
        final float offsetx43 = offsetx41 + 0.3f;
        final float offsety43 = -0.9f;

        float[] tableVerticesWithTriangles = {
                // Order of coordinates: X, Y, R, G, B

//                // Base Triangle Fan
//                0f + offset1,    0f + offset1,   1f,   1f,   1f,
//                -0.05f + offset1, -0.05f + offset1, 0.7f, 0.7f, 0.7f,
//                0.05f + offset1, -0.05f + offset1, 0.7f, 0.7f, 0.7f,
//                0.05f + offset1,  0.05f + offset1, 0.7f, 0.7f, 0.7f,
//                -0.05f + offset1,  0.05f + offset1, 0.7f, 0.7f, 0.7f,
//                -0.05f + offset1, -0.05f + offset1, 0.7f, 0.7f, 0.7f,

                // Shape 1
                // Triangle Fan
                0f + offsetx1,    0f + offsety1,   1f,   0f,   0f,
                (-0.05f/2f) + offsetx1, -0.05f + offsety1, 1f,   0f,   0f,
                (0.05f/2f) + offsetx1, -0.05f + offsety1, 1f,   0f,   0f,
                (0.05f/2f) + offsetx1,  0.05f + offsety1, 1f,   0f,   0f,
                (-0.05f/2f) + offsetx1,  0.05f + offsety1, 1f,   0f,   0f,
                (-0.05f/2f) + offsetx1, -0.05f + offsety1, 1f,   0f,   0f,

                // Triangle Fan long
                0f + offsetx2,    0f + offsety2,   1f,   0f,   0f,
                -0.1f + offsetx2, (-0.05f/2f) + offsety2, 1f,   0f,   0f,
                0.1f + offsetx2, (-0.05f/2f) + offsety2, 1f,   0f,   0f,
                0.1f + offsetx2,  (0.05f/2f) + offsety2, 1f,   0f,   0f,
                -0.1f + offsetx2,  (0.05f/2f) + offsety2, 1f,   0f,   0f,
                -0.1f + offsetx2, (-0.05f/2f) + offsety2, 1f,   0f,   0f,

                // Triangle Fan
                0f + offsetx3,    0f + offsety3,   1f,   0f,   0f,
                -0.05f + offsetx3, -0.05f + offsety3, 1f,   0f,   0f,
                0.05f + offsetx3, -0.05f + offsety3, 1f,   0f,   0f,
                0.05f + offsetx3,  0.05f + offsety3, 1f,   0f,   0f,
                -0.05f + offsetx3,  0.05f + offsety3, 1f,   0f,   0f,
                -0.05f + offsetx3, -0.05f + offsety3, 1f,   0f,   0f,

                // Shape 2
                // Triangle Fan
                0f + offsetx21,    0f + offsety21,   0f,   1f,   0f,
                -0.05f + offsetx21, -0.05f + offsety21, 0f,   1f,   0f,
                0.05f + offsetx21, -0.05f + offsety21, 0f,   1f,   0f,
                0.05f + offsetx21,  0.05f + offsety21, 0f,   1f,   0f,
                -0.05f + offsetx21,  0.05f + offsety21, 0f,   1f,   0f,
                -0.05f + offsetx21, -0.05f + offsety21, 0f,   1f,   0f,

                // Triangle Fan long
                0f + offsetx22,    0f + offsety22,   0f,   1f,   0f,
                -0.1f + offsetx22, (-0.05f/2f) + offsety22, 0f,   1f,   0f,
                0.1f + offsetx22, (-0.05f/2f) + offsety22, 0f,   1f,   0f,
                0.1f + offsetx22,  (0.05f/2f) + offsety22, 0f,   1f,   0f,
                -0.1f + offsetx22,  (0.05f/2f) + offsety22, 0f,   1f,   0f,
                -0.1f + offsetx22, (-0.05f/2f) + offsety22, 0f,   1f,   0f,

                // Triangle Fan
                0f + offsetx23,    0f + offsety23,   0f,   1f,   0f,
                -0.05f + offsetx23, -0.05f + offsety23, 0f,   1f,   0f,
                0.05f + offsetx23, -0.05f + offsety23, 0f,   1f,   0f,
                0.05f + offsetx23,  0.05f + offsety23, 0f,   1f,   0f,
                -0.05f + offsetx23,  0.05f + offsety23, 0f,   1f,   0f,
                -0.05f + offsetx23, -0.05f + offsety23, 0f,   1f,   0f,

                // Shape 3
                // Triangle Fan
                0f + offsetx31,    0f + offsety31,   0f,   0f,   1f,
                -0.05f + offsetx31, -0.05f + offsety31, 0f,   0f,   1f,
                0.05f + offsetx31, -0.05f + offsety31, 0f,   0f,   1f,
                0.05f + offsetx31,  0.05f + offsety31, 0f,   0f,   1f,
                -0.05f + offsetx31,  0.05f + offsety31, 0f,   0f,   1f,
                -0.05f + offsetx31, -0.05f + offsety31, 0f,   0f,   1f,

                // Triangle Fan long
                0f + offsetx32,    0f + offsety32,   0f,   0f,   1f,
                -0.1f + offsetx32, (-0.05f/2f) + offsety32, 0f,   0f,   1f,
                0.1f + offsetx32, (-0.05f/2f) + offsety32, 0f,   0f,   1f,
                0.1f + offsetx32,  (0.05f/2f) + offsety32, 0f,   0f,   1f,
                -0.1f + offsetx32,  (0.05f/2f) + offsety32, 0f,   0f,   1f,
                -0.1f + offsetx32, (-0.05f/2f) + offsety32, 0f,   0f,   1f,

                // Triangle Fan
                0f + offsetx33,    0f + offsety33,   0f,   0f,   1f,
                -0.05f + offsetx33, -0.05f + offsety33, 0f,   0f,   1f,
                0.05f + offsetx33, -0.05f + offsety33, 0f,   0f,   1f,
                0.05f + offsetx33,  0.05f + offsety33, 0f,   0f,   1f,
                -0.05f + offsetx33,  0.05f + offsety33, 0f,   0f,   1f,
                -0.05f + offsetx33, -0.05f + offsety33, 0f,   0f,   1f,

                // Shape 4
                // Triangle Fan
                0f + offsetx41,    0f + offsety41,   1f,   1f,   0f,
                -0.05f + offsetx41, -0.05f + offsety41, 1f,   1f,   0f,
                0.05f + offsetx41, -0.05f + offsety41, 1f,   1f,   0f,
                0.05f + offsetx41,  0.05f + offsety41, 1f,   1f,   0f,
                -0.05f + offsetx41,  0.05f + offsety41, 1f,   1f,   0f,
                -0.05f + offsetx41, -0.05f + offsety41, 1f,   1f,   0f,

                // Triangle Fan long
                0f + offsetx42,    0f + offsety42,   1f,   1f,   0f,
                -0.1f + offsetx42, (-0.05f/2f) + offsety42, 1f,   1f,   0f,
                0.1f + offsetx42, (-0.05f/2f) + offsety42, 1f,   1f,   0f,
                0.1f + offsetx42,  (0.05f/2f) + offsety42, 1f,   1f,   0f,
                -0.1f + offsetx42,  (0.05f/2f) + offsety42, 1f,   1f,   0f,
                -0.1f + offsetx42, (-0.05f/2f) + offsety42, 1f,   1f,   0f,

                // Triangle Fan
                0f + offsetx43,    0f + offsety43,   1f,   1f,   0f,
                -0.05f + offsetx43, -0.05f + offsety43, 1f,   1f,   0f,
                0.05f + offsetx43, -0.05f + offsety43, 1f,   1f,   0f,
                0.05f + offsetx43,  0.05f + offsety43, 1f,   1f,   0f,
                -0.05f + offsetx43,  0.05f + offsety43, 1f,   1f,   0f,
                -0.05f + offsetx43, -0.05f + offsety43, 1f,   1f,   0f,
        };

        vertexData = ByteBuffer
                .allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        vertexData.put(tableVerticesWithTriangles);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        String vertexShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.simple_fragment_shader);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper
                .compileFragmentShader(fragmentShaderSource);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program);
        }

        glUseProgram(program);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_COLOR);

        // Bind our data, specified by the variable vertexData, to the vertex
        // attribute at location A_POSITION_LOCATION.
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation,
                POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);

        glEnableVertexAttribArray(aPositionLocation);

        // Bind our data, specified by the variable vertexData, to the vertex
        // attribute at location A_COLOR_LOCATION.
        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation,
                COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);

        glEnableVertexAttribArray(aColorLocation);
    }

    /**
     * onSurfaceChanged is called whenever the surface has changed. This is
     * called at least once when the surface is initialized. Keep in mind that
     * Android normally restarts an Activity on rotation, and in that case, the
     * renderer will be destroyed and a new one created.
     *
     * @param width  The new width, in pixels.
     * @param height The new height, in pixels.
     */
    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);

        final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;

        if (width > height) {
            // Landscape
            orthoM(projectionMatrix, 0,
                    -aspectRatio, aspectRatio,
                    -1f, 1f,
                    -1f, 1f);
        } else {
            // Portrait or square
            orthoM(projectionMatrix, 0,
                    -1f, 1f,
                    -aspectRatio, aspectRatio,
                    -1f, 1f);
        }
    }

    /**
     * OnDrawFrame is called whenever a new frame needs to be drawn. Normally,
     * this is done at the refresh rate of the screen.
     */
    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);

        // Draw shape 1
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
        glDrawArrays(GL_TRIANGLE_FAN, 6, 6);
        glDrawArrays(GL_TRIANGLE_FAN, 12, 6);

        // Draw shape 2
        glDrawArrays(GL_TRIANGLE_FAN, 18, 6);
        glDrawArrays(GL_TRIANGLE_FAN, 24, 6);
        glDrawArrays(GL_TRIANGLE_FAN, 30, 6);

        // Draw shape 3
        glDrawArrays(GL_TRIANGLE_FAN, 36, 6);
        glDrawArrays(GL_TRIANGLE_FAN, 42, 6);
        glDrawArrays(GL_TRIANGLE_FAN, 48, 6);

        // Draw shape 4
        glDrawArrays(GL_TRIANGLE_FAN, 54, 6);
        glDrawArrays(GL_TRIANGLE_FAN, 60, 6);
        glDrawArrays(GL_TRIANGLE_FAN, 66, 6);
    }
}