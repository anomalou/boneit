package org.anomalou.model.scene;

import lombok.Getter;
import lombok.Setter;
import org.anomalou.annotation.Editable;
import org.anomalou.annotation.EditorType;
import org.anomalou.model.FPoint;

import java.awt.*;

/**
 * Default scene object that can applies various transformation to itself
 */
public class TransformObject extends SceneObject {
    //TODO replace to Vector2D
    /**
     * Source of normal vector of the bone.
     */
    @Editable(name = "Root vector source", description = "Position in pixels of beginning of the direction vector", editorType = EditorType.VECTOR_EDITOR)
    @Getter
    @Setter
    protected Point rootVectorOrigin;
    /**
     * Direction of normal vector of the bone.
     */
    @Editable(name = "Root vector direction", description = "Position in pixels of end of the direction vector", editorType = EditorType.VECTOR_EDITOR)
    @Getter
    @Setter
    protected Point rootVectorDirection;
    /**
     * Angle of the bone in radian.
     */
    @Editable(name = "Rotation angle", description = "Rotation angle of the direction vector in radian", editorType = EditorType.TEXT_FIELD)
    @Getter
    @Setter
    protected Double rotationAngle;
    /**
     * Sum of angles of all parents of the bone in radian.
     */
    @Getter
    @Setter
    protected Double parentRotationAngle;

    public TransformObject() {
        super();

        name = "Transform object";
        rootVectorOrigin = new Point();
        rootVectorDirection = new Point();
        rotationAngle = 0d;
        parentRotationAngle = 0d;
    }

    public Double getFullRotationAngle() {
        return rotationAngle + parentRotationAngle;
    }

    public void applyTransformation() {
//        logger.info("I'm not implemented! :3"); //TODO temporary
    }

    @Override
    public FPoint getGlobalPosition() {
        FPoint rotatedVector = calculateRotationVectorForAngle(localPosition, parentRotationAngle);
        return new FPoint(rotatedVector.x + originPosition.x, rotatedVector.y + originPosition.y);
    }

    /**
     * Get angle between direction and rootDirectionPosition vectors, as it began in (0, 0)
     *
     * @return double
     */
    public double calculateRotationAngle(FPoint direction) {
        FPoint normalizedRootDirectionVector = normalizeSourceVector();

        double side = (direction.x) * (normalizedRootDirectionVector.y) - (direction.y) * (normalizedRootDirectionVector.x);

        side = side <= 0 ? 1 : -1;

        double cos = (direction.x * normalizedRootDirectionVector.x + direction.y * normalizedRootDirectionVector.y) /
                (Math.sqrt(Math.pow(direction.x, 2) + Math.pow(direction.y, 2)) * Math.sqrt(Math.pow(normalizedRootDirectionVector.x, 2) + Math.pow(normalizedRootDirectionVector.y, 2)));
        cos = Math.abs(cos) > 1d ? 1d : cos;

        double resultAngle = Math.acos(cos) * side;
        if (Double.isNaN(resultAngle))
            resultAngle = 0d;

        setRotationAngle(resultAngle);

        return resultAngle;
    }

    /**
     * Normalize rootDirection vector. Move it to (0; 0) coordinates.
     *
     * @return FPoint normalized vector
     */
    public FPoint normalizeSourceVector() {
        return new FPoint(getRootVectorDirection().x - getRootVectorOrigin().x, (getRootVectorDirection().y - getRootVectorOrigin().y) * -1);
    }

    public FPoint calculateRotationVector() {
        return calculateRotationVectorForAngle(normalizeSourceVector(), getRotationAngle());
    }

    /**
     * New vector, result of rotation rootDirection vector. Returns vector that start from (0; 0) coordinates.
     *
     * @return FPoint vector
     */
    public FPoint calculateParentRotationVector() {
        return calculateRotationVectorForAngle(normalizeSourceVector(), getParentRotationAngle());
    }

    public FPoint calculateFullRotationVector() {
        return calculateRotationVectorForAngle(normalizeSourceVector(), getRotationAngle() + getParentRotationAngle());
    }

    /**
     * Calculate new vector like if vector (begins in (0, 0)) would be rotated to some angle
     *
     * @param vector vector to rotate (source in (0,0))
     * @param angle  angle to rotate
     * @return FPoint
     */
    public FPoint calculateRotationVectorForAngle(FPoint vector, Double angle) {
        FPoint rotatedVector = new FPoint(vector.x * Math.cos(angle) - vector.y * Math.sin(angle),
                vector.x * Math.sin(angle) + vector.y * Math.cos(angle));

        rotatedVector.y *= -1;

        return rotatedVector;
    }

    public FPoint calculateRotationVectorForAngle(Point vector, Double angle) {
        return calculateRotationVectorForAngle(new FPoint(vector), angle);
    }

    /**
     * Check if point is hit in bound of object (object, bone etc)
     *
     * @param point point to check
     * @return boolean
     */
    public boolean isInBounds(Point point) {
        return isInRectangle(point, new Rectangle((int) getGlobalPosition().x, (int) getGlobalPosition().y, rootVectorDirection.x, rootVectorDirection.x));
    }

    protected boolean isInRectangle(Point point, Rectangle rectangle) {
        if (point.x >= rectangle.x && point.x < (rectangle.x + rectangle.width)) {
            return point.y >= rectangle.y && point.y < (rectangle.y + rectangle.height);
        }

        return false;
    }
}
