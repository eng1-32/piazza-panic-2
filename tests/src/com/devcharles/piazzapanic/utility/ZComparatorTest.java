package com.devcharles.piazzapanic.utility;


import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.devcharles.piazzapanic.GdxTestRunner;
import com.devcharles.piazzapanic.components.TransformComponent;
import org.junit.Test;
import org.junit.runner.RunWith;
import sun.font.TrueTypeFont;

import static org.junit.Assert.*;

@RunWith(GdxTestRunner.class)
public class ZComparatorTest {
    @Test
    public void EqualityTest(){

        World world = new World(new Vector2(0,0), true);
        PooledEngine engine = new PooledEngine();
        TransformComponent transform = engine.createComponent(TransformComponent.class);

        Entity a = new Entity();
        Entity b = new Entity();

        transform.position.set(new Vector3(0,0,1.0f));
        a.add(transform);
        b.add(transform);

        ZComparator ZComparator = new ZComparator();

        assertEquals("Checks that the Z coordinates are equal", Math.round(Mappers.transform.get(a).position.z),
                Math.round(Mappers.transform.get(b).position.z));

        assertEquals("Checks that the ZComparator returns a 0 when the coordinates are equal",
                0, ZComparator.compare(a,b));

    }

    @Test
    public void NotEqualTest(){
        World world = new World(new Vector2(0,0), true);
        PooledEngine engine = new PooledEngine();
        TransformComponent transform1 = engine.createComponent(TransformComponent.class);
        TransformComponent transform2 = engine.createComponent(TransformComponent.class);

        Entity a = new Entity();
        Entity b = new Entity();
        Entity c = new Entity();
        Entity d = new Entity();

        transform1.position.set(new Vector3(0,0,1));
        transform2.position.set(new Vector3(0,0,2));

        a.add(transform1);
        b.add(transform2);
        c.add(transform2);
        d.add(transform1);

        ZComparator ZComparator = new ZComparator();

        assertEquals("A 1 should be returned if an entity's Z coordinate is less than the other's",
                1, ZComparator.compare(a,b));
        assertEquals("A -1 is returned otherwise", -1, ZComparator.compare(c,d));



    }
}

