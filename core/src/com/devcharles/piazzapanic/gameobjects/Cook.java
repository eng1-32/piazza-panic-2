package com.devcharles.piazzapanic.gameobjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Cook extends Entity{

    public Cook(World world, float x, float y) {
        super(new Texture("droplet.png"));

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.linearDamping = 2f;
        bodyDef.fixedRotation = true;
        bodyDef.awake = true;

        bodyDef.position.set(x, y);

        this.body = world.createBody(bodyDef);

        // Create a circle shape and set its radius to 1
        CircleShape circle = new CircleShape();
        circle.setRadius(0.75f);

        // Create a fixture definition to apply our shape to
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 20f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f; // Make it bounce a little bit

        // Create our fixture and attach it to the body
        body.createFixture(fixtureDef);

        // BodyDef and FixtureDef don't need disposing, but shapes do.
        circle.dispose();
    }
}
