/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 ***/
package com.example.lckt.myairhockeywithimprovedmallets6;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import com.example.lckt.myairhockeywithimprovedmallets6.objects.Mallet;
import com.example.lckt.myairhockeywithimprovedmallets6.objects.Puck;
import com.example.lckt.myairhockeywithimprovedmallets6.objects.Table;
import com.example.lckt.myairhockeywithimprovedmallets6.programs.ColorShaderProgram;
import com.example.lckt.myairhockeywithimprovedmallets6.programs.TextureShaderProgram;
import com.example.lckt.myairhockeywithimprovedmallets6.util.MatrixHelper;
import com.example.lckt.myairhockeywithimprovedmallets6.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;

public class MyRenderer implements Renderer {
    private final Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];

    private Table table;
    private Mallet mallet;
    private Puck puck;

    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;

    private int texture;

    public MyRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        table = new Table();
        mallet = new Mallet(0.08f, 0.15f, 32);
        puck = new Puck(0.2f, 0.02f, 6 );

        textureProgram = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram(context);

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width
                / (float) height, 1f, 10f);

//        setIdentityM(modelMatrix, 0);
//        translateM(modelMatrix, 0, 0f, 0f, -2.5f);
//        rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);
//
//        final float[] temp = new float[16];
//        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
//        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);

        setLookAtM(viewMatrix,
                0,
                0, 1.2f, 2.2f,
                0f, 0f, 0f,
                0f, 1f, 0f);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);

        // Multiply the view and projection matrices together
        multiplyMM(viewProjectionMatrix,
                0, projectionMatrix, 0, viewMatrix, 0);

        // Draw the table.
        positionTableInScene();
        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        // Draw the mallets.
        positionObjectInScene(0f, mallet.height / 2f, -0.4f);
        colorProgram.useProgram();
        colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f);
        mallet.bindData(colorProgram);
        mallet.draw();

        positionObjectInScene(0f, mallet.height / 2f, 0.4f);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
        mallet.draw();

        // Draw the puck
        positionObjectInScene(0f, puck.height / 2f, 0f);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.0f, 1f, 0f);
        puck.bindData(colorProgram);
        puck.draw();
    }

    private void positionTableInScene() {
        // The table is defined in terms of X & Y coordinates, so we rotate it
        // 90 degrees to lie flat on the XZ plane.
        setIdentityM(modelMatrix, 0);
        rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
                0, modelMatrix, 0);
    }

    // The mallets and the puck are positioned on the same plane as the table.
    private void positionObjectInScene(float x, float y, float z) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
                0, modelMatrix, 0);
    }
}