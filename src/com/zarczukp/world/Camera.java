package com.zarczukp.world;

import com.sun.scenario.effect.impl.prism.ps.PPStoPSWDisplacementMapPeer;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.*;

import static org.apache.commons.math3.linear.MatrixUtils.createRealIdentityMatrix;
import static org.apache.commons.math3.util.FastMath.*;

public class Camera {


    RealVector at;
    RealVector up;
    static private double pi = 3.1419;
    public RealVector position;
    public RealMatrix rotationCameraMatrix;
    public RealMatrix perspectiveMatrix;
    public RealMatrix cameraMatrix;
    public RealMatrix scallingMatrix;
    public double scala;
    public int width;
    public int height;
    public double aspect;
    public double fov;
    public double zfar;
    public double znear;
    public double focalLength;


    public Camera(int width, int height) {
        this.position = new ArrayRealVector(new double[]{0, 0, 8});
        this.rotationCameraMatrix = MatrixUtils.createRealIdentityMatrix(4);
        this.at = new ArrayRealVector(new double[]{0, 0, 1});
        this.up = new ArrayRealVector(new double[]{0, 1, 0});
        this.width = width;
        this.height = height;
        this.aspect = (double) height / (double) width;
        this.fov = toRadians(90.0);
        this.zfar = 1000.0;
        this.znear = 1.0;
        this.focalLength = 1.0;
        this.scala = 1.0;
        recalculatePerspectiveMatrix();
        recaluclateCameraMatrix();
        recaluclateScalling();
    }

    private void recaluclateScalling() {
        RealMatrix m = MatrixUtils.createRealMatrix(4, 4);
        m.setEntry(0, 0, this.scala);
        m.setEntry(1, 1, this.scala);
        m.setEntry(2, 2, 1.0);
        m.setEntry(3, 3, 1.0);
        this.scallingMatrix = m;

    }


    public RealVector projectToScreen(RealVector point) {
        RealVector projected = point.copy();
        projected = perspectiveChange(projected);
        projected = scallingMatrix.operate(projected);

        /*
        if(projected.getEntry(projected.getMaxIndex())!=0){
    projected.mapDivideToSelf(projected.getEntry(projected.getMaxIndex()));
        }
        projected.setEntry(0,projected.getEntry(0)+1.0f);
        projected.setEntry(1,projected.getEntry(1)+1.0f);
        projected.setEntry(0,Math.round(projected.getEntry(0)*0.5d*width));
        projected.setEntry(1,Math.round(projected.getEntry(1)*0.5d*height));
        */
        return projected;
    }

    private RealVector perspectiveChange(RealVector point) {


        RealVector camered = this.cameraChange(point);
        RealVector rolling = this.rotationCameraMatrix.operate(camered);
        RealVector perspectived = this.perspectiveMatrix.operate(rolling);
        return perspectived;
    }

    private RealVector cameraChange(RealVector point) {
        RealVector translated = this.cameraMatrix.operate(point);
        return translated;
    }

    private void recalculatePerspectiveMatrix() {
        RealMatrix tmpPerspective = MatrixUtils.createRealMatrix(4, 4);
       /* double f = 1/tan(this.fov*0.5);
        double q = this.zfar/(this.zfar-this.znear);

        tmpPerspective.setEntry(0,0,(f*this.aspect));
        tmpPerspective.setEntry(1,1,f);
        tmpPerspective.setEntry(2,2,q);
        tmpPerspective.setEntry(2,3,1.0);
        tmpPerspective.setEntry(3,2,((-1)*this.zfar)*q);
        */

       /*
        double f = 1/tan(this.fov*0.5);
        double a = (zfar+znear)/(znear-zfar);
        double b = (2.0*zfar*znear)/(znear-zfar);


        this.perspectiveMatrix = tmpPerspective;
        tmpPerspective.setEntry(0,0,(1.0/aspect)*f);
        tmpPerspective.setEntry(1,1,f);
        tmpPerspective.setEntry(2,2,a);
        tmpPerspective.setEntry(2,3,b);
        tmpPerspective.setEntry(3,2,-1.0);
        System.out.println(tmpPerspective);
        */

        double f = 1 / tan(this.fov * 0.5);
        double a = (-znear - zfar) / (znear - zfar);
        double b = (2.0 * zfar * znear) / (znear - zfar);


        this.perspectiveMatrix = tmpPerspective;
        tmpPerspective.setEntry(0, 0, (1.0 / aspect) * f);
        tmpPerspective.setEntry(1, 1, f);
        tmpPerspective.setEntry(2, 2, a);
        tmpPerspective.setEntry(2, 3, b);
        tmpPerspective.setEntry(3, 2, 1.0);

        this.perspectiveMatrix = tmpPerspective;


    }

    private void recaluclateCameraMatrix() {
      /*

        RealMatrix tmpCamera = MatrixUtils.createRealMatrix(4,4);

        tmpCamera.setSubMatrix(this.rotationCameraMatrix.getData(),0,0);
        RealVector t = new ArrayRealVector(4);
        tmpCamera.setColumnVector(3,t);
        tmpCamera.setEntry(0,3,this.position.getEntry(0));
        tmpCamera.setEntry(1,3,this.position.getEntry(1));
        tmpCamera.setEntry(2,3,this.position.getEntry(2));
        tmpCamera.setEntry(3,3,1.0);


        System.out.println(tmpCamera);



             this.cameraMatrix = tmpCamera;
*/
        RealMatrix rotation = MatrixUtils.createRealIdentityMatrix(4);
        RealMatrix translated = MatrixUtils.createRealIdentityMatrix(4);

        Vector3D zaxis = new Vector3D(this.position.subtract(at).toArray()).normalize();
        Vector3D xaxis = Vector3D.crossProduct(new Vector3D(this.up.toArray()), zaxis).normalize();
        Vector3D yaxis = Vector3D.crossProduct(zaxis, xaxis);

        rotation.setEntry(0, 0, xaxis.getX());
        rotation.setEntry(0, 1, yaxis.getX());
        rotation.setEntry(0, 2, zaxis.getX());
        rotation.setEntry(1, 0, xaxis.getY());
        rotation.setEntry(1, 1, yaxis.getY());
        rotation.setEntry(1, 2, zaxis.getY());
        rotation.setEntry(2, 0, xaxis.getZ());
        rotation.setEntry(2, 1, yaxis.getZ());
        rotation.setEntry(2, 2, zaxis.getZ());

        translated.setEntry(0, 3, -this.position.getEntry(0));
        translated.setEntry(1, 3, -this.position.getEntry(1));
        translated.setEntry(2, 3, -this.position.getEntry(2));

        System.out.println(translated);
        this.cameraMatrix = rotation.multiply(translated);
    }


    private double toRadians(double angle) {
        return angle * pi / 180;
    }


    public void setDimension(int width, int height) {
        this.width = width;
        this.height = height;
        this.aspect = (double) height / (double) width;
        recalculatePerspectiveMatrix();

    }


    public void translatePosition(RealVector v) {
        this.up = this.up.add(v);
        this.position = this.position.add(v);
        recaluclateCameraMatrix();
    }

    public void moveForward(double step) {
        RealVector forwardvector = this.at.subtract(position);
        this.position = this.position.add(forwardvector.mapMultiply(step));
        this.at = this.at.add(forwardvector.mapMultiply(step));
        recaluclateCameraMatrix();
    }

    public void moveUp(double step) {
        RealVector upvector = this.up;
        this.position = this.position.add(upvector.mapMultiply(step));
        this.at = this.at.add(upvector.mapMultiply(step));
        recaluclateCameraMatrix();
    }

    public void moveRight(double step) {
        Vector3D forwardvector = new Vector3D(this.at.subtract(position).toArray());
        Vector3D upVector = new Vector3D(this.up.toArray());
        RealVector right = new ArrayRealVector(Vector3D.crossProduct(upVector, forwardvector).toArray());
        this.position = this.position.add(right.mapMultiply(step));
        this.at = this.at.add(right.mapMultiply(step));
        recaluclateCameraMatrix();
    }

    public void yaw(double angle) {
        double cs = cos(toRadians(angle));
        double si = sin(toRadians(angle));
        RealMatrix yawMatrix = MatrixUtils.createRealIdentityMatrix(4);
        yawMatrix.setEntry(0, 0, cs);
        yawMatrix.setEntry(0, 2, si);
        yawMatrix.setEntry(2, 0, -si);
        yawMatrix.setEntry(2, 2, cs);
        yawMatrix.setEntry(1, 1, 1.0);
        //this.rotationCameraMatrix = this.rotationCameraMatrix.multiply(yawMatrix);
        this.rotationCameraMatrix = yawMatrix.multiply(this.rotationCameraMatrix);
        recaluclateCameraMatrix();
    }

    public void pitch(double angle) {

        double cs = cos(toRadians(angle));
        double si = sin(toRadians(angle));
        RealMatrix yawMatrix = MatrixUtils.createRealIdentityMatrix(4);
        yawMatrix.setEntry(0, 0, cs);
        yawMatrix.setEntry(1, 0, si);
        yawMatrix.setEntry(0, 1, -si);
        yawMatrix.setEntry(1, 1, cs);
        yawMatrix.setEntry(2, 2, 1.0);
        yawMatrix = yawMatrix.transpose();
        // this.rotationCameraMatrix = this.rotationCameraMatrix.multiply(yawMatrix);
        this.rotationCameraMatrix = yawMatrix.multiply(this.rotationCameraMatrix);
        recaluclateCameraMatrix();
    }

    public void Roll(double angle) {

        double cs = cos(toRadians(angle));
        double si = sin(toRadians(angle));
        RealMatrix yawMatrix = MatrixUtils.createRealIdentityMatrix(4);
        yawMatrix.setEntry(1, 1, cs);
        yawMatrix.setEntry(2, 1, si);
        yawMatrix.setEntry(1, 2, -si);
        yawMatrix.setEntry(2, 2, cs);
        yawMatrix.setEntry(0, 0, 1.0);
        yawMatrix = yawMatrix.transpose();
        this.rotationCameraMatrix = this.rotationCameraMatrix.multiply(yawMatrix);
        //this.rotationCameraMatrix = yawMatrix.multiply(this.rotationCameraMatrix);
        recaluclateCameraMatrix();
    }

    public void zoom(double v) {
        this.scala += v;
        if (this.scala < 0) {
            this.scala = 0;
        }
        recaluclateScalling();
    }
}
