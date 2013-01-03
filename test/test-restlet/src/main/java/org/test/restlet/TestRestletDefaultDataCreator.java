package org.test.restlet;

import com.tinkerpop.blueprints.TransactionalGraph;
import org.tuml.runtime.adaptor.DefaultDataCreator;
import org.tuml.runtime.adaptor.GraphDb;
import org.tuml.test.*;

/**
 * Date: 2012/12/31
 * Time: 3:30 PM
 */
public class TestRestletDefaultDataCreator implements DefaultDataCreator {

    public void createData() {
        for (int i = 0; i < 2; i++) {
            Human human = new Human(true);
            human.setName("human1" + i);
            human.setName2("human2" + i);
            human.setGender(Gender.MALE);
            Home home = new Home(human);
            home.setName("home" + i);
            ComponentMany componentMany = new ComponentMany(human);
            componentMany.setName("componentMany" + i);

            ComponentManyDeep1 componentManyDeep1 = new ComponentManyDeep1(human);
            componentManyDeep1.setName("componentManyDeep1" + i);

            ComponentManyDeep2 componentManyDeep2 = new ComponentManyDeep2(componentManyDeep1);
            componentManyDeep2.setName("componentManyDeep2" + i);

            ComponentOneDeep3 componentOneDeep3 = new ComponentOneDeep3(componentManyDeep2);
            componentOneDeep3.setName("componentOneDeep3" + i);

            for (int j = 0; j < 2; j++) {
                Many1 many1 = new Many1(human);
                many1.setName("many1" + j);
                Many2 many2 = new Many2(human);
                many2.setName("many2" + j);
                One one = new One(human);
                one.setName("one" + j);
                many1.addToMany2(many2);
                if (j % 2 == 1) {
                    many1.addToOne(one);
                }
            }

            for (int j = 0; j < 2; j++) {
                Hand hand = new Hand(human);
                hand.setName("hand" + j);
                hand.setTestNumber(50);
                hand.setTestBoolean(false);
                hand.setTestUnlimitedNatural(1 + j * 10000000L);

                AnotherOne1 anotherOne1 = new AnotherOne1(hand);
                anotherOne1.setName("anotherOne1" + j);

                AnotherOne2 anotherOne2 = new AnotherOne2(anotherOne1);
                anotherOne2.setName("anotherOne2" + j);

                AnotherMany1 anotherMany1 = new AnotherMany1(anotherOne2);
                anotherMany1.setName("anotherMany1" + j);

                for (int k = 0; k < 5; k++) {
                    Finger finger = new Finger(hand);
                    finger.setName("finger" + k);
                    finger.addToManyInteger(1);
                    finger.addToManyInteger(2);
                    finger.addToManyInteger(3);

                    finger.addToManyRequiredInteger(6);

                    Ring ring = new Ring(human);
                    ring.setName("ring" + i + j + k);
                    finger.setRing(ring);
                }
                Ring ring = new Ring(human);
                ring.setName("ringExtra" + i + j);
            }
        }

        for (int i = 0; i < 2; i++) {
            Alien alien = new Alien(true);
            alien.setName("alien" + i);
            for (int j = 0; j < 2; j++) {
                SpaceCraft spaceCraft = new SpaceCraft(alien);
                spaceCraft.setName("spaceCraftShip" + j);
                spaceCraft.setIntergalactic(true);

                TerrestrialCraft terrestrialCraft = new TerrestrialCraft(alien);
                terrestrialCraft.setName("terrestrialCraftShip" + j);
                terrestrialCraft.setAquatic(true);
            }
        }
        GraphDb.getDb().stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
    }
}