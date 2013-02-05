/* Copyright (c) 2011 Danish Maritime Authority
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.dma.epd.common.math;

/**
 * 2D vector class
 */
public class Vector2D {
    
    private double x1, y1, x2, y2, dx, dy;
    
    public Vector2D(){
        
    }
    
    /**
     * The command Vector2D takes two double valued arguments and constructs the vector.
     * @param dx Value of vectors x-component
     * @param dy Value of vectors y-component
     */
    public Vector2D (double x1, double y1, double x2, double y2)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.dx = x2 - x1;
        this.dy = y2 - y1;
    }
    
    /**
     * Copy constructor
     * @param vector
     */
    public Vector2D(Vector2D vector){
        this.x1 = vector.x1;
        this.y1 = vector.y1;
        this.x2 = vector.x2;
        this.y2 = vector.y2;
        this.dx = vector.dx;
        this.dy = vector.dy;
    }
    
    /**
     * Adds one vector to another.
     * This is a command since changes the state of the instantiated object.
     * @param other Vector to be added to
     */
    public void add(Vector2D other)
    {
        x1 = this.x1 + other.x1;
        y1 = this.y1 + other.y1;
        x2 = this.x2 + other.x2;
        y2 = this.y2 + other.y2;
    }
    
    /**
     * Subtracts one vector from another.
     * This is a command since changes the state of the instantiated object.
     * @param other Vector to subtract from
     */
    public void subtract(Vector2D other)
    {
        x1 = this.x1 - other.x1;
        y1 = this.y1 - other.y1;
        x2 = this.x2 - other.x2;
        y2 = this.y2 - other.y2;
    }
    
    /**
     * Produces the dot-product of two vectors.
     * This is a query.It performs a simple operation on the object and returns a value.
     * @param other Second vector to dot with
     * @return Returns the dot product
     */
    public double dot(Vector2D other) {
        return this.dx * other.dx + this.dy * other.dy;
    }
    
    public static double dot(Vector2D vector1, Vector2D vector2) {
        return vector1.dx * vector2.dx + vector1.dy * vector2.dy;
    }
    
    public Vector2D unit(){
        Vector2D newVector = new Vector2D(this);
        double scale = newVector.norm();
        newVector.setX2(x2/scale);
        newVector.setY2(y2/scale);
//        this.x2 /= scale;
//        this.y2 /= scale;
        this.dx = x2 - x1;
        this.dy = y2 - y1;
        return newVector;
    }
    
    /**
     * Scales the input vector.
     * This is a command since it changes the state of the instantiated object.
     * @param scalar The amount of scaling done to the vector
     */
    public void scale(double scalar) {
        dx = this.dx * scalar;
        dy = this.dy * scalar;
    }
    
    /**
     * Returns the length of input vector.
     * This is a query. It performs a simple operation and returns a value.
     * @return Length of the input vector
     */
    public double norm() {
        return Math.sqrt(Math.pow(dx, 2)+Math.pow(dy, 2));
    }
    
    /**
     * Calculates the projection of a vector (or point) onto this vector
     * @param vector The vector or point to project onto this vector
     * @return The vector projected onto this vector
     */
    public Vector2D projection(Vector2D vector){
        double scale = dot(vector, this)/dot(this,this);
        Vector2D result = new Vector2D(this);
        result.x2 = result.x1 + scale * result.dx;
        result.y2 = result.y1 + scale * result.dy;
        result.dx = result.x2 - result.x1;
        result.dy = result.y2 - result.y1;
        return result;
    }
    
    private void recalc(){
        dx = x2 - x1;
        dy = y2 - y1;
    }
    
    /**
     * Prints input vector to screen.
     * This is a query since it returns a string
     */
    @Override
    public String toString()
    {
        return "Vector2D: x1: " + x1 + "; y1: " + y1 + "\n x2:" + x2 + "; y2:" + y2;
    }
    
    public void setValues(double x1, double y1, double x2, double y2){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        recalc();
    }
    
    public double getX1() {
        return x1;
    }

    public void setX1(double x1) {
        this.x1 = x1;
        recalc();
    }

    public double getY1() {
        return y1;
    }

    public void setY1(double y1) {
        this.y1 = y1;
        recalc();
    }

    public double getX2() {
        return x2;
    }

    public void setX2(double x2) {
        this.x2 = x2;
        recalc();
    }

    public double getY2() {
        return y2;
    }

    public void setY2(double y2) {
        this.y2 = y2;
        recalc();
    }
}
